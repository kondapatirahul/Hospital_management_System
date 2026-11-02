package com.healthcare.hospital.enterprise.phase4.purchaseapproval.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Persisted purchase approvals workflow record. Status transitions are enforced by its service. */
@Entity(name = "Phase4PurchaseApproval")
@Table(name = "cf4_purchaseapproval", indexes = {@Index(name="idx_cf4_purchaseapproval_status", columnList="status"), @Index(name="idx_cf4_purchaseapproval_reference", columnList="referenceCode")})
public class PurchaseApproval {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max=180) @Column(nullable=false,length=180) private String title;
    @Size(max=4000) @Column(length=4000) private String description;
    @NotBlank @Size(max=80) @Column(nullable=false,length=80) private String status = "REQUESTED";
    @Size(max=100) @Column(length=100) private String referenceCode;
    @Size(max=160) @Column(length=160) private String owner;
    @Size(max=160) @Column(length=160) private String department;
    @Size(max=160) @Column(length=160) private String counterparty;
    @DecimalMin("0.00") @Digits(integer=12,fraction=2) @Column(precision=14,scale=2) private BigDecimal amount = BigDecimal.ZERO;
    private LocalDate dueDate;
    @Size(max=1000) @Column(length=1000) private String evidenceReference;
    @Size(max=1000) @Column(length=1000) private String decisionNote;
    @Column(nullable=false) private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    @Size(max=120) private String createdBy;
    @Size(max=120) private String updatedBy;
    @Version private Long version;
    public PurchaseApproval() {}
    public Long getId() { return id; } public void setId(Long v) { id=v; }
    public String getTitle() { return title; } public void setTitle(String v) { title=v==null?null:v.trim(); }
    public String getDescription() { return description; } public void setDescription(String v) { description=v; }
    public String getStatus() { return status; } public void setStatus(String v) { status=v==null?null:v.trim().toUpperCase(); }
    public String getReferenceCode() { return referenceCode; } public void setReferenceCode(String v) { referenceCode=v==null?null:v.trim(); }
    public String getOwner() { return owner; } public void setOwner(String v) { owner=v==null?null:v.trim(); }
    public String getDepartment() { return department; } public void setDepartment(String v) { department=v==null?null:v.trim(); }
    public String getCounterparty() { return counterparty; } public void setCounterparty(String v) { counterparty=v==null?null:v.trim(); }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal v) { amount=v; }
    public LocalDate getDueDate() { return dueDate; } public void setDueDate(LocalDate v) { dueDate=v; }
    public String getEvidenceReference() { return evidenceReference; } public void setEvidenceReference(String v) { evidenceReference=v; }
    public String getDecisionNote() { return decisionNote; } public void setDecisionNote(String v) { decisionNote=v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt=v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { updatedAt=v; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String v) { createdBy=v; }
    public String getUpdatedBy() { return updatedBy; } public void setUpdatedBy(String v) { updatedBy=v; }
    public Long getVersion() { return version; }
}
