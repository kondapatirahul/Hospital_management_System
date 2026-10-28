package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(length = 255)
    private String symptoms;

    @Column(nullable = false, length = 255)
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(length = 150)
    private String vitalSigns; // e.g. "BP: 120/80 mmHg, Pulse: 72 bpm, Temp: 98.6 F"

    @Column(length = 255)
    private String followUpInstructions;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public MedicalRecord() {
    }

    public MedicalRecord(Patient patient, Doctor doctor, LocalDate visitDate, String symptoms, String diagnosis, String clinicalNotes, String vitalSigns, String followUpInstructions) {
        this.patient = patient;
        this.doctor = doctor;
        this.visitDate = visitDate;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.clinicalNotes = clinicalNotes;
        this.vitalSigns = vitalSigns;
        this.followUpInstructions = followUpInstructions;
        this.createdAt = LocalDateTime.now();
    }

    public MedicalRecord(Patient patient, Doctor doctor, Appointment appointment, LocalDate visitDate, String symptoms, String diagnosis, String clinicalNotes, String vitalSigns, String followUpInstructions) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointment = appointment;
        this.visitDate = visitDate;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.clinicalNotes = clinicalNotes;
        this.vitalSigns = vitalSigns;
        this.followUpInstructions = followUpInstructions;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }

    public String getVitalSigns() {
        return vitalSigns;
    }

    public void setVitalSigns(String vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    public String getFollowUpInstructions() {
        return followUpInstructions;
    }

    public void setFollowUpInstructions(String followUpInstructions) {
        this.followUpInstructions = followUpInstructions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
