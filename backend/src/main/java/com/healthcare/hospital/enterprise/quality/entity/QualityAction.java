package com.healthcare.hospital.enterprise.quality.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Corrective/preventive action with accountable owner and verifiable closure evidence. */
@Entity
@Table(name="clinical_quality_actions", indexes={
    @Index(name="idx_quality_action_case", columnList="review_id"),
    @Index(name="idx_quality_action_status_due", columnList="status,due_date"),
    @Index(name="idx_quality_action_owner", columnList="owner_username")
})
public class QualityAction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="review_id", nullable=false)
    private QualityReview review;
    @Column(nullable=false, length=32, unique=true)
    private String actionReference;
    @Column(nullable=false, length=180)
    private String title;
    @Column(nullable=false, columnDefinition="TEXT")
    private String description;
    @Column(nullable=false, length=24)
    private String actionType="CORRECTIVE";
    @Column(nullable=false, length=24)
    private String priority="NORMAL";
    @Column(nullable=false, length=24)
    private String status="OPEN";
    @Column(name="owner_username", nullable=false, length=100)
    private String ownerUsername;
    @Column(name="created_by", nullable=false, length=100)
    private String createdBy;
    @Column(name="due_date")
    private LocalDate dueDate;
    @Column(name="completed_at")
    private LocalDateTime completedAt;
    @Column(name="verified_at")
    private LocalDateTime verifiedAt;
    @Column(name="verified_by", length=100)
    private String verifiedBy;
    @Column(name="completion_evidence", columnDefinition="TEXT")
    private String completionEvidence;
    @Column(name="verification_notes", columnDefinition="TEXT")
    private String verificationNotes;
    @Column(name="blocked_reason", columnDefinition="TEXT")
    private String blockedReason;
    @Column(nullable=false)
    private LocalDateTime createdAt=LocalDateTime.now();
    @Column(nullable=false)
    private LocalDateTime updatedAt=LocalDateTime.now();
    @Version private Long version;

    public QualityAction() { }
    @PrePersist protected void beforeInsert() { LocalDateTime now=LocalDateTime.now(); if(createdAt==null)createdAt=now; updatedAt=now; }
    @PreUpdate protected void beforeUpdate() { updatedAt=LocalDateTime.now(); }
    public Long getId() { return id; }
    public void setId(Long value) { this.id=value; }
    public QualityReview getReview() { return review; }
    public void setReview(QualityReview value) { this.review=value; }
    public String getActionReference() { return actionReference; }
    public void setActionReference(String value) { this.actionReference=value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { this.title=value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description=value; }
    public String getActionType() { return actionType; }
    public void setActionType(String value) { this.actionType=value; }
    public String getPriority() { return priority; }
    public void setPriority(String value) { this.priority=value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status=value; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String value) { this.ownerUsername=value; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String value) { this.createdBy=value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate value) { this.dueDate=value; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime value) { this.completedAt=value; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime value) { this.verifiedAt=value; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String value) { this.verifiedBy=value; }
    public String getCompletionEvidence() { return completionEvidence; }
    public void setCompletionEvidence(String value) { this.completionEvidence=value; }
    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String value) { this.verificationNotes=value; }
    public String getBlockedReason() { return blockedReason; }
    public void setBlockedReason(String value) { this.blockedReason=value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { this.createdAt=value; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime value) { this.updatedAt=value; }
    public Long getVersion() { return version; }
    public void setVersion(Long value) { this.version=value; }
}
