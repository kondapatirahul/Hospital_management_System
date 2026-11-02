package com.healthcare.hospital.enterprise.phase5.accessreview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;

/** Persisted periodic least-privilege review and attestation record with optimistic locking and revision tracking. */
@Entity
@Table(name="cf5_accessreview", indexes={@Index(name="idx_cf5_accessreview_status", columnList="status"), @Index(name="idx_cf5_accessreview_due", columnList="dueDate"), @Index(name="idx_cf5_accessreview_ref", columnList="referenceCode")})
public class AccessReview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max=180) @Column(nullable=false,length=180) private String title;
    @Size(max=4000) @Column(length=4000) private String description;
    @NotBlank @Size(max=80) @Column(nullable=false,length=80) private String status = "OPEN";
    @Size(max=100) @Column(length=100) private String referenceCode;
    @Size(max=160) @Column(length=160) private String owner;
    @Size(max=160) @Column(length=160) private String department;
    @Size(max=160) @Column(length=160) private String category;
    @Size(max=160) @Column(length=160) private String priority = "NORMAL";
    private LocalDate dueDate;
    @Size(max=1000) @Column(length=1000) private String evidenceReference;
    @Size(max=2000) @Column(length=2000) private String decisionNote;
    @Column(nullable=false) private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    @Size(max=120) private String createdBy;
    @Size(max=120) private String updatedBy;
    @Column(nullable=false) private int revisionCount = 0;
    @Version private Long version;
    public AccessReview() {}
    public Long getId() { return id; }
    public String getTitle() { return title; } public void setTitle(String v) { title=v==null?null:v.trim(); }
    public String getDescription() { return description; } public void setDescription(String v) { description=v; }
    public String getStatus() { return status; } public void setStatus(String v) { status=v==null?null:v.trim().toUpperCase(); }
    public String getReferenceCode() { return referenceCode; } public void setReferenceCode(String v) { referenceCode=v==null?null:v.trim(); }
    public String getOwner() { return owner; } public void setOwner(String v) { owner=v==null?null:v.trim(); }
    public String getDepartment() { return department; } public void setDepartment(String v) { department=v==null?null:v.trim(); }
    public String getCategory() { return category; } public void setCategory(String v) { category=v==null?null:v.trim(); }
    public String getPriority() { return priority; } public void setPriority(String v) { priority=v==null?null:v.trim(); }
    public LocalDate getDueDate() { return dueDate; } public void setDueDate(LocalDate v) { dueDate=v; }
    public String getEvidenceReference() { return evidenceReference; } public void setEvidenceReference(String v) { evidenceReference=v; }
    public String getDecisionNote() { return decisionNote; } public void setDecisionNote(String v) { decisionNote=v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt=v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { updatedAt=v; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String v) { createdBy=v; }
    public String getUpdatedBy() { return updatedBy; } public void setUpdatedBy(String v) { updatedBy=v; }
    public int getRevisionCount() { return revisionCount; } public void setRevisionCount(int v) { revisionCount=v; }
    public Long getVersion() { return version; }
}
