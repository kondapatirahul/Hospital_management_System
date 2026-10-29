package com.healthcare.hospital.enterprise.quality.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CreateQualityActionRequest {
    @NotBlank @Size(max=180) private String title;
    @NotBlank @Size(max=4000) private String description;
    @NotBlank @Size(max=24) private String actionType;
    @NotBlank @Size(max=24) private String priority;
    @NotBlank @Size(max=100) private String ownerUsername;
    private LocalDate dueDate;
    public String getTitle() { return title; }
    public void setTitle(String value) { this.title=value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description=value; }
    public String getActionType() { return actionType; }
    public void setActionType(String value) { this.actionType=value; }
    public String getPriority() { return priority; }
    public void setPriority(String value) { this.priority=value; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String value) { this.ownerUsername=value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate value) { this.dueDate=value; }
}
