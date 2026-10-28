package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.MedicineDto;
import com.healthcare.hospital.dto.PharmacyDashboardStatsDto;
import com.healthcare.hospital.entity.Medicine;
import com.healthcare.hospital.entity.MedicineCategory;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PharmacyInventoryServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private MedicineCategoryRepository categoryRepository;

    @Mock
    private MedicineBatchRepository batchRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PrescriptionDispensationRepository dispensationRepository;

    @Mock
    private StockTransactionRepository transactionRepository;

    @Mock
    private PharmacyAuditService auditService;

    @InjectMocks
    private PharmacyInventoryService inventoryService;

    private MedicineCategory category;
    private Medicine medicine;

    @BeforeEach
    void setUp() {
        category = new MedicineCategory("CAT-ANTI", "Antibiotics", "Antimicrobial medications");
        category.setId(1L);

        medicine = new Medicine(
                "MED-0001", "Amoxicillin", "Amoxicillin Trihydrate",
                category, "Tablet", "500 mg", "Strip", 20,
                BigDecimal.valueOf(15.00), BigDecimal.valueOf(9.00), "PharmaCorp"
        );
        medicine.setId(10L);
        medicine.setTotalStock(100);
    }

    @Test
    void testSaveNewMedicine_Success() {
        MedicineDto dto = new MedicineDto();
        dto.setName("Augmentin");
        dto.setGenericName("Amoxicillin / Clavulanate");
        dto.setCategoryId(1L);
        dto.setDosageForm("Tablet");
        dto.setStrength("625 mg");
        dto.setUnitOfMeasure("Strip");
        dto.setReorderLevel(25);
        dto.setUnitPrice(BigDecimal.valueOf(18.00));
        dto.setPurchasePrice(BigDecimal.valueOf(11.00));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(medicineRepository.count()).thenReturn(5L);
        when(medicineRepository.existsByMedicineCodeIgnoreCase(anyString())).thenReturn(false);
        when(medicineRepository.save(any(Medicine.class))).thenAnswer(invocation -> {
            Medicine m = invocation.getArgument(0);
            m.setId(11L);
            return m;
        });

        Medicine saved = inventoryService.saveMedicine(dto, "test-pharmacist");

        assertNotNull(saved);
        assertEquals("Augmentin", saved.getName());
        assertEquals("MED-0006", saved.getMedicineCode());
        verify(medicineRepository, times(1)).save(any(Medicine.class));
        verify(auditService, times(1)).logEvent(eq("MEDICINE_CREATED"), eq("Medicine"), eq("11"), eq("test-pharmacist"), anyString());
    }

    @Test
    void testSaveMedicine_DuplicateCodeThrowsException() {
        MedicineDto dto = new MedicineDto();
        dto.setMedicineCode("MED-EXISTING");
        dto.setName("Paracetamol");
        dto.setCategoryId(1L);
        dto.setDosageForm("Tablet");
        dto.setStrength("500 mg");
        dto.setUnitOfMeasure("Strip");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(medicineRepository.existsByMedicineCodeIgnoreCase("MED-EXISTING")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                inventoryService.saveMedicine(dto, "test-pharmacist")
        );

        assertTrue(ex.getMessage().contains("already exists"));
        verify(medicineRepository, never()).save(any(Medicine.class));
    }

    @Test
    void testLowStockAndOutOfStockCalculations() {
        Medicine lowStockMed = new Medicine("MED-002", "Albuterol", "Albuterol", category, "Inhaler", "90mcg", "Inhaler", 15, BigDecimal.TEN, BigDecimal.ONE, "Mfr");
        lowStockMed.setTotalStock(10);
        assertTrue(lowStockMed.isLowStock());
        assertFalse(lowStockMed.isOutOfStock());

        Medicine outOfStockMed = new Medicine("MED-003", "Cipro", "Cipro", category, "Drops", "0.3%", "Bottle", 10, BigDecimal.TEN, BigDecimal.ONE, "Mfr");
        outOfStockMed.setTotalStock(0);
        assertFalse(outOfStockMed.isLowStock());
        assertTrue(outOfStockMed.isOutOfStock());
    }

    @Test
    void testToggleMedicineStatus() {
        when(medicineRepository.findById(10L)).thenReturn(Optional.of(medicine));
        assertTrue(medicine.isActive());

        inventoryService.toggleMedicineStatus(10L, "pharmacist");
        assertFalse(medicine.isActive());

        inventoryService.toggleMedicineStatus(10L, "pharmacist");
        assertTrue(medicine.isActive());
        verify(medicineRepository, times(2)).save(medicine);
    }

    @Test
    void testGetDashboardStats() {
        when(medicineRepository.countActiveMedicines()).thenReturn(20L);
        when(medicineRepository.countAvailableMedicines()).thenReturn(15L);
        when(medicineRepository.countLowStockMedicines()).thenReturn(3L);
        when(medicineRepository.countOutOfStockMedicines()).thenReturn(2L);
        when(batchRepository.countExpiredBatches(any(LocalDate.class))).thenReturn(1L);
        when(batchRepository.countNearExpiryBatches(any(LocalDate.class), any(LocalDate.class))).thenReturn(4L);
        when(prescriptionRepository.countPendingPrescriptions()).thenReturn(7L);
        when(dispensationRepository.countCompletedDispensations()).thenReturn(45L);
        when(transactionRepository.findTop10ByOrderByTransactionDateDesc()).thenReturn(Collections.emptyList());
        when(prescriptionRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        PharmacyDashboardStatsDto stats = inventoryService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(20L, stats.getTotalMedicines());
        assertEquals(15L, stats.getAvailableMedicines());
        assertEquals(3L, stats.getLowStockMedicines());
        assertEquals(2L, stats.getOutOfStockMedicines());
        assertEquals(1L, stats.getExpiredBatches());
        assertEquals(4L, stats.getNearExpiryBatches());
        assertEquals(7L, stats.getPendingPrescriptions());
        assertEquals(45L, stats.getCompletedDispensations());
    }
}
