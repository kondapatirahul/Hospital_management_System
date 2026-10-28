package com.healthcare.hospital.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BatchAllocationDto {

    @NotNull(message = "Batch is required")
    private Long batchId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Dispense quantity must be at least 1")
    private Integer quantity;

    public BatchAllocationDto() {
    }

    public BatchAllocationDto(Long batchId, Integer quantity) {
        this.batchId = batchId;
        this.quantity = quantity;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
