package com.healthcare.hospital.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DispenseRequestDto {

    @NotNull(message = "Prescription is required")
    private Long prescriptionId;

    @NotNull(message = "Medicine is required")
    private Long medicineId;

    private String remarks;

    @NotEmpty(message = "At least one batch allocation is required")
    @Valid
    private List<BatchAllocationDto> allocations = new ArrayList<>();

    public DispenseRequestDto() {
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<BatchAllocationDto> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<BatchAllocationDto> allocations) {
        this.allocations = allocations;
    }

    public int getTotalQuantity() {
        return allocations.stream()
                .mapToInt(a -> a.getQuantity() != null ? a.getQuantity() : 0)
                .sum();
    }
}
