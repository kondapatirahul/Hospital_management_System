package com.healthcare.hospital.enterprise.quality.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Persisted clinical quality case. Workflow state is changed only by QualityReviewService. */
@Entity
@Table(name="clinical_quality_reviews", indexes={
    @Index(name="idx_quality_status_due", columnList="status,due_date"),
    @Index(name="idx_quality_patient_ref", columnList="patient_reference"),
    @Index(name="idx_quality_owner", columnList="owner_username"),
    @Index(name="idx_quality_category", columnList="category")
})
public class QualityReview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="case_reference", nullable=false, unique=true, length=32)
    private String caseReference;
    @Column(nullable=false, length=160)
    private String title;
    @Column(nullable=false, length=32)
    private String category;
    @Column(nullable=false, length=24)
    private String severity="MODERATE";
    @Column(nullable=false, length=32)
    private String status="DRAFT";
    @Column(name="patient_reference", length=80)
    private String patientReference;
    @Column(name="patient_display_name", length=160)
    private String patientDisplayName;
    @Column(nullable=false, columnDefinition="TEXT")
    private String eventDescription;
    @Column(columnDefinition="TEXT")
    private String immediateContainment;
    @Column(columnDefinition="TEXT")
    private String contributingFactors;
    @Column(columnDefinition="TEXT")
    private String rootCause;
    @Column(columnDefinition="TEXT")
    private String correctivePlan;
    @Column(columnDefinition="TEXT")
    private String preventionPlan;
    @Column(columnDefinition="TEXT")
    private String learningSummary;
    @Column(length=100)
    private String location;
    @Column(length=100)
    private String department;
    @Column(name="owner_username", length=100)
    private String ownerUsername;
    @Column(name="reporter_username", nullable=false, length=100)
    private String reporterUsername;
    @Column(name="reviewer_username", length=100)
    private String reviewerUsername;
    @Column(name="reported_at", nullable=false)
    private LocalDateTime reportedAt=LocalDateTime.now();
    @Column(name="event_date")
    private LocalDate eventDate;
    @Column(name="due_date")
    private LocalDate dueDate;
    @Column(name="reviewed_at")
    private LocalDateTime reviewedAt;
    @Column(name="closed_at")
    private LocalDateTime closedAt;
    @Column(name="closure_summary", columnDefinition="TEXT")
    private String closureSummary;
    @Column(nullable=false)
    private boolean patientSafetyImpact;
    @Column(nullable=false)
    private boolean requiresDisclosure;
    @Column(nullable=false)
    private boolean regulatoryNotification;
    @Column(nullable=false)
    private boolean sentinelEvent;
    @Column(nullable=false)
    private boolean privacySensitive;
    @Column(nullable=false)
    private boolean followUpRequired;
    @Column(nullable=false)
    private boolean evidencePreserved;
    @Column(nullable=false)
    private int revisionNumber;
    @Column(nullable=false)
    private LocalDateTime createdAt=LocalDateTime.now();
    @Column(nullable=false)
    private LocalDateTime updatedAt=LocalDateTime.now();
    @Version
    private Long version;

    public QualityReview() { }

    @PrePersist
    protected void beforeInsert() {
        LocalDateTime now=LocalDateTime.now();
        if (createdAt==null) createdAt=now;
        if (reportedAt==null) reportedAt=now;
        updatedAt=now;
        if (status==null || status.isBlank()) status="DRAFT";
    }

    @PreUpdate
    protected void beforeUpdate() {
        updatedAt=LocalDateTime.now();
        revisionNumber++;
    }

    public Long getId() { return id; }
    public void setId(Long value) { this.id=value; }
    public String getCaseReference() { return caseReference; }
    public void setCaseReference(String value) { this.caseReference=value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { this.title=value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { this.category=value; }
    public String getSeverity() { return severity; }
    public void setSeverity(String value) { this.severity=value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status=value; }
    public String getPatientReference() { return patientReference; }
    public void setPatientReference(String value) { this.patientReference=value; }
    public String getPatientDisplayName() { return patientDisplayName; }
    public void setPatientDisplayName(String value) { this.patientDisplayName=value; }
    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String value) { this.eventDescription=value; }
    public String getImmediateContainment() { return immediateContainment; }
    public void setImmediateContainment(String value) { this.immediateContainment=value; }
    public String getContributingFactors() { return contributingFactors; }
    public void setContributingFactors(String value) { this.contributingFactors=value; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String value) { this.rootCause=value; }
    public String getCorrectivePlan() { return correctivePlan; }
    public void setCorrectivePlan(String value) { this.correctivePlan=value; }
    public String getPreventionPlan() { return preventionPlan; }
    public void setPreventionPlan(String value) { this.preventionPlan=value; }
    public String getLearningSummary() { return learningSummary; }
    public void setLearningSummary(String value) { this.learningSummary=value; }
    public String getLocation() { return location; }
    public void setLocation(String value) { this.location=value; }
    public String getDepartment() { return department; }
    public void setDepartment(String value) { this.department=value; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String value) { this.ownerUsername=value; }
    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String value) { this.reporterUsername=value; }
    public String getReviewerUsername() { return reviewerUsername; }
    public void setReviewerUsername(String value) { this.reviewerUsername=value; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime value) { this.reportedAt=value; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate value) { this.eventDate=value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate value) { this.dueDate=value; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime value) { this.reviewedAt=value; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime value) { this.closedAt=value; }
    public String getClosureSummary() { return closureSummary; }
    public void setClosureSummary(String value) { this.closureSummary=value; }
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
    public int getRevisionNumber() { return revisionNumber; }
    public void setRevisionNumber(int value) { this.revisionNumber=value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { this.createdAt=value; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime value) { this.updatedAt=value; }
    public Long getVersion() { return version; }
    public void setVersion(Long value) { this.version=value; }
}
