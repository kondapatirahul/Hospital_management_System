package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Physical bed inventory for ward operations.
 * A bed can be AVAILABLE, OCCUPIED, CLEANING, or OUT_OF_SERVICE.
 */
@Entity
@Table(name = "ward_beds", indexes = {
    @Index(name = "idx_ward_bed_ward", columnList = "ward_name"),
    @Index(name = "idx_ward_bed_status", columnList = "status")
}, uniqueConstraints = @UniqueConstraint(name = "uk_ward_bed_number", columnNames = {"ward_name", "bed_number"}))
public class WardBed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ward_name", nullable = false, length = 100)
    private String wardName;

    @Column(name = "bed_number", nullable = false, length = 40)
    private String bedNumber;

    @Column(name = "bed_type", nullable = false, length = 40)
    private String bedType = "GENERAL";

    @Column(nullable = false, length = 24)
    private String status = "AVAILABLE";

    @Column(name = "patient_name", length = 120)
    private String patientName;

    @Column(name = "patient_identifier", length = 40)
    private String patientIdentifier;

    @Column(name = "admission_notes", length = 1500)
    private String admissionNotes;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "last_status_change", nullable = false)
    private LocalDateTime lastStatusChange = LocalDateTime.now();

    @Version
    private Long version;

    public WardBed() {}

    public Long getId() { return id; }
    public String getWardName() { return wardName; }
    public void setWardName(String wardName) { this.wardName = wardName; }
    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientIdentifier() { return patientIdentifier; }
    public void setPatientIdentifier(String patientIdentifier) { this.patientIdentifier = patientIdentifier; }
    public String getAdmissionNotes() { return admissionNotes; }
    public void setAdmissionNotes(String admissionNotes) { this.admissionNotes = admissionNotes; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public LocalDateTime getLastStatusChange() { return lastStatusChange; }
    public void setLastStatusChange(LocalDateTime lastStatusChange) { this.lastStatusChange = lastStatusChange; }
    public Long getVersion() { return version; }
}
