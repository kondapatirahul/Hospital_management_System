package com.healthcare.hospital.shared.dto;

import java.math.BigDecimal;

public record MedicineReorderRecommendationDto(
        Long medicineId,
        String medicineCode,
        String medicineName,
        String unitOfMeasure,
        int currentStock,
        int reorderLevel,
        long quantityOnOpenPurchaseOrders,
        int suggestedOrderQuantity,
        BigDecimal estimatedPurchaseCost
) {
}
