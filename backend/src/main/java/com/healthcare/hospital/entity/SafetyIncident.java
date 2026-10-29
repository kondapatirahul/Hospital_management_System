package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** A traceable safety event used by clinical and operational teams. */
@Entity
@Table(name = "safety_incidents", indexes = {
    @Index(name = "idx_safety_incident_status", columnList = "status"),
    @Index(name = "idx_safety_incident_severity", columnList = "severity"),
    @Index(name = "idx_safety_incident_occurred", columnList = "occurred_at"),
    @Index(name = "idx_safety_incident_department", columnList = "department")
})
public class SafetyIncident {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 32)
    private String referenceCode;
    @Column(nullable = false, length = 140)
    private String title;
    @Column(nullable = false, length = 36)
    private String category;
    @Column(nullable = false, length = 24)
    private String severity = "LOW";
    @Column(nullable = false, length = 24)
    private String status = "REPORTED";
    @Column(nullable = false, length = 100)
    private String department;
    @Column(nullable = false, length = 100)
    private String reportedBy;
    @Column(length = 100)
    private String assignedTo;
    @Column(length = 30)
    private String patientReference;
    @Column(length = 100)
    private String location;
    @Column(nullable = false, length = 3000)
    private String description;
    @Column(length = 3000)
    private String immediateAction;
    @Column(length = 3000)
    private String rootCause;
    @Column(length = 3000)
    private String correctiveAction;
    @Column(length = 3000)
    private String preventionPlan;
    @Column(length = 2000)
    private String reviewNotes;
    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
    @Column(nullable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    @Column(length = 100)
    private String acknowledgedBy;
    @Column(length = 100)
    private String resolvedBy;
    @Column(nullable = false)
    private boolean confidential = true;
    @Version
    private Long version;

    public SafetyIncident() { }
    public Long getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String value) { referenceCode = value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { title = value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = value; }
    public String getSeverity() { return severity; }
    public void setSeverity(String value) { severity = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getDepartment() { return department; }
    public void setDepartment(String value) { department = value; }
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String value) { reportedBy = value; }
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
    public String getRootCause() { return rootCause; }
    public void setRootCause(String value) { rootCause = value; }
    public String getCorrectiveAction() { return correctiveAction; }
    public void setCorrectiveAction(String value) { correctiveAction = value; }
    public String getPreventionPlan() { return preventionPlan; }
    public void setPreventionPlan(String value) { preventionPlan = value; }
    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String value) { reviewNotes = value; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime value) { occurredAt = value; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime value) { reportedAt = value; }
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime value) { acknowledgedAt = value; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime value) { resolvedAt = value; }
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String value) { acknowledgedBy = value; }
    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String value) { resolvedBy = value; }
    public boolean isConfidential() { return confidential; }
    public void setConfidential(boolean value) { confidential = value; }
    public Long getVersion() { return version; }
}
