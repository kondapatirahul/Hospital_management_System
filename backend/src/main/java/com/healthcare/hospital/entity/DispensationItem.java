package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dispensation_items")
public class DispensationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensation_id", nullable = false)
    private PrescriptionDispensation dispensation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id", nullable = false)
    private MedicineBatch batch;

    @Column(nullable = false)
    private Integer quantityDispensed;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(length = 255)
    private String directions;

    public DispensationItem() {
    }

    public DispensationItem(PrescriptionDispensation dispensation, Prescription prescription, Medicine medicine,
                            MedicineBatch batch, Integer quantityDispensed, BigDecimal unitPrice, String directions) {
        this.dispensation = dispensation;
        this.prescription = prescription;
        this.medicine = medicine;
        this.batch = batch;
        this.quantityDispensed = quantityDispensed != null ? quantityDispensed : 0;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantityDispensed));
        this.directions = directions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrescriptionDispensation getDispensation() {
        return dispensation;
    }

    public void setDispensation(PrescriptionDispensation dispensation) {
        this.dispensation = dispensation;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public MedicineBatch getBatch() {
        return batch;
    }

    public void setBatch(MedicineBatch batch) {
        this.batch = batch;
    }

    public Integer getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(Integer quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
        if (this.unitPrice != null && this.quantityDispensed != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantityDispensed));
        }
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.unitPrice != null && this.quantityDispensed != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantityDispensed));
        }
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }
}
