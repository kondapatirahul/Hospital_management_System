package com.healthcare.hospital.enterprise.quality.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Validated input contract for opening a clinical quality case. */
public class CreateQualityReviewRequest {
    @NotBlank @Size(max=160) private String title;
    @NotBlank @Size(max=32) private String category;
    @NotBlank @Size(max=24) private String severity;
    @NotBlank @Size(max=5000) private String eventDescription;
    @Size(max=80) private String patientReference;
    @Size(max=160) private String patientDisplayName;
    @Size(max=100) private String location;
    @Size(max=100) private String department;
    @Size(max=100) private String ownerUsername;
    private LocalDate eventDate;
    private LocalDate dueDate;
    private boolean patientSafetyImpact;
    private boolean requiresDisclosure;
    private boolean regulatoryNotification;
    private boolean sentinelEvent;
    private boolean privacySensitive;
    private boolean followUpRequired;
    private boolean evidencePreserved;
    public String getTitle() { return title; }
    public void setTitle(String value) { this.title=value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { this.category=value; }
    public String getSeverity() { return severity; }
    public void setSeverity(String value) { this.severity=value; }
    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String value) { this.eventDescription=value; }
    public String getPatientReference() { return patientReference; }
    public void setPatientReference(String value) { this.patientReference=value; }
    public String getPatientDisplayName() { return patientDisplayName; }
    public void setPatientDisplayName(String value) { this.patientDisplayName=value; }
    public String getLocation() { return location; }
    public void setLocation(String value) { this.location=value; }
    public String getDepartment() { return department; }
    public void setDepartment(String value) { this.department=value; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String value) { this.ownerUsername=value; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate value) { this.eventDate=value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate value) { this.dueDate=value; }
    public boolean getPatientSafetyImpact() { return patientSafetyImpact; }
    public boolean isPatientSafetyImpact() { return patientSafetyImpact; }
    public void setPatientSafetyImpact(boolean value) { this.patientSafetyImpact=value; }
    public boolean getRequiresDisclosure() { return requiresDisclosure; }
    public boolean isRequiresDisclosure() { return requiresDisclosure; }
    public void setRequiresDisclosure(boolean value) { this.requiresDisclosure=value; }
    public boolean getRegulatoryNotification() { return regulatoryNotification; }
    public boolean isRegulatoryNotification() { return regulatoryNotification; }
    public void setRegulatoryNotification(boolean value) { this.regulatoryNotification=value; }
    public boolean getSentinelEvent() { return sentinelEvent; }
    public boolean isSentinelEvent() { return sentinelEvent; }
    public void setSentinelEvent(boolean value) { this.sentinelEvent=value; }
    public boolean getPrivacySensitive() { return privacySensitive; }
    public boolean isPrivacySensitive() { return privacySensitive; }
    public void setPrivacySensitive(boolean value) { this.privacySensitive=value; }
    public boolean getFollowUpRequired() { return followUpRequired; }
    public boolean isFollowUpRequired() { return followUpRequired; }
    public void setFollowUpRequired(boolean value) { this.followUpRequired=value; }
    public boolean getEvidencePreserved() { return evidencePreserved; }
    public boolean isEvidencePreserved() { return evidencePreserved; }
    public void setEvidencePreserved(boolean value) { this.evidencePreserved=value; }
}
