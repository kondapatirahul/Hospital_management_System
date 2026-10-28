package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
public class Prescription {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @Column(nullable = false, length = 150)
    private String medicationName;

    @Column(nullable = false, length = 100)
    private String dosage; // e.g. "500 mg"

    @Column(nullable = false, length = 100)
    private String frequency; // e.g. "Twice daily after meals"

    @Column(length = 100)
    private String duration; // e.g. "7 days"

    @Column(length = 255)
    private String instructions; // e.g. "Drink plenty of fluids, do not skip doses"

    @Column(length = 30)
    private String status = "Active"; // Active, Completed

    @Column(name = "prescribed_quantity")
    private Integer prescribedQuantity = 10;

    @Column(name = "dispensed_quantity")
    private Integer dispensedQuantity = 0;

    @Column(name = "dispense_status", length = 30)
    private String dispenseStatus = "PENDING"; // PENDING, PARTIALLY_DISPENSED, DISPENSED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Prescription() {
    }

    public Prescription(Patient patient, Doctor doctor, LocalDate prescriptionDate, String medicationName, String dosage, String frequency, String duration, String instructions, String status) {
        this.patient = patient;
        this.doctor = doctor;
        this.prescriptionDate = prescriptionDate;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
        this.status = status != null ? status : "Active";
        this.prescribedQuantity = calculateDefaultQuantity(duration, frequency);
        this.dispensedQuantity = 0;
        this.dispenseStatus = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Prescription(Patient patient, Doctor doctor, Appointment appointment, MedicalRecord medicalRecord, LocalDate prescriptionDate, String medicationName, String dosage, String frequency, String duration, String instructions, String status) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointment = appointment;
        this.medicalRecord = medicalRecord;
        this.prescriptionDate = prescriptionDate;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
        this.status = status != null ? status : "Active";
        this.prescribedQuantity = calculateDefaultQuantity(duration, frequency);
        this.dispensedQuantity = 0;
        this.dispenseStatus = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    private int calculateDefaultQuantity(String duration, String frequency) {
        // Sensible default based on duration if parseable
        if (duration != null && duration.toLowerCase().contains("day")) {
            String digits = duration.replaceAll("\\D+", "");
            if (!digits.isEmpty()) {
                try {
                    int days = Integer.parseInt(digits);
                    int perDay = 1;
                    if (frequency != null) {
                        String freqLower = frequency.toLowerCase();
                        if (freqLower.contains("twice") || freqLower.contains("2 times") || freqLower.contains("bid")) perDay = 2;
                        else if (freqLower.contains("three") || freqLower.contains("3 times") || freqLower.contains("tid")) perDay = 3;
                        else if (freqLower.contains("four") || freqLower.contains("4 times") || freqLower.contains("qid")) perDay = 4;
                    }
                    return Math.max(1, days * perDay);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 10;
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

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPrescribedQuantity() {
        return prescribedQuantity != null ? prescribedQuantity : 10;
    }

    public void setPrescribedQuantity(Integer prescribedQuantity) {
        this.prescribedQuantity = prescribedQuantity;
    }

    public Integer getDispensedQuantity() {
        return dispensedQuantity != null ? dispensedQuantity : 0;
    }

    public void setDispensedQuantity(Integer dispensedQuantity) {
        this.dispensedQuantity = dispensedQuantity;
    }

    public String getDispenseStatus() {
        return dispenseStatus != null ? dispenseStatus : "PENDING";
    }

    public void setDispenseStatus(String dispenseStatus) {
        this.dispenseStatus = dispenseStatus;
    }

    public int getRemainingQuantity() {
        return Math.max(0, getPrescribedQuantity() - getDispensedQuantity());
    }

    public boolean isFullyDispensed() {
        return getRemainingQuantity() == 0 || "DISPENSED".equalsIgnoreCase(dispenseStatus);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
