package com.healthcare.hospital.modules.legacy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_feedback", uniqueConstraints = {
        @UniqueConstraint(name = "uk_patient_feedback_appointment", columnNames = "appointment_id")
}, indexes = {
        @Index(name = "idx_patient_feedback_patient", columnList = "patient_id")
})
public class PatientFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "doctor_rating", nullable = false)
    private int doctorRating;

    @Column(name = "service_rating", nullable = false)
    private int serviceRating;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected PatientFeedback() {
    }

    public PatientFeedback(Patient patient, Appointment appointment, int doctorRating,
                           int serviceRating, String comments) {
        this.patient = patient;
        this.appointment = appointment;
        this.doctorRating = doctorRating;
        this.serviceRating = serviceRating;
        this.comments = comments;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Appointment getAppointment() { return appointment; }
    public int getDoctorRating() { return doctorRating; }
    public int getServiceRating() { return serviceRating; }
    public String getComments() { return comments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
