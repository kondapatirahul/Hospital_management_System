package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.MedicineBatchDto;
import com.healthcare.hospital.dto.StockAdjustmentDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.BatchStatus;
import com.healthcare.hospital.enums.StockAdjustmentReason;
import com.healthcare.hospital.enums.StockTransactionType;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PharmacyBatchServiceTest {

    @Mock
    private MedicineBatchRepository batchRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private StockTransactionRepository transactionRepository;

    @Mock
    private StockAdjustmentRepository adjustmentRepository;

    @Mock
    private PharmacyAuditService auditService;

    @InjectMocks
    private PharmacyBatchService batchService;

    private Medicine medicine;
    private Supplier supplier;
    private MedicineBatch batch;

    @BeforeEach
    void setUp() {
        medicine = new Medicine("MED-001", "Amoxicillin", "Generic", null, "Tablet", "500mg", "Strip", 20, BigDecimal.TEN, BigDecimal.valueOf(6.0), "Mfr");
        medicine.setId(1L);
        medicine.setTotalStock(50);

        supplier = new Supplier("SUP-001", "Apex Pharma", "Contact", "email@apex.com", "+1 555-1234", "Chicago", "LIC-01", "Net 30");
        supplier.setId(2L);

        batch = new MedicineBatch("BAT-2026-A", medicine, supplier, 50, BigDecimal.valueOf(6.0), BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusMonths(12));
        batch.setId(100L);
    }

    @Test
    void testCreateBatchAndStockIn_Success() {
        MedicineBatchDto dto = new MedicineBatchDto();
        dto.setMedicineId(1L);
        dto.setSupplierId(2L);
        dto.setBatchNumber("BAT-NEW-01");
        dto.setQuantity(30);
        dto.setPurchasePrice(BigDecimal.valueOf(6.50));
        dto.setUnitPrice(BigDecimal.valueOf(11.00));
        dto.setReceivedDate(LocalDate.now());
        dto.setExpiryDate(LocalDate.now().plusMonths(18));
        dto.setNotes("Regular intake");

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));
        when(batchRepository.findByBatchNumberAndMedicine("BAT-NEW-01", medicine)).thenReturn(Optional.empty());
        when(batchRepository.save(any(MedicineBatch.class))).thenAnswer(invocation -> {
            MedicineBatch b = invocation.getArgument(0);
            b.setId(101L);
            return b;
        });
        when(batchRepository.calculateValidStockForMedicine(eq(medicine), any(LocalDate.class))).thenReturn(80);

        MedicineBatch result = batchService.createBatchAndStockIn(dto, "pharmacist");

        assertNotNull(result);
        assertEquals("BAT-NEW-01", result.getBatchNumber());
        assertEquals(30, result.getCurrentQuantity());
        verify(transactionRepository, times(1)).save(any(StockTransaction.class));
        verify(medicineRepository, times(1)).save(medicine);
        verify(auditService, times(1)).logEvent(eq("BATCH_STOCK_IN"), eq("MedicineBatch"), anyString(), eq("pharmacist"), anyString());
    }

    @Test
    void testStockIn_ExpiredBatchThrowsException() {
        MedicineBatchDto dto = new MedicineBatchDto();
        dto.setMedicineId(1L);
        dto.setBatchNumber("BAT-EXPIRED");
        dto.setQuantity(20);
        dto.setExpiryDate(LocalDate.now().minusDays(1)); // Expired!

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                batchService.createBatchAndStockIn(dto, "pharmacist")
        );
        assertTrue(ex.getMessage().contains("already expired"));
        verify(batchRepository, never()).save(any(MedicineBatch.class));
    }

    @Test
    void testStockAdjustment_IncreaseStock() {
        StockAdjustmentDto dto = new StockAdjustmentDto();
        dto.setBatchId(100L);
        dto.setAdjustmentType("INCREASE");
        dto.setQuantity(10);
        dto.setReason(StockAdjustmentReason.INVENTORY_COUNT_CORRECTION);
        dto.setRemarks("Found extra physical strip");

        when(batchRepository.findByIdWithLock(100L)).thenReturn(Optional.of(batch));
        when(adjustmentRepository.save(any(StockAdjustment.class))).thenAnswer(invocation -> {
            StockAdjustment a = invocation.getArgument(0);
            a.setId(999L);
            return a;
        });
        when(batchRepository.calculateValidStockForMedicine(eq(medicine), any(LocalDate.class))).thenReturn(60);

        StockAdjustment adj = batchService.adjustStock(dto, "pharmacist");

        assertNotNull(adj);
        assertEquals(60, batch.getCurrentQuantity());
        verify(transactionRepository, times(1)).save(any(StockTransaction.class));
        verify(medicineRepository, times(1)).save(medicine);
    }

    @Test
    void testStockAdjustment_DecreaseStock_NegativeStockPrevented() {
        StockAdjustmentDto dto = new StockAdjustmentDto();
        dto.setBatchId(100L);
        dto.setAdjustmentType("DECREASE");
        dto.setQuantity(60); // Batch only has 50!
        dto.setReason(StockAdjustmentReason.DAMAGED_IN_STORAGE);
        dto.setRemarks("Water damage");

        when(batchRepository.findByIdWithLock(100L)).thenReturn(Optional.of(batch));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                batchService.adjustStock(dto, "pharmacist")
        );

        assertTrue(ex.getMessage().contains("Negative stock is strictly prevented"));
        verify(batchRepository, never()).save(any(MedicineBatch.class));
        verify(transactionRepository, never()).save(any(StockTransaction.class));
    }

    @Test
    void testBatchStatusCalculations() {
        MedicineBatch activeBatch = new MedicineBatch("BAT-A", medicine, supplier, 10, BigDecimal.ONE, BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusMonths(6));
        assertEquals(BatchStatus.ACTIVE, activeBatch.calculateStatus(LocalDate.now()));
        assertTrue(activeBatch.isEligibleForDispensing());

        MedicineBatch expiredBatch = new MedicineBatch("BAT-E", medicine, supplier, 10, BigDecimal.ONE, BigDecimal.TEN, LocalDate.now(), LocalDate.now().minusDays(5));
        assertEquals(BatchStatus.EXPIRED, expiredBatch.calculateStatus(LocalDate.now()));
        assertFalse(expiredBatch.isEligibleForDispensing());

        MedicineBatch depletedBatch = new MedicineBatch("BAT-D", medicine, supplier, 0, BigDecimal.ONE, BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusMonths(6));
        assertEquals(BatchStatus.DEPLETED, depletedBatch.calculateStatus(LocalDate.now()));
        assertFalse(depletedBatch.isEligibleForDispensing());
    }
}
