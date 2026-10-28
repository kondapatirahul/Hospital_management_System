package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.DispensationStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescription_dispensations")
public class PrescriptionDispensation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String dispensationNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false, length = 100)
    private String dispensedBy;

    @Column(name = "dispensation_date", nullable = false)
    private LocalDateTime dispensationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DispensationStatus status = DispensationStatus.COMPLETED;

    @Column(nullable = false)
    private Integer totalQuantity = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @OneToMany(mappedBy = "dispensation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DispensationItem> items = new ArrayList<>();

    public PrescriptionDispensation() {
    }

    public PrescriptionDispensation(String dispensationNumber, Prescription prescription, Patient patient, Doctor doctor,
                                    String dispensedBy, DispensationStatus status, String remarks) {
        this.dispensationNumber = dispensationNumber;
        this.prescription = prescription;
        this.patient = patient;
        this.doctor = doctor;
        this.dispensedBy = dispensedBy;
        this.status = status != null ? status : DispensationStatus.COMPLETED;
        this.remarks = remarks;
        this.dispensationDate = LocalDateTime.now();
        this.totalQuantity = 0;
        this.totalAmount = BigDecimal.ZERO;
    }

    public void addItem(DispensationItem item) {
        items.add(item);
        item.setDispensation(this);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.totalQuantity = items.stream()
                .mapToInt(item -> item.getQuantityDispensed() != null ? item.getQuantityDispensed() : 0)
                .sum();
        this.totalAmount = items.stream()
                .map(DispensationItem::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDispensationNumber() {
        return dispensationNumber;
    }

    public void setDispensationNumber(String dispensationNumber) {
        this.dispensationNumber = dispensationNumber;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
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

    public String getDispensedBy() {
        return dispensedBy;
    }

    public void setDispensedBy(String dispensedBy) {
        this.dispensedBy = dispensedBy;
    }

    public LocalDateTime getDispensationDate() {
        return dispensationDate;
    }

    public void setDispensationDate(LocalDateTime dispensationDate) {
        this.dispensationDate = dispensationDate;
    }

    public DispensationStatus getStatus() {
        return status;
    }

    public void setStatus(DispensationStatus status) {
        this.status = status;
    }

    public Integer getTotalQuantity() {
        return totalQuantity != null ? totalQuantity : 0;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<DispensationItem> getItems() {
        return items;
    }

    public void setItems(List<DispensationItem> items) {
        this.items = items;
    }
}
