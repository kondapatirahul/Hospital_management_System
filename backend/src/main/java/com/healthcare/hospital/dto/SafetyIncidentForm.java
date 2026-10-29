package com.healthcare.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/** Form contract: validation stays separate from persistence details. */
public class SafetyIncidentForm {
    @NotBlank @Size(max = 140)
    private String title;
    @NotBlank @Size(max = 36)
    private String category;
    @NotBlank @Size(max = 24)
    private String severity = "LOW";
    @NotBlank @Size(max = 100)
    private String department;
    @Size(max = 100)
    private String assignedTo;
    @Size(max = 30)
    private String patientReference;
    @Size(max = 100)
    private String location;
    @NotBlank @Size(max = 3000)
    private String description;
    @Size(max = 3000)
    private String immediateAction;
    private LocalDateTime occurredAt = LocalDateTime.now();
    private boolean confidential = true;

    public String getTitle() { return title; }
    public void setTitle(String value) { title = value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = value; }
    public String getSeverity() { return severity; }
    public void setSeverity(String value) { severity = value; }
    public String getDepartment() { return department; }
    public void setDepartment(String value) { department = value; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String value) { assignedTo = value; }
    public String getPatientReference() { return patientReference; }
    public void setPatientReference(String value) { patientReference = value; }
    public String getLocation() { return location; }
    public void setLocation(String value) { location = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    public String getImmediateAction() { return immediateAction; }
    public void setImmediateAction(String value) { immediateAction = value; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime value) { occurredAt = value; }
    public boolean isConfidential() { return confidential; }
    public void setConfidential(boolean value) { confidential = value; }
}
