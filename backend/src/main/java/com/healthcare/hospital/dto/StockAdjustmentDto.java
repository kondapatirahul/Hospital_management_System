package com.healthcare.hospital.dto;

import com.healthcare.hospital.enums.StockAdjustmentReason;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentDto {

    @NotNull(message = "Batch is required")
    private Long batchId;

    @NotBlank(message = "Adjustment type is required")
    private String adjustmentType; // INCREASE, DECREASE

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Reason is required")
    private StockAdjustmentReason reason;

    private String remarks;

    public StockAdjustmentDto() {
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public StockAdjustmentReason getReason() {
        return reason;
    }

    public void setReason(StockAdjustmentReason reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
