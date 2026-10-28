package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.StockTransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String transactionNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StockTransactionType transactionType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private MedicineBatch batch;

    @Column(nullable = false)
    private Integer quantity; // positive magnitude

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(name = "previous_stock", nullable = false)
    private Integer previousStock;

    @Column(name = "new_stock", nullable = false)
    private Integer newStock;

    @Column(length = 50)
    private String referenceType; // PRESCRIPTION, PURCHASE_ORDER, ADJUSTMENT, RETURN

    @Column(length = 100)
    private String referenceId;

    @Column(length = 100)
    private String performedBy;

    @Column(length = 255)
    private String notes;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    public StockTransaction() {
    }

    public StockTransaction(String transactionNumber, StockTransactionType transactionType, Medicine medicine,
                            MedicineBatch batch, Integer quantity, BigDecimal unitPrice, Integer previousStock,
                            Integer newStock, String referenceType, String referenceId, String performedBy, String notes) {
        this.transactionNumber = transactionNumber;
        this.transactionType = transactionType;
        this.medicine = medicine;
        this.batch = batch;
        this.quantity = quantity;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.totalValue = this.unitPrice.multiply(BigDecimal.valueOf(quantity != null ? quantity : 0));
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.performedBy = performedBy;
        this.notes = notes;
        this.transactionDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public StockTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(StockTransactionType transactionType) {
        this.transactionType = transactionType;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public Integer getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(Integer previousStock) {
        this.previousStock = previousStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
