package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Tracks the physical specimen lifecycle for a requested laboratory report. */
@Entity
@Table(name = "lab_samples", indexes = {
    @Index(name = "idx_lab_sample_status", columnList = "status"),
    @Index(name = "idx_lab_sample_collected", columnList = "collected_at")
})
public class LabSample {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String sampleCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lab_report_id", nullable = false)
    private LabReport labReport;

    @Column(nullable = false, length = 40)
    private String sampleType;

    @Column(nullable = false, length = 24)
    private String status = "ORDERED";

    @Column(length = 100)
    private String collectedBy;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(length = 255)
    private String rejectionReason;

    @Column(length = 1000)
    private String handlingNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version;

    public LabSample() {}

    public LabSample(String sampleCode, LabReport labReport, String sampleType) {
        this.sampleCode = sampleCode;
        this.labReport = labReport;
        this.sampleType = sampleType;
        this.status = "ORDERED";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSampleCode() { return sampleCode; }
    public void setSampleCode(String sampleCode) { this.sampleCode = sampleCode; }
    public LabReport getLabReport() { return labReport; }
    public void setLabReport(LabReport labReport) { this.labReport = labReport; }
    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCollectedBy() { return collectedBy; }
    public void setCollectedBy(String collectedBy) { this.collectedBy = collectedBy; }
    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getHandlingNotes() { return handlingNotes; }
    public void setHandlingNotes(String handlingNotes) { this.handlingNotes = handlingNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getVersion() { return version; }
}
