package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.MedicineDto;
import com.healthcare.hospital.dto.PharmacyDashboardStatsDto;
import com.healthcare.hospital.entity.Medicine;
import com.healthcare.hospital.entity.MedicineCategory;
import com.healthcare.hospital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyInventoryService {

    private final MedicineRepository medicineRepository;
    private final MedicineCategoryRepository categoryRepository;
    private final MedicineBatchRepository batchRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDispensationRepository dispensationRepository;
    private final StockTransactionRepository transactionRepository;
    private final PharmacyAuditService auditService;

    public PharmacyInventoryService(MedicineRepository medicineRepository,
                                    MedicineCategoryRepository categoryRepository,
                                    MedicineBatchRepository batchRepository,
                                    PrescriptionRepository prescriptionRepository,
                                    PrescriptionDispensationRepository dispensationRepository,
                                    StockTransactionRepository transactionRepository,
                                    PharmacyAuditService auditService) {
        this.medicineRepository = medicineRepository;
        this.categoryRepository = categoryRepository;
        this.batchRepository = batchRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.dispensationRepository = dispensationRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    public List<MedicineCategory> getAllCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    public Optional<MedicineCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public MedicineCategory saveCategory(MedicineCategory category, String username) {
        boolean isNew = category.getId() == null;
        if (isNew && categoryRepository.existsByCodeIgnoreCase(category.getCode())) {
            throw new IllegalArgumentException("Category code already exists: " + category.getCode());
        }
        MedicineCategory saved = categoryRepository.save(category);
        auditService.logEvent(
                isNew ? "CATEGORY_CREATED" : "CATEGORY_UPDATED",
                "MedicineCategory",
                saved.getId().toString(),
                username,
                "Category " + saved.getName() + " (" + saved.getCode() + ") saved."
        );
        return saved;
    }

    public Page<Medicine> searchMedicines(String query, Long categoryId, String status, int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortField != null && !sortField.isBlank() ? sortField : "name");
        Pageable pageable = PageRequest.of(page, size, sort);
        return medicineRepository.searchMedicines(query, categoryId, status, pageable);
    }

    public List<Medicine> getActiveMedicines() {
        return medicineRepository.findByActiveTrueOrderByNameAsc();
    }

    public Optional<Medicine> getMedicineById(Long id) {
        return medicineRepository.findById(id);
    }

    public Optional<Medicine> getMedicineByCode(String code) {
        return medicineRepository.findByMedicineCodeIgnoreCase(code);
    }

    public Optional<Medicine> findByName(String name) {
        return medicineRepository.findByNameIgnoreCase(name);
    }

    @Transactional
    public Medicine saveMedicine(MedicineDto dto, String username) {
        MedicineCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + dto.getCategoryId()));

        Medicine medicine;
        boolean isNew = dto.getId() == null;

        if (isNew) {
            String code = dto.getMedicineCode();
            if (code == null || code.trim().isEmpty()) {
                code = generateNextMedicineCode();
            } else {
                code = code.trim().toUpperCase();
                if (medicineRepository.existsByMedicineCodeIgnoreCase(code)) {
                    throw new IllegalArgumentException("Medicine code already exists: " + code);
                }
            }
            medicine = new Medicine(
                    code,
                    dto.getName().trim(),
                    dto.getGenericName(),
                    category,
                    dto.getDosageForm().trim(),
                    dto.getStrength().trim(),
                    dto.getUnitOfMeasure().trim(),
                    dto.getReorderLevel(),
                    dto.getUnitPrice(),
                    dto.getPurchasePrice(),
                    dto.getManufacturer()
            );
        } else {
            medicine = medicineRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Medicine not found with ID: " + dto.getId()));
            medicine.setName(dto.getName().trim());
            medicine.setGenericName(dto.getGenericName());
            medicine.setCategory(category);
            medicine.setDosageForm(dto.getDosageForm().trim());
            medicine.setStrength(dto.getStrength().trim());
            medicine.setUnitOfMeasure(dto.getUnitOfMeasure().trim());
            medicine.setReorderLevel(dto.getReorderLevel());
            medicine.setUnitPrice(dto.getUnitPrice());
            medicine.setPurchasePrice(dto.getPurchasePrice());
            medicine.setManufacturer(dto.getManufacturer());
            medicine.setUpdatedAt(LocalDateTime.now());
        }

        medicine.setStorageConditions(dto.getStorageConditions());
        medicine.setPrescriptionRequired(dto.isPrescriptionRequired());
        medicine.setActive(dto.isActive());
        medicine.setDescription(dto.getDescription());

        // Refresh total stock from valid active batches
        if (!isNew) {
            int currentValidStock = batchRepository.calculateValidStockForMedicine(medicine, LocalDate.now());
            medicine.setTotalStock(Math.max(0, currentValidStock));
        }

        Medicine saved = medicineRepository.save(medicine);

        auditService.logEvent(
                isNew ? "MEDICINE_CREATED" : "MEDICINE_UPDATED",
                "Medicine",
                saved.getId().toString(),
                username,
                "Medicine " + saved.getName() + " (" + saved.getMedicineCode() + ") saved."
        );

        return saved;
    }

    @Transactional
    public void toggleMedicineStatus(Long id, String username) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + id));
        medicine.setActive(!medicine.isActive());
        medicine.setUpdatedAt(LocalDateTime.now());
        medicineRepository.save(medicine);

        auditService.logEvent(
                medicine.isActive() ? "MEDICINE_ACTIVATED" : "MEDICINE_DEACTIVATED",
                "Medicine",
                medicine.getId().toString(),
                username,
                "Medicine " + medicine.getName() + " active status set to " + medicine.isActive()
        );
    }

    public synchronized String generateNextMedicineCode() {
        long count = medicineRepository.count() + 1;
        String code = String.format("MED-%04d", count);
        while (medicineRepository.existsByMedicineCodeIgnoreCase(code)) {
            count++;
            code = String.format("MED-%04d", count);
        }
        return code;
    }

    @Transactional
    public void refreshMedicineTotalStock(Medicine medicine) {
        int validStock = batchRepository.calculateValidStockForMedicine(medicine, LocalDate.now());
        medicine.setTotalStock(Math.max(0, validStock));
        medicine.setUpdatedAt(LocalDateTime.now());
        medicineRepository.save(medicine);
    }

    public PharmacyDashboardStatsDto getDashboardStats() {
        PharmacyDashboardStatsDto stats = new PharmacyDashboardStatsDto();
        LocalDate today = LocalDate.now();
        LocalDate nearExpiryDate = today.plusDays(60);

        stats.setTotalMedicines(medicineRepository.countActiveMedicines());
        stats.setAvailableMedicines(medicineRepository.countAvailableMedicines());
        stats.setLowStockMedicines(medicineRepository.countLowStockMedicines());
        stats.setOutOfStockMedicines(medicineRepository.countOutOfStockMedicines());

        stats.setExpiredBatches(batchRepository.countExpiredBatches(today));
        stats.setNearExpiryBatches(batchRepository.countNearExpiryBatches(today, nearExpiryDate));

        stats.setPendingPrescriptions(prescriptionRepository.countPendingPrescriptions());
        stats.setCompletedDispensations(dispensationRepository.countCompletedDispensations());

        stats.setRecentStockTransactions(transactionRepository.findTop10ByOrderByTransactionDateDesc());
        stats.setRecentPrescriptionActivity(prescriptionRepository.findTop10ByOrderByCreatedAtDesc());

        return stats;
    }
}
