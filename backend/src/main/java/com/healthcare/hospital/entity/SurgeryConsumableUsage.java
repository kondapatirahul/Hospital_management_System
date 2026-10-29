package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record of consumable, disposable, or medication used during an operation.
 * Integrates with existing Medicine & Batch inventory for validated transactional deductions.
 */
@Entity
@Table(name = "surgery_consumable_usages", indexes = {
    @Index(name = "idx_surg_consumable_booking", columnList = "surgery_booking_id"),
    @Index(name = "idx_surg_consumable_med", columnList = "medicine_id")
})
public class SurgeryConsumableUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgery_booking_id", nullable = false)
    private SurgeryBooking surgeryBooking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private MedicineBatch batch;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "is_deducted", nullable = false)
    private boolean isDeducted = false;

    @Column(name = "recorded_by", length = 100)
    private String recordedBy;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    public SurgeryConsumableUsage() {
    }

    public SurgeryConsumableUsage(SurgeryBooking surgeryBooking, Medicine medicine, MedicineBatch batch,
                                  String itemName, Integer quantity, BigDecimal unitPrice, String recordedBy) {
        this.surgeryBooking = surgeryBooking;
        this.medicine = medicine;
        this.batch = batch;
        this.itemName = itemName != null ? itemName : (medicine != null ? medicine.getName() : "Item");
        this.quantity = quantity != null ? quantity : 1;
        this.unitPrice = unitPrice != null ? unitPrice : (batch != null ? batch.getUnitPrice() : BigDecimal.ZERO);
        this.totalCost = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        this.isDeducted = false;
        this.recordedBy = recordedBy;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SurgeryBooking getSurgeryBooking() {
        return surgeryBooking;
    }

    public void setSurgeryBooking(SurgeryBooking surgeryBooking) {
        this.surgeryBooking = surgeryBooking;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (unitPrice != null && quantity != null) {
            this.totalCost = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (unitPrice != null && quantity != null) {
            this.totalCost = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isDeducted() {
        return isDeducted;
    }

    public void setDeducted(boolean deducted) {
        isDeducted = deducted;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}
