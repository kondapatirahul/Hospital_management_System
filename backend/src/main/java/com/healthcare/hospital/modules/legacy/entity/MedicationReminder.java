package com.healthcare.hospital.modules.legacy.entity;

import com.healthcare.hospital.shared.enums.MedicationReminderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medication_reminders", uniqueConstraints = {
        @UniqueConstraint(name = "uk_medication_reminder_prescription_time",
                columnNames = {"prescription_id", "scheduled_at"})
}, indexes = {
        @Index(name = "idx_medication_reminder_patient_time", columnList = "patient_id, scheduled_at")
})
public class MedicationReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private MedicationReminderStatus status = MedicationReminderStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    protected MedicationReminder() {
    }

    public MedicationReminder(Patient patient, Prescription prescription, LocalDateTime scheduledAt) {
        this.patient = patient;
        this.prescription = prescription;
        this.scheduledAt = scheduledAt;
        this.status = MedicationReminderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Prescription getPrescription() { return prescription; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public MedicationReminderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    public void record(MedicationReminderStatus value, LocalDateTime recordedAt) {
        this.status = value;
        this.recordedAt = recordedAt;
    }
}
