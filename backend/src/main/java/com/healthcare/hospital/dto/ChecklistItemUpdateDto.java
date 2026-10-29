package com.healthcare.hospital.dto;

import com.healthcare.hospital.enums.ChecklistStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChecklistItemUpdateDto {

    @NotNull(message = "Status is required")
    private ChecklistStatus status;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    private String remarks;

    public ChecklistItemUpdateDto() {
    }

    public ChecklistStatus getStatus() {
        return status;
    }

    public void setStatus(ChecklistStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
