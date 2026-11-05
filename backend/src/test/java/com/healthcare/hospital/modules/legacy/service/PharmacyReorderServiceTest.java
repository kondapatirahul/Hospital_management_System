package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.modules.legacy.entity.Medicine;
import com.healthcare.hospital.modules.legacy.repository.MedicineRepository;
import com.healthcare.hospital.modules.legacy.repository.PurchaseOrderItemRepository;
import com.healthcare.hospital.shared.dto.MedicineReorderRecommendationDto;
import com.healthcare.hospital.shared.enums.PurchaseOrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PharmacyReorderServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @InjectMocks
    private PharmacyReorderService reorderService;

    @Test
    void recommendationAccountsForOpenPurchaseOrdersAndCalculatesCost() {
        Medicine medicine = medicine(2L, 3, 10, "7.25");
        when(medicineRepository.findActiveMedicinesNeedingReorder()).thenReturn(List.of(medicine));
        when(purchaseOrderItemRepository.sumUnreceivedQuantityForMedicineAndOrderStatusIn(
                medicine, List.of(PurchaseOrderStatus.ORDERED, PurchaseOrderStatus.PARTIALLY_RECEIVED)))
                .thenReturn(4L);

        List<MedicineReorderRecommendationDto> results = reorderService.getRecommendations();

        assertEquals(1, results.size());
        assertEquals(13, results.get(0).suggestedOrderQuantity());
        assertEquals(4L, results.get(0).quantityOnOpenPurchaseOrders());
        assertEquals(new BigDecimal("94.25"), results.get(0).estimatedPurchaseCost());
    }

    @Test
    void recommendationIsZeroWhenOpenOrdersCoverTargetStock() {
        Medicine medicine = medicine(3L, 1, 10, "4.00");
        when(medicineRepository.findActiveMedicinesNeedingReorder()).thenReturn(List.of(medicine));
        when(purchaseOrderItemRepository.sumUnreceivedQuantityForMedicineAndOrderStatusIn(
                medicine, List.of(PurchaseOrderStatus.ORDERED, PurchaseOrderStatus.PARTIALLY_RECEIVED)))
                .thenReturn(25L);

        List<MedicineReorderRecommendationDto> results = reorderService.getRecommendations();

        assertEquals(0, results.get(0).suggestedOrderQuantity());
        assertEquals(BigDecimal.ZERO, results.get(0).estimatedPurchaseCost());
    }

    private Medicine medicine(long id, int currentStock, int reorderLevel, String purchasePrice) {
        Medicine medicine = new Medicine();
        medicine.setId(id);
        medicine.setMedicineCode("MED-" + id);
        medicine.setName("Medicine " + id);
        medicine.setUnitOfMeasure("box");
        medicine.setTotalStock(currentStock);
        medicine.setReorderLevel(reorderLevel);
        medicine.setPurchasePrice(new BigDecimal(purchasePrice));
        return medicine;
    }
}
