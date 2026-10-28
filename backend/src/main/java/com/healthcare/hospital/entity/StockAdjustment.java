package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.StockAdjustmentReason;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_adjustments")
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String adjustmentNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id", nullable = false)
    private MedicineBatch batch;

    @Column(nullable = false, length = 20)
    private String adjustmentType; // INCREASE, DECREASE

    @Column(nullable = false)
    private Integer quantity; // positive magnitude

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StockAdjustmentReason reason;

    @Column(length = 255)
    private String remarks;

    @Column(nullable = false, length = 100)
    private String adjustedBy;

    @Column(name = "adjustment_date", nullable = false)
    private LocalDateTime adjustmentDate = LocalDateTime.now();

    public StockAdjustment() {
    }

    public StockAdjustment(String adjustmentNumber, Medicine medicine, MedicineBatch batch, String adjustmentType,
                           Integer quantity, StockAdjustmentReason reason, String remarks, String adjustedBy) {
        this.adjustmentNumber = adjustmentNumber;
        this.medicine = medicine;
        this.batch = batch;
        this.adjustmentType = adjustmentType;
        this.quantity = quantity;
        this.reason = reason;
        this.remarks = remarks;
        this.adjustedBy = adjustedBy;
        this.adjustmentDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdjustmentNumber() {
        return adjustmentNumber;
    }

    public void setAdjustmentNumber(String adjustmentNumber) {
        this.adjustmentNumber = adjustmentNumber;
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

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public StockAdjustmentReason getReason() {
        return reason;
    }

    public void setReason(StockAdjustmentReason reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAdjustedBy() {
        return adjustedBy;
    }

    public void setAdjustedBy(String adjustedBy) {
        this.adjustedBy = adjustedBy;
    }

    public LocalDateTime getAdjustmentDate() {
        return adjustmentDate;
    }

    public void setAdjustmentDate(LocalDateTime adjustmentDate) {
        this.adjustmentDate = adjustmentDate;
    }
}
