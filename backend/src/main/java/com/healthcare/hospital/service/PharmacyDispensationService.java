package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.BatchAllocationDto;
import com.healthcare.hospital.dto.DispenseRequestDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.BatchStatus;
import com.healthcare.hospital.enums.DispensationStatus;
import com.healthcare.hospital.enums.StockTransactionType;
import com.healthcare.hospital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyDispensationService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDispensationRepository dispensationRepository;
    private final DispensationItemRepository dispensationItemRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository batchRepository;
    private final StockTransactionRepository transactionRepository;
    private final PharmacyAuditService auditService;
    private final NotificationService notificationService;

    public PharmacyDispensationService(PrescriptionRepository prescriptionRepository,
                                       PrescriptionDispensationRepository dispensationRepository,
                                       DispensationItemRepository dispensationItemRepository,
                                       MedicineRepository medicineRepository,
                                       MedicineBatchRepository batchRepository,
                                       StockTransactionRepository transactionRepository,
                                       PharmacyAuditService auditService,
                                       NotificationService notificationService) {
        this.prescriptionRepository = prescriptionRepository;
        this.dispensationRepository = dispensationRepository;
        this.dispensationItemRepository = dispensationItemRepository;
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    public Page<Prescription> searchPrescriptions(String query, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return prescriptionRepository.searchPrescriptions(query, status, pageable);
    }

    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }

    public Page<PrescriptionDispensation> searchDispensations(String query, DispensationStatus status,
                                                              LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return dispensationRepository.searchDispensations(query, status, start, end, pageable);
    }

    public Optional<PrescriptionDispensation> getDispensationById(Long id) {
        return dispensationRepository.findById(id);
    }

    public List<PrescriptionDispensation> getDispensationsForPrescription(Prescription rx) {
        return dispensationRepository.findByPrescription(rx);
    }

    /**
     * Dispenses a prescription with concurrency-safe locking on the prescription and batches.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PrescriptionDispensation dispensePrescription(DispenseRequestDto dto, String username) {
        // Concurrency-safe lock on prescription to prevent duplicate concurrent dispensing
        Prescription prescription = prescriptionRepository.findByIdWithLock(dto.getPrescriptionId())
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + dto.getPrescriptionId()));

        if (prescription.isFullyDispensed()) {
            throw new IllegalStateException("Prescription has already been fully dispensed. Duplicate dispensing is prohibited.");
        }

        int remainingQty = prescription.getRemainingQuantity();
        int requestedQty = dto.getTotalQuantity();

        if (requestedQty <= 0) {
            throw new IllegalArgumentException("Dispensing quantity must be at least 1.");
        }

        if (requestedQty > remainingQty) {
            throw new IllegalArgumentException(String.format(
                    "Requested dispense quantity (%d) exceeds the remaining prescribed quantity (%d).",
                    requestedQty, remainingQty
            ));
        }

        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + dto.getMedicineId()));

        String dispensationNumber = generateDispensationNumber();
        DispensationStatus status = (requestedQty == remainingQty) ? DispensationStatus.COMPLETED : DispensationStatus.PARTIAL;

        PrescriptionDispensation dispensation = new PrescriptionDispensation(
                dispensationNumber,
                prescription,
                prescription.getPatient(),
                prescription.getDoctor(),
                username,
                status,
                dto.getRemarks()
        );

        LocalDate today = LocalDate.now();

        // Process batch allocations with pessimistic locking
        for (BatchAllocationDto alloc : dto.getAllocations()) {
            if (alloc.getQuantity() == null || alloc.getQuantity() <= 0) {
                continue;
            }

            MedicineBatch batch = batchRepository.findByIdWithLock(alloc.getBatchId())
                    .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + alloc.getBatchId()));

            // Verify medicine matching
            if (!batch.getMedicine().getId().equals(medicine.getId())) {
                throw new IllegalArgumentException("Batch " + batch.getBatchNumber() + " does not belong to medicine " + medicine.getName());
            }

            // Expiry prevention check
            if (batch.isExpired(today)) {
                throw new IllegalStateException(String.format(
                        "Batch %s expired on %s. Dispensing expired medication is strictly prohibited.",
                        batch.getBatchNumber(), batch.getExpiryDate()
                ));
            }

            // Available stock check & negative stock prevention
            if (batch.getCurrentQuantity() < alloc.getQuantity()) {
                throw new IllegalStateException(String.format(
                        "Insufficient stock in batch %s: requested %d, available %d. Negative stock prevented.",
                        batch.getBatchNumber(), alloc.getQuantity(), batch.getCurrentQuantity()
                ));
            }

            int previousMedicineStock = medicine.getTotalStock();

            // Deduct stock
            int newBatchQty = batch.getCurrentQuantity() - alloc.getQuantity();
            batch.setCurrentQuantity(newBatchQty);
            batch.setStatus(batch.calculateStatus(today));
            batchRepository.save(batch);

            // Create dispensation item
            DispensationItem item = new DispensationItem(
                    dispensation,
                    prescription,
                    medicine,
                    batch,
                    alloc.getQuantity(),
                    batch.getUnitPrice(),
                    prescription.getInstructions()
            );
            dispensation.addItem(item);

            // Refresh medicine aggregate stock
            int newValidStock = batchRepository.calculateValidStockForMedicine(medicine, today);
            medicine.setTotalStock(Math.max(0, newValidStock));
            medicineRepository.save(medicine);

            // Record stock transaction
            String txNumber = "STX-" + System.currentTimeMillis() % 10000000;
            StockTransaction tx = new StockTransaction(
                    txNumber,
                    StockTransactionType.DISPENSED,
                    medicine,
                    batch,
                    alloc.getQuantity(),
                    batch.getUnitPrice(),
                    previousMedicineStock,
                    medicine.getTotalStock(),
                    "PRESCRIPTION",
                    dispensationNumber,
                    username,
                    "Dispensed for Patient: " + prescription.getPatient().getFullName() + " | Rx ID: " + prescription.getId()
            );
            transactionRepository.save(tx);
        }

        PrescriptionDispensation savedDispensation = dispensationRepository.save(dispensation);

        // Update Prescription Status
        prescription.setDispensedQuantity(prescription.getDispensedQuantity() + requestedQty);
        if (prescription.getRemainingQuantity() == 0) {
            prescription.setDispenseStatus("DISPENSED");
            prescription.setStatus("Completed");
        } else {
            prescription.setDispenseStatus("PARTIALLY_DISPENSED");
        }
        prescriptionRepository.save(prescription);

        // Audit Log
        auditService.logEvent(
                "PRESCRIPTION_DISPENSED",
                "PrescriptionDispensation",
                savedDispensation.getId().toString(),
                username,
                String.format("Dispensed %d units of %s (%s) for Patient %s (Rx #%d). Dispensation #%s.",
                        requestedQty, medicine.getName(), medicine.getMedicineCode(),
                        prescription.getPatient().getFullName(), prescription.getId(), savedDispensation.getDispensationNumber())
        );

        // Notify patient if user account exists
        if (prescription.getPatient().getUser() != null) {
            notificationService.createNotification(
                    prescription.getPatient().getUser(),
                    "Prescription Dispensed",
                    String.format("Your prescription for %s has been %s by the hospital pharmacy (%d units dispensed).",
                            prescription.getMedicationName(),
                            prescription.isFullyDispensed() ? "fully dispensed" : "partially dispensed",
                            requestedQty),
                    "PHARMACY_UPDATE"
            );
        }

        return savedDispensation;
    }

    public synchronized String generateDispensationNumber() {
        return "DISP-" + LocalDate.now().getYear() + "-" + String.format("%05d", dispensationRepository.count() + 1);
    }
}
