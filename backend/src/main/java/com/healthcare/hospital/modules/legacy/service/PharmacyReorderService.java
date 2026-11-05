package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.modules.legacy.entity.Medicine;
import com.healthcare.hospital.modules.legacy.repository.MedicineRepository;
import com.healthcare.hospital.modules.legacy.repository.PurchaseOrderItemRepository;
import com.healthcare.hospital.shared.dto.MedicineReorderRecommendationDto;
import com.healthcare.hospital.shared.enums.PurchaseOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PharmacyReorderService {

    private static final List<PurchaseOrderStatus> OPEN_ORDER_STATUSES =
            List.of(PurchaseOrderStatus.ORDERED, PurchaseOrderStatus.PARTIALLY_RECEIVED);

    private final MedicineRepository medicineRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    public PharmacyReorderService(MedicineRepository medicineRepository,
                                  PurchaseOrderItemRepository purchaseOrderItemRepository) {
        this.medicineRepository = medicineRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicineReorderRecommendationDto> getRecommendations() {
        return medicineRepository.findActiveMedicinesNeedingReorder().stream()
                .map(this::recommend)
                .toList();
    }

    private MedicineReorderRecommendationDto recommend(Medicine medicine) {
        int reorderLevel = Math.max(0, medicine.getReorderLevel() == null ? 0 : medicine.getReorderLevel());
        int currentStock = Math.max(0, medicine.getTotalStock() == null ? 0 : medicine.getTotalStock());
        long incoming = Math.max(0, purchaseOrderItemRepository
                .sumUnreceivedQuantityForMedicineAndOrderStatusIn(medicine, OPEN_ORDER_STATUSES));

        long targetStock = Math.max(1L, (long) reorderLevel * 2);
        long uncoveredNeed = Math.max(0L, targetStock - currentStock - incoming);
        int suggestedQuantity = (int) Math.min(Integer.MAX_VALUE, uncoveredNeed);
        BigDecimal unitCost = medicine.getPurchasePrice() == null ? BigDecimal.ZERO : medicine.getPurchasePrice();

        return new MedicineReorderRecommendationDto(
                medicine.getId(),
                medicine.getMedicineCode(),
                medicine.getName(),
                medicine.getUnitOfMeasure(),
                currentStock,
                reorderLevel,
                incoming,
                suggestedQuantity,
                unitCost.multiply(BigDecimal.valueOf(suggestedQuantity))
        );
    }
}
