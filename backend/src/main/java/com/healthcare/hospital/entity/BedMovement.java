package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Historical record of a bed status or patient assignment transition. */
@Entity
@Table(name = "bed_movements", indexes = {
    @Index(name = "idx_bed_movement_time", columnList = "changed_at"),
    @Index(name = "idx_bed_movement_bed", columnList = "bed_id")
})
public class BedMovement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bed_id", nullable = false)
    private WardBed bed;

    @Column(name = "from_status", length = 24)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 24)
    private String toStatus;

    @Column(name = "patient_name", length = 120)
    private String patientName;

    @Column(name = "patient_identifier", length = 40)
    private String patientIdentifier;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(length = 1000)
    private String notes;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    protected BedMovement() {}
    public BedMovement(WardBed bed, String fromStatus, String toStatus, String patientName,
                       String patientIdentifier, String changedBy, String notes) {
        this.bed = bed;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.patientName = patientName;
        this.patientIdentifier = patientIdentifier;
        this.changedBy = changedBy == null || changedBy.isBlank() ? "system" : changedBy;
        this.notes = notes == null ? null : notes.substring(0, Math.min(notes.length(), 1000));
        this.changedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public WardBed getBed() { return bed; }
    public String getFromStatus() { return fromStatus; }
    public String getToStatus() { return toStatus; }
    public String getPatientName() { return patientName; }
    public String getPatientIdentifier() { return patientIdentifier; }
    public String getChangedBy() { return changedBy; }
    public String getNotes() { return notes; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
