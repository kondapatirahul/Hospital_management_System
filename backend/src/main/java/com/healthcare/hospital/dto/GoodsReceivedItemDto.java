package com.healthcare.hospital.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GoodsReceivedItemDto {

    private Long purchaseOrderItemId;

    @NotNull(message = "Medicine is required")
    private Long medicineId;

    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @NotNull(message = "Quantity received is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantityReceived;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost cannot be negative")
    private BigDecimal unitCost = BigDecimal.ZERO;

    public GoodsReceivedItemDto() {
    }

    public Long getPurchaseOrderItemId() {
        return purchaseOrderItemId;
    }

    public void setPurchaseOrderItemId(Long purchaseOrderItemId) {
        this.purchaseOrderItemId = purchaseOrderItemId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
}
