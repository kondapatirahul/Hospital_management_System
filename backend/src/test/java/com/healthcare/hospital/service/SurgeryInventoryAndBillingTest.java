package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SurgeryConsumableDto;
import com.healthcare.hospital.dto.SurgeryReportStatsDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.BatchStatus;
import com.healthcare.hospital.enums.SurgeryPriority;
import com.healthcare.hospital.enums.SurgeryStatus;
import com.healthcare.hospital.enums.TheatreStatus;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurgeryInventoryAndBillingTest {

    // Inventory Mocks
    @Mock private SurgeryConsumableUsageRepository consumableRepository;
    @Mock private SurgeryBookingRepository bookingRepository;
    @Mock private MedicineRepository medicineRepository;
    @Mock private MedicineBatchRepository batchRepository;
    @Mock private StockTransactionRepository transactionRepository;
    @Mock private SurgeryAuditTrailRepository auditTrailRepository;

    // Billing Mocks
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private BillingService billingService;
    @Mock private AuditEventService auditEventService;

    // Report Mocks
    @Mock private OperationTheatreRepository theatreRepository;

    private SurgeryInventoryService inventoryService;
    private SurgeryBillingService surgeryBillingService;
    private SurgeryReportService reportService;

    private SurgeryBooking booking;
    private Medicine medicine;
    private MedicineBatch batch;

    @BeforeEach
    void setUp() {
        inventoryService = new SurgeryInventoryService(
                consumableRepository, bookingRepository, medicineRepository, batchRepository, transactionRepository, auditTrailRepository);

        surgeryBillingService = new SurgeryBillingService(
                bookingRepository, consumableRepository, invoiceRepository, billingService, auditEventService);

        reportService = new SurgeryReportService(theatreRepository, bookingRepository);

        OperationTheatre theatre = new OperationTheatre("OT-01", "Main OT", "Wing A", "GENERAL", 1, null);
        theatre.setId(1L);

        Department dept = new Department("Surgery", "Surg", "knife");
        Doctor doctor = new Doctor("Alexander Wright", "MD", dept, "dr.wright@hospital.com", null, null, BigDecimal.valueOf(100.00), null, null);
        doctor.setId(10L);

        Patient patient = new Patient("PAT-001", null, "Sarah Jenkins", "patient@hospital.com", "555", LocalDate.of(1990, 1, 1), "FEMALE", "A+", null, null, null, null, null);
        patient.setId(100L);

        booking = new SurgeryBooking(
                "SURG-2026-0001", patient, theatre, doctor,
                "Appendectomy", LocalDate.of(2026, 11, 15),
                LocalTime.of(9, 0), 120, SurgeryPriority.ROUTINE, "admin"
        );
        booking.setId(1000L);

        medicine = new Medicine("MED-PROP", "Propofol 1% 20ml", "Propofol", null, "Injectable", "10mg/ml", "Vial", 10, BigDecimal.valueOf(15.00), BigDecimal.valueOf(10.00), "Pharma");
        medicine.setId(50L);
        medicine.setTotalStock(100);

        batch = new MedicineBatch("BAT-001", medicine, null, 100, BigDecimal.valueOf(10.00), BigDecimal.valueOf(15.00), LocalDate.now(), LocalDate.now().plusMonths(12));
        batch.setId(500L);
        batch.setStatus(BatchStatus.ACTIVE);
    }

    @Test
    @DisplayName("Criterion 10: Deducts stock from inventory and logs stock transaction")
    void deductStockForSurgery_Success() {
        SurgeryConsumableUsage usage = new SurgeryConsumableUsage(booking, medicine, batch, "Propofol", 2, BigDecimal.valueOf(15.00), "nurse");
        usage.setId(1L);
        usage.setDeducted(false);

        when(bookingRepository.findById(1000L)).thenReturn(Optional.of(booking));
        when(consumableRepository.findBySurgeryBookingAndIsDeductedFalse(booking)).thenReturn(List.of(usage));
        when(batchRepository.findByIdWithLock(500L)).thenReturn(Optional.of(batch));
        when(batchRepository.calculateValidStockForMedicine(eq(medicine), any())).thenReturn(98);

        int deducted = inventoryService.deductStockForSurgery(1000L, "nurse");

        assertEquals(1, deducted);
        assertEquals(98, batch.getCurrentQuantity());
        assertTrue(usage.isDeducted());
        verify(transactionRepository, times(1)).save(any(StockTransaction.class));
        verify(consumableRepository).save(usage);
    }

    @Test
    @DisplayName("Criterion 10: Duplicate deduction request is prevented (Idempotency)")
    void deductStockForSurgery_DuplicateRequest_Protected() {
        when(bookingRepository.findById(1000L)).thenReturn(Optional.of(booking));
        // All items already deducted!
        when(consumableRepository.findBySurgeryBookingAndIsDeductedFalse(booking)).thenReturn(Collections.emptyList());

        int deducted = inventoryService.deductStockForSurgery(1000L, "nurse");

        assertEquals(0, deducted);
        verify(batchRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterion 10: Prevents negative stock when requested quantity exceeds available")
    void deductStockForSurgery_InsufficientStock_ThrowsException() {
        batch.setCurrentQuantity(1); // Only 1 left
        SurgeryConsumableUsage usage = new SurgeryConsumableUsage(booking, medicine, batch, "Propofol", 5, BigDecimal.valueOf(15.00), "nurse");
        usage.setDeducted(false);

        when(bookingRepository.findById(1000L)).thenReturn(Optional.of(booking));
        when(consumableRepository.findBySurgeryBookingAndIsDeductedFalse(booking)).thenReturn(List.of(usage));
        when(batchRepository.findByIdWithLock(500L)).thenReturn(Optional.of(batch));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> inventoryService.deductStockForSurgery(1000L, "nurse"));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Criterion 11: Calculates surgery charges on backend and creates Invoice")
    void generateSurgeryInvoice_CreatesNewInvoice() {
        when(bookingRepository.findByIdWithLock(1000L)).thenReturn(Optional.of(booking));
        when(consumableRepository.calculateTotalCostForSurgery(booking)).thenReturn(BigDecimal.valueOf(100.00));
        when(billingService.generateNextInvoiceNumber()).thenReturn("INV-2026-0001");
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Invoice invoice = surgeryBillingService.generateSurgeryInvoice(1000L, "billing.staff");

        assertNotNull(invoice);
        assertEquals("INV-2026-0001", invoice.getInvoiceNumber());
        // 120 mins = 2h theatre ($300) + Routine procedure base ($500) + Surgeon ($300) + Anesthesia ($250) + Consumables ($100) = $1450.00 + 5% tax ($72.50) = $1522.50
        assertEquals(new BigDecimal("1450.00"), invoice.getAmount());
        assertEquals(new BigDecimal("72.50"), invoice.getTax());
        assertEquals(new BigDecimal("1522.50"), invoice.getTotalAmount());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Criterion 11: Subsequent billing sync updates existing invoice without duplicate creation")
    void generateSurgeryInvoice_DuplicateSync_UpdatesExistingInvoice() {
        Invoice existingInvoice = new Invoice("INV-2026-0001", booking.getPatient(), LocalDate.now(), null, "Initial", BigDecimal.valueOf(1000), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(1000), null, null, null);
        existingInvoice.setId(99L);
        booking.setInvoice(existingInvoice);

        when(bookingRepository.findByIdWithLock(1000L)).thenReturn(Optional.of(booking));
        when(consumableRepository.calculateTotalCostForSurgery(booking)).thenReturn(BigDecimal.valueOf(100.00));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Invoice synced = surgeryBillingService.generateSurgeryInvoice(1000L, "billing.staff");

        assertEquals(99L, synced.getId()); // Same invoice ID!
        assertEquals("INV-2026-0001", synced.getInvoiceNumber());
        assertEquals(new BigDecimal("1522.50"), synced.getTotalAmount()); // Updated total!
        verify(billingService, never()).generateNextInvoiceNumber(); // Did not generate new invoice number!
    }

    @Test
    @DisplayName("Criterion 12: Report statistics aggregate utilization, delays, and procedure counts")
    void generateStats_CalculatesMetricsCorrectly() {
        OperationTheatre th = booking.getTheatre();
        when(theatreRepository.findAll()).thenReturn(List.of(th));

        booking.setStatus(SurgeryStatus.COMPLETED);
        booking.setActualStartTime(LocalDateTime.of(2026, 11, 15, 9, 20)); // 20 min delay
        when(bookingRepository.findByPlannedDateBetweenOrderByPlannedDateAscPlannedStartTimeAsc(any(), any()))
                .thenReturn(List.of(booking));

        SurgeryReportStatsDto stats = reportService.generateStats(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));

        assertEquals(1, stats.getTotalTheatres());
        assertEquals(1, stats.getTotalSurgeries());
        assertEquals(1, stats.getCompletedSurgeries());
        assertEquals(20.0, stats.getAverageDelayMinutes());
        assertTrue(stats.getOverallUtilizationRatePercent() > 0);
        assertTrue(stats.getProcedureCategoryCounts().containsKey("General"));
    }
}
