package com.healthcare.hospital.enterprise.staff.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Ward round observations.
 * Records are retained as auditable workflow objects and are not physically deleted
 * through the web controller. The status and timestamps describe the business lifecycle.
 */
@Entity
@Table(name = "ward_round")
public class WardRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false, length = 40)
    private String status = "OPEN";

    @Column(length = 120)
    private String referenceCode;

    @Column(length = 120)
    private String assignedTo;

    private LocalDate dueDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Column(length = 120)
    private String createdBy;

    @Column(length = 120)
    private String updatedBy;

    @Version
    private Long version;

    public WardRound() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title == null ? null : title.trim(); }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Long getVersion() { return version; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null || status.isBlank()) status = "OPEN";
        validateRequiredFields();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        validateRequiredFields();
    }

    public void validateRequiredFields() {
        if (title == null || title.isBlank()) throw new IllegalStateException("Title is required");
        if (title.length() > 180) throw new IllegalStateException("Title must be 180 characters or fewer");
        if (status == null || status.isBlank()) throw new IllegalStateException("Status is required");
        if (amount != null && amount.signum() < 0) throw new IllegalStateException("Amount cannot be negative");
    }

    public boolean isOpen() { return "OPEN".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status); }
    public boolean isClosed() { return "CLOSED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status); }

    public void markInProgress(String actor) {
        if (isClosed()) throw new IllegalStateException("Closed records cannot be reopened through this action");
        this.status = "IN_PROGRESS";
        this.updatedBy = actor;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(String actor) {
        if (isClosed()) throw new IllegalStateException("Record is already closed");
        this.status = "COMPLETED";
        this.updatedBy = actor;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String actor, String reason) {
        if (isClosed()) throw new IllegalStateException("Record is already closed");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Cancellation reason is required");
        this.status = "CANCELLED";
        this.description = (this.description == null ? "" : this.description + "\\n") + "Cancellation: " + reason.trim();
        this.updatedBy = actor;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && !isClosed();
    }

    public String getDisplayStatus() {
        if (isOverdue()) return "OVERDUE";
        return status == null ? "UNKNOWN" : status.toUpperCase();
    }
}
