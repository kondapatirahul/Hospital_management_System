package com.healthcare.hospital.enterprise.quality.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CreateQualityCommentRequest {
    @NotBlank @Size(max=5000) private String body;
    @NotBlank @Size(max=24) private String visibility;
    @NotBlank @Size(max=24) private String commentType;
    private boolean actionRequired;
    @Size(max=100) private String assignedTo;
    private LocalDate followUpDate;
    public String getBody() { return body; }
    public void setBody(String value) { this.body=value; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String value) { this.visibility=value; }
    public String getCommentType() { return commentType; }
    public void setCommentType(String value) { this.commentType=value; }
    public boolean getActionRequired() { return actionRequired; }
    public boolean isActionRequired() { return actionRequired; }
    public void setActionRequired(boolean value) { this.actionRequired=value; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String value) { this.assignedTo=value; }
    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate value) { this.followUpDate=value; }
}
