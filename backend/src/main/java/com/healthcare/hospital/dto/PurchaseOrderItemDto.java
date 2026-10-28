package com.healthcare.hospital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PurchaseOrderItemDto {

    private Long id;

    @NotNull(message = "Medicine is required")
    private Long medicineId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer orderedQuantity;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost cannot be negative")
    private BigDecimal unitCost = BigDecimal.ZERO;

    public PurchaseOrderItemDto() {
    }

    public PurchaseOrderItemDto(Long medicineId, Integer orderedQuantity, BigDecimal unitCost) {
        this.medicineId = medicineId;
        this.orderedQuantity = orderedQuantity;
        this.unitCost = unitCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
}
