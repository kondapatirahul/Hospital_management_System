package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.BatchStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicine_batches")
public class MedicineBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String batchNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(nullable = false)
    private Integer initialQuantity = 0;

    @Column(nullable = false)
    private Integer currentQuantity = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BatchStatus status = BatchStatus.ACTIVE;

    @Column(length = 255)
    private String notes;

    @Version
    private Long version;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public MedicineBatch() {
    }

    public MedicineBatch(String batchNumber, Medicine medicine, Supplier supplier, Integer initialQuantity,
                         BigDecimal purchasePrice, BigDecimal unitPrice, LocalDate receivedDate, LocalDate expiryDate) {
        this.batchNumber = batchNumber;
        this.medicine = medicine;
        this.supplier = supplier;
        this.initialQuantity = initialQuantity != null ? initialQuantity : 0;
        this.currentQuantity = this.initialQuantity;
        this.purchasePrice = purchasePrice != null ? purchasePrice : BigDecimal.ZERO;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.receivedDate = receivedDate != null ? receivedDate : LocalDate.now();
        this.expiryDate = expiryDate;
        this.status = calculateStatus(LocalDate.now());
        this.createdAt = LocalDateTime.now();
    }

    public BatchStatus calculateStatus(LocalDate asOf) {
        if (currentQuantity <= 0) {
            return BatchStatus.DEPLETED;
        }
        if (expiryDate != null && expiryDate.isBefore(asOf)) {
            return BatchStatus.EXPIRED;
        }
        return BatchStatus.ACTIVE;
    }

    public boolean isExpired() {
        return isExpired(LocalDate.now());
    }

    public boolean isExpired(LocalDate asOf) {
        return expiryDate != null && expiryDate.isBefore(asOf);
    }

    public boolean isNearExpiry(int daysThreshold) {
        return isNearExpiry(LocalDate.now(), daysThreshold);
    }

    public boolean isNearExpiry(LocalDate asOf, int daysThreshold) {
        if (isExpired(asOf)) return false;
        return expiryDate != null && !expiryDate.isAfter(asOf.plusDays(daysThreshold));
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public boolean isEligibleForDispensing() {
        return status == BatchStatus.ACTIVE && currentQuantity > 0 && !isExpired();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
        if (this.currentQuantity <= 0 && this.status != BatchStatus.QUARANTINED) {
            this.status = BatchStatus.DEPLETED;
        }
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        this.status = calculateStatus(LocalDate.now());
    }

    public BatchStatus getStatus() {
        return status;
    }

    public void setStatus(BatchStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
