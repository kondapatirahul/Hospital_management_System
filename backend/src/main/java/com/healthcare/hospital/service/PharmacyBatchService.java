package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.MedicineBatchDto;
import com.healthcare.hospital.dto.StockAdjustmentDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.BatchStatus;
import com.healthcare.hospital.enums.StockAdjustmentReason;
import com.healthcare.hospital.enums.StockTransactionType;
import com.healthcare.hospital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyBatchService {

    private final MedicineBatchRepository batchRepository;
    private final MedicineRepository medicineRepository;
    private final SupplierRepository supplierRepository;
    private final StockTransactionRepository transactionRepository;
    private final StockAdjustmentRepository adjustmentRepository;
    private final PharmacyAuditService auditService;

    public PharmacyBatchService(MedicineBatchRepository batchRepository,
                                MedicineRepository medicineRepository,
                                SupplierRepository supplierRepository,
                                StockTransactionRepository transactionRepository,
                                StockAdjustmentRepository adjustmentRepository,
                                PharmacyAuditService auditService) {
        this.batchRepository = batchRepository;
        this.medicineRepository = medicineRepository;
        this.supplierRepository = supplierRepository;
        this.transactionRepository = transactionRepository;
        this.adjustmentRepository = adjustmentRepository;
        this.auditService = auditService;
    }

    public Page<MedicineBatch> searchBatches(String query, BatchStatus status, Long medicineId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "expiryDate"));
        return batchRepository.searchBatches(query, status, medicineId, pageable);
    }

    public Optional<MedicineBatch> getBatchById(Long id) {
        return batchRepository.findById(id);
    }

    public List<MedicineBatch> getBatchesForMedicine(Medicine medicine) {
        return batchRepository.findByMedicineOrderByExpiryDateAsc(medicine);
    }

    /**
     * Earliest Expiry First Out (FEFO) retrieval:
     * Only returns batches that have available stock, are ACTIVE, and are NOT expired.
     */
    public List<MedicineBatch> getEligibleBatchesFEFO(Medicine medicine) {
        return batchRepository.findEligibleBatchesForDispensing(medicine, LocalDate.now());
    }

    public List<MedicineBatch> getNearExpiryBatches(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(daysThreshold);
        return batchRepository.findNearExpiryBatches(today, threshold);
    }

    public List<MedicineBatch> getExpiredBatches() {
        return batchRepository.findExpiredBatches(LocalDate.now());
    }

    @Transactional
    public MedicineBatch createBatchAndStockIn(MedicineBatchDto dto, String username) {
        Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + dto.getMedicineId()));

        Supplier supplier = null;
        if (dto.getSupplierId() != null) {
            supplier = supplierRepository.findById(dto.getSupplierId()).orElse(null);
        }

        if (dto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot stock in a batch that is already expired: " + dto.getExpiryDate());
        }

        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Stock-in quantity must be greater than zero.");
        }

        Optional<MedicineBatch> existingBatchOpt = batchRepository.findByBatchNumberAndMedicine(dto.getBatchNumber().trim(), medicine);
        MedicineBatch batch;
        int previousStock = medicine.getTotalStock();

        if (existingBatchOpt.isPresent()) {
            batch = existingBatchOpt.get();
            batch.setCurrentQuantity(batch.getCurrentQuantity() + dto.getQuantity());
            batch.setInitialQuantity(batch.getInitialQuantity() + dto.getQuantity());
            if (dto.getUnitPrice() != null && dto.getUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
                batch.setUnitPrice(dto.getUnitPrice());
            }
            if (dto.getPurchasePrice() != null && dto.getPurchasePrice().compareTo(BigDecimal.ZERO) > 0) {
                batch.setPurchasePrice(dto.getPurchasePrice());
            }
            batch.setStatus(batch.calculateStatus(LocalDate.now()));
        } else {
            batch = new MedicineBatch(
                    dto.getBatchNumber().trim(),
                    medicine,
                    supplier,
                    dto.getQuantity(),
                    dto.getPurchasePrice(),
                    dto.getUnitPrice() != null ? dto.getUnitPrice() : medicine.getUnitPrice(),
                    dto.getReceivedDate() != null ? dto.getReceivedDate() : LocalDate.now(),
                    dto.getExpiryDate()
            );
            batch.setNotes(dto.getNotes());
        }

        MedicineBatch savedBatch = batchRepository.save(batch);

        // Update medicine stock
        int validStock = batchRepository.calculateValidStockForMedicine(medicine, LocalDate.now());
        medicine.setTotalStock(validStock);
        medicineRepository.save(medicine);

        // Record stock transaction
        String txNumber = generateTransactionNumber();
        StockTransaction tx = new StockTransaction(
                txNumber,
                StockTransactionType.STOCK_IN,
                medicine,
                savedBatch,
                dto.getQuantity(),
                savedBatch.getPurchasePrice(),
                previousStock,
                medicine.getTotalStock(),
                "DIRECT_RECEIPT",
                savedBatch.getBatchNumber(),
                username,
                dto.getNotes() != null ? dto.getNotes() : "Direct stock-in receipt"
        );
        transactionRepository.save(tx);

        auditService.logEvent(
                "BATCH_STOCK_IN",
                "MedicineBatch",
                savedBatch.getId().toString(),
                username,
                String.format("Stocked in %d units for %s (Batch: %s, Expiry: %s)",
                        dto.getQuantity(), medicine.getName(), savedBatch.getBatchNumber(), savedBatch.getExpiryDate())
        );

        return savedBatch;
    }

    @Transactional
    public StockAdjustment adjustStock(StockAdjustmentDto dto, String username) {
        MedicineBatch batch = batchRepository.findByIdWithLock(dto.getBatchId())
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + dto.getBatchId()));

        Medicine medicine = batch.getMedicine();
        int previousStock = medicine.getTotalStock();
        int previousBatchQty = batch.getCurrentQuantity();
        int qty = dto.getQuantity();

        boolean isIncrease = "INCREASE".equalsIgnoreCase(dto.getAdjustmentType());

        if (!isIncrease && previousBatchQty < qty) {
            throw new IllegalArgumentException(String.format(
                    "Cannot decrease by %d units. Batch %s has only %d units available. Negative stock is strictly prevented.",
                    qty, batch.getBatchNumber(), previousBatchQty
            ));
        }

        int newBatchQty = isIncrease ? (previousBatchQty + qty) : (previousBatchQty - qty);
        batch.setCurrentQuantity(newBatchQty);
        batch.setStatus(batch.calculateStatus(LocalDate.now()));
        batchRepository.save(batch);

        // Re-calculate medicine aggregate valid stock
        int newValidStock = batchRepository.calculateValidStockForMedicine(medicine, LocalDate.now());
        medicine.setTotalStock(Math.max(0, newValidStock));
        medicineRepository.save(medicine);

        // Create adjustment record
        String adjNumber = "ADJ-" + System.currentTimeMillis() % 1000000;
        StockAdjustment adjustment = new StockAdjustment(
                adjNumber,
                medicine,
                batch,
                dto.getAdjustmentType().toUpperCase(),
                qty,
                dto.getReason(),
                dto.getRemarks(),
                username
        );
        StockAdjustment savedAdjustment = adjustmentRepository.save(adjustment);

        // Record stock transaction
        StockTransactionType txType = isIncrease ? StockTransactionType.ADJUSTMENT_INCREASE :
                (dto.getReason() == StockAdjustmentReason.EXPIRED_STOCK_REMOVAL ? StockTransactionType.EXPIRED_DISPOSAL :
                        StockTransactionType.ADJUSTMENT_DECREASE);

        String txNumber = generateTransactionNumber();
        StockTransaction tx = new StockTransaction(
                txNumber,
                txType,
                medicine,
                batch,
                qty,
                batch.getUnitPrice(),
                previousStock,
                medicine.getTotalStock(),
                "STOCK_ADJUSTMENT",
                adjNumber,
                username,
                dto.getRemarks() != null ? dto.getRemarks() : "Reason: " + dto.getReason().name()
        );
        transactionRepository.save(tx);

        auditService.logEvent(
                "STOCK_ADJUSTED",
                "StockAdjustment",
                savedAdjustment.getId() != null ? savedAdjustment.getId().toString() : savedAdjustment.getAdjustmentNumber(),
                username,
                String.format("Adjusted batch %s of %s by %s %d units. Reason: %s",
                        batch.getBatchNumber(), medicine.getName(), dto.getAdjustmentType(), qty, dto.getReason())
        );

        return savedAdjustment;
    }

    public synchronized String generateTransactionNumber() {
        return "STX-" + System.currentTimeMillis() % 10000000;
    }
}
