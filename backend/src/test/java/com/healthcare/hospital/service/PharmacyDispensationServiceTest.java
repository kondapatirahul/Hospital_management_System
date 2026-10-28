package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.BatchAllocationDto;
import com.healthcare.hospital.dto.DispenseRequestDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.DispensationStatus;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PharmacyDispensationServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PrescriptionDispensationRepository dispensationRepository;

    @Mock
    private DispensationItemRepository dispensationItemRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private MedicineBatchRepository batchRepository;

    @Mock
    private StockTransactionRepository transactionRepository;

    @Mock
    private PharmacyAuditService auditService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PharmacyDispensationService dispensationService;

    private Patient patient;
    private Doctor doctor;
    private Prescription prescription;
    private Medicine medicine;
    private MedicineBatch batch;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setPatientId("PAT-0001");
        patient.setFullName("Sarah Jenkins");

        doctor = new Doctor();
        doctor.setId(2L);
        doctor.setFullName("Alexander Wright");

        prescription = new Prescription();
        prescription.setId(10L);
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setMedicationName("Augmentin");
        prescription.setDosage("625 mg");
        prescription.setPrescriptionDate(LocalDate.now());
        prescription.setPrescribedQuantity(14);
        prescription.setDispensedQuantity(0);
        prescription.setDispenseStatus("PENDING");

        medicine = new Medicine("MED-001", "Augmentin", "Amox/Clav", null, "Tablet", "625mg", "Strip", 20, BigDecimal.valueOf(18.50), BigDecimal.valueOf(11.20), "GSK");
        medicine.setId(100L);
        medicine.setTotalStock(50);

        batch = new MedicineBatch("BAT-AUG-01", medicine, null, 50, BigDecimal.valueOf(11.20), BigDecimal.valueOf(18.50), LocalDate.now(), LocalDate.now().plusMonths(12));
        batch.setId(500L);
    }

    @Test
    void testDispensePrescription_FullDispense_Success() {
        DispenseRequestDto dto = new DispenseRequestDto();
        dto.setPrescriptionId(10L);
        dto.setMedicineId(100L);
        dto.setAllocations(List.of(new BatchAllocationDto(500L, 14)));
        dto.setRemarks("Dispensed full 14 units");

        when(prescriptionRepository.findByIdWithLock(10L)).thenReturn(Optional.of(prescription));
        when(medicineRepository.findById(100L)).thenReturn(Optional.of(medicine));
        when(batchRepository.findByIdWithLock(500L)).thenReturn(Optional.of(batch));
        when(dispensationRepository.save(any(PrescriptionDispensation.class))).thenAnswer(invocation -> {
            PrescriptionDispensation d = invocation.getArgument(0);
            d.setId(1001L);
            return d;
        });
        when(batchRepository.calculateValidStockForMedicine(eq(medicine), any(LocalDate.class))).thenReturn(36);

        PrescriptionDispensation result = dispensationService.dispensePrescription(dto, "pharmacist");

        assertNotNull(result);
        assertEquals(DispensationStatus.COMPLETED, result.getStatus());
        assertEquals(14, result.getTotalQuantity());
        assertEquals(36, batch.getCurrentQuantity());
        assertEquals("DISPENSED", prescription.getDispenseStatus());
        assertEquals(14, prescription.getDispensedQuantity());

        verify(prescriptionRepository, times(1)).save(prescription);
        verify(batchRepository, times(1)).save(batch);
        verify(transactionRepository, times(1)).save(any(StockTransaction.class));
        verify(auditService, times(1)).logEvent(eq("PRESCRIPTION_DISPENSED"), anyString(), anyString(), eq("pharmacist"), anyString());
    }

    @Test
    void testDispensePrescription_PartialDispense_Success() {
        DispenseRequestDto dto = new DispenseRequestDto();
        dto.setPrescriptionId(10L);
        dto.setMedicineId(100L);
        dto.setAllocations(List.of(new BatchAllocationDto(500L, 10))); // Dispensing 10 out of 14

        when(prescriptionRepository.findByIdWithLock(10L)).thenReturn(Optional.of(prescription));
        when(medicineRepository.findById(100L)).thenReturn(Optional.of(medicine));
        when(batchRepository.findByIdWithLock(500L)).thenReturn(Optional.of(batch));
        when(dispensationRepository.save(any(PrescriptionDispensation.class))).thenAnswer(invocation -> {
            PrescriptionDispensation d = invocation.getArgument(0);
            d.setId(1002L);
            return d;
        });
        when(batchRepository.calculateValidStockForMedicine(eq(medicine), any(LocalDate.class))).thenReturn(40);

        PrescriptionDispensation result = dispensationService.dispensePrescription(dto, "pharmacist");

        assertNotNull(result);
        assertEquals(DispensationStatus.PARTIAL, result.getStatus());
        assertEquals(10, result.getTotalQuantity());
        assertEquals("PARTIALLY_DISPENSED", prescription.getDispenseStatus());
        assertEquals(10, prescription.getDispensedQuantity());
        assertEquals(4, prescription.getRemainingQuantity());
    }

    @Test
    void testDispensePrescription_DuplicateDispensingPrevented() {
        prescription.setDispensedQuantity(14);
        prescription.setDispenseStatus("DISPENSED");

        DispenseRequestDto dto = new DispenseRequestDto();
        dto.setPrescriptionId(10L);
        dto.setMedicineId(100L);
        dto.setAllocations(List.of(new BatchAllocationDto(500L, 5)));

        when(prescriptionRepository.findByIdWithLock(10L)).thenReturn(Optional.of(prescription));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                dispensationService.dispensePrescription(dto, "pharmacist")
        );
        assertTrue(ex.getMessage().contains("already been fully dispensed"));
        verify(dispensationRepository, never()).save(any(PrescriptionDispensation.class));
    }

    @Test
    void testDispensePrescription_ExpiredBatchPrevented() {
        MedicineBatch expiredBatch = new MedicineBatch("BAT-EXP", medicine, null, 20, BigDecimal.ONE, BigDecimal.TEN, LocalDate.now(), LocalDate.now().minusDays(5));
        expiredBatch.setId(505L);

        DispenseRequestDto dto = new DispenseRequestDto();
        dto.setPrescriptionId(10L);
        dto.setMedicineId(100L);
        dto.setAllocations(List.of(new BatchAllocationDto(505L, 5)));

        when(prescriptionRepository.findByIdWithLock(10L)).thenReturn(Optional.of(prescription));
        when(medicineRepository.findById(100L)).thenReturn(Optional.of(medicine));
        when(batchRepository.findByIdWithLock(505L)).thenReturn(Optional.of(expiredBatch));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                dispensationService.dispensePrescription(dto, "pharmacist")
        );
        assertTrue(ex.getMessage().contains("expired"));
        verify(dispensationRepository, never()).save(any(PrescriptionDispensation.class));
    }

    @Test
    void testDispensePrescription_InsufficientStock_NegativeStockPrevented() {
        batch.setCurrentQuantity(5); // Only 5 in batch

        DispenseRequestDto dto = new DispenseRequestDto();
        dto.setPrescriptionId(10L);
        dto.setMedicineId(100L);
        dto.setAllocations(List.of(new BatchAllocationDto(500L, 10))); // Requested 10

        when(prescriptionRepository.findByIdWithLock(10L)).thenReturn(Optional.of(prescription));
        when(medicineRepository.findById(100L)).thenReturn(Optional.of(medicine));
        when(batchRepository.findByIdWithLock(500L)).thenReturn(Optional.of(batch));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                dispensationService.dispensePrescription(dto, "pharmacist")
        );
        assertTrue(ex.getMessage().contains("Insufficient stock") || ex.getMessage().contains("Negative stock prevented"));
        verify(dispensationRepository, never()).save(any(PrescriptionDispensation.class));
    }
}
