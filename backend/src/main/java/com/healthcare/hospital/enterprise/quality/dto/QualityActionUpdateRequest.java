package com.healthcare.hospital.enterprise.quality.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class QualityActionUpdateRequest {
    @Size(max=100) private String ownerUsername;
    @Size(max=4000) private String description;
    @Size(max=1500) private String blockedReason;
    @Size(max=4000) private String completionEvidence;
    @Size(max=2500) private String verificationNotes;
    private LocalDate dueDate;
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String value) { this.ownerUsername=value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description=value; }
    public String getBlockedReason() { return blockedReason; }
    public void setBlockedReason(String value) { this.blockedReason=value; }
    public String getCompletionEvidence() { return completionEvidence; }
    public void setCompletionEvidence(String value) { this.completionEvidence=value; }
    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String value) { this.verificationNotes=value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate value) { this.dueDate=value; }
}
