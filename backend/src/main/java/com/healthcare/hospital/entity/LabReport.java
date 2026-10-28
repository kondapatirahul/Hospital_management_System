package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.LabReportStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_reports")
public class LabReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(nullable = false, length = 150)
    private String testName;

    @Column(length = 100)
    private String testCategory; // Hematology, Biochemistry, Radiology, etc.

    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LabReportStatus status = LabReportStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String summaryResult;

    @Column(length = 255)
    private String referenceRange;

    @Column(columnDefinition = "TEXT")
    private String doctorNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public LabReport() {
    }

    public LabReport(Patient patient, Doctor doctor, String testName, String testCategory, LocalDate requestedDate, LocalDate completedDate, LabReportStatus status, String summaryResult, String referenceRange, String doctorNotes) {
        this.patient = patient;
        this.doctor = doctor;
        this.testName = testName;
        this.testCategory = testCategory;
        this.requestedDate = requestedDate;
        this.completedDate = completedDate;
        this.status = status != null ? status : LabReportStatus.PENDING;
        this.summaryResult = summaryResult;
        this.referenceRange = referenceRange;
        this.doctorNotes = doctorNotes;
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

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public LabReportStatus getStatus() {
        return status;
    }

    public void setStatus(LabReportStatus status) {
        this.status = status;
    }

    public String getSummaryResult() {
        return summaryResult;
    }

    public void setSummaryResult(String summaryResult) {
        this.summaryResult = summaryResult;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
