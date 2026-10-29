package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SurgeryConsumableDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.StockTransactionType;
import com.healthcare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SurgeryInventoryService {

    private final SurgeryConsumableUsageRepository consumableRepository;
    private final SurgeryBookingRepository bookingRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository batchRepository;
    private final StockTransactionRepository transactionRepository;
    private final SurgeryAuditTrailRepository auditTrailRepository;

    public SurgeryInventoryService(SurgeryConsumableUsageRepository consumableRepository,
                                   SurgeryBookingRepository bookingRepository,
                                   MedicineRepository medicineRepository,
                                   MedicineBatchRepository batchRepository,
                                   StockTransactionRepository transactionRepository,
                                   SurgeryAuditTrailRepository auditTrailRepository) {
        this.consumableRepository = consumableRepository;
        this.bookingRepository = bookingRepository;
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.transactionRepository = transactionRepository;
        this.auditTrailRepository = auditTrailRepository;
    }

    public List<SurgeryConsumableUsage> getConsumablesForSurgery(SurgeryBooking booking) {
        return consumableRepository.findBySurgeryBooking(booking);
    }

    @Transactional
    public SurgeryConsumableUsage addConsumable(Long surgeryId, SurgeryConsumableDto dto, String username) {
        SurgeryBooking booking = bookingRepository.findById(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        Medicine medicine = null;
        MedicineBatch batch = null;
        BigDecimal price = dto.getUnitPrice();
        String itemName = dto.getCustomItemName();

        if (dto.getMedicineId() != null) {
            medicine = medicineRepository.findById(dto.getMedicineId())
                    .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + dto.getMedicineId()));
            itemName = medicine.getName();
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                price = medicine.getUnitPrice();
            }
        }

        if (dto.getBatchId() != null) {
            batch = batchRepository.findById(dto.getBatchId())
                    .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + dto.getBatchId()));
            if (medicine != null && !batch.getMedicine().getId().equals(medicine.getId())) {
                throw new IllegalArgumentException("Selected batch does not belong to the selected medicine.");
            }
            if (medicine == null) {
                medicine = batch.getMedicine();
                itemName = medicine.getName();
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                price = batch.getUnitPrice();
            }
        }

        if (itemName == null || itemName.trim().isBlank()) {
            throw new IllegalArgumentException("Item name or medicine must be specified.");
        }
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        SurgeryConsumableUsage usage = new SurgeryConsumableUsage(
                booking, medicine, batch, itemName.trim(), dto.getQuantity(), price, username
        );
        SurgeryConsumableUsage saved = consumableRepository.save(usage);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                booking, null, "CONSUMABLE_RECORDED", username,
                String.format("Recorded consumable usage: %s x %d (Total: $%s)",
                        saved.getItemName(), saved.getQuantity(), saved.getTotalCost())
        );
        auditTrailRepository.save(audit);

        return saved;
    }

    /**
     * Deducts inventory stock for recorded consumables with concurrency safety and duplicate prevention.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int deductStockForSurgery(Long surgeryId, String username) {
        SurgeryBooking booking = bookingRepository.findById(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        List<SurgeryConsumableUsage> pending = consumableRepository.findBySurgeryBookingAndIsDeductedFalse(booking);
        if (pending.isEmpty()) {
            return 0; // Already deducted or no consumables
        }

        LocalDate today = LocalDate.now();
        int deductedCount = 0;

        for (SurgeryConsumableUsage usage : pending) {
            if (usage.isDeducted()) {
                continue; // Guard against duplicate deductions
            }

            Medicine medicine = usage.getMedicine();
            if (medicine == null) {
                // Non-medicine custom surgical item (e.g. specialized suture, implant, pack)
                usage.setDeducted(true);
                consumableRepository.save(usage);
                deductedCount++;
                continue;
            }

            MedicineBatch batch = usage.getBatch();
            if (batch == null) {
                // Select active batch by FEFO
                List<MedicineBatch> activeBatches = batchRepository.findEligibleBatchesForDispensing(medicine, today);
                if (activeBatches.isEmpty()) {
                    throw new IllegalStateException("No active stock batch available for medicine: " + medicine.getName());
                }
                batch = activeBatches.get(0);
            }

            // Lock batch
            batch = batchRepository.findByIdWithLock(batch.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + usage.getBatch().getId()));

            if (batch.getCurrentQuantity() < usage.getQuantity()) {
                throw new IllegalStateException(String.format(
                        "Insufficient stock for %s in batch %s: requested %d, available %d.",
                        medicine.getName(), batch.getBatchNumber(), usage.getQuantity(), batch.getCurrentQuantity()
                ));
            }

            int prevStock = medicine.getTotalStock();
            batch.setCurrentQuantity(batch.getCurrentQuantity() - usage.getQuantity());
            batch.setStatus(batch.calculateStatus(today));
            batchRepository.save(batch);

            int newStock = batchRepository.calculateValidStockForMedicine(medicine, today);
            medicine.setTotalStock(Math.max(0, newStock));
            medicineRepository.save(medicine);

            // Record transaction
            String txNumber = "STX-OT-" + System.currentTimeMillis() % 10000000;
            StockTransaction tx = new StockTransaction(
                    txNumber,
                    StockTransactionType.DISPENSED,
                    medicine,
                    batch,
                    usage.getQuantity(),
                    usage.getUnitPrice(),
                    prevStock,
                    medicine.getTotalStock(),
                    "OPERATION_THEATRE",
                    booking.getBookingNumber(),
                    username,
                    "Consumables deducted for Surgery: " + booking.getProcedureName() + " (" + booking.getBookingNumber() + ")"
            );
            transactionRepository.save(tx);

            usage.setBatch(batch);
            usage.setDeducted(true);
            consumableRepository.save(usage);
            deductedCount++;
        }

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                booking, null, "STOCK_DEDUCTION", username,
                String.format("Deducted %d consumable item(s) from inventory for surgery %s",
                        deductedCount, booking.getBookingNumber())
        );
        auditTrailRepository.save(audit);

        return deductedCount;
    }
}
