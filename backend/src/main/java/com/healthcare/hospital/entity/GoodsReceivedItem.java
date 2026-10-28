package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goods_received_items")
public class GoodsReceivedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceivedNote goodsReceivedNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_item_id")
    private PurchaseOrderItem purchaseOrderItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private MedicineBatch batch;

    @Column(nullable = false, length = 100)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Integer quantityReceived;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    public GoodsReceivedItem() {
    }

    public GoodsReceivedItem(GoodsReceivedNote goodsReceivedNote, PurchaseOrderItem purchaseOrderItem, Medicine medicine,
                             MedicineBatch batch, String batchNumber, LocalDate expiryDate, Integer quantityReceived,
                             BigDecimal unitCost) {
        this.goodsReceivedNote = goodsReceivedNote;
        this.purchaseOrderItem = purchaseOrderItem;
        this.medicine = medicine;
        this.batch = batch;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.quantityReceived = quantityReceived != null ? quantityReceived : 0;
        this.unitCost = unitCost != null ? unitCost : BigDecimal.ZERO;
        this.totalCost = this.unitCost.multiply(BigDecimal.valueOf(this.quantityReceived));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GoodsReceivedNote getGoodsReceivedNote() {
        return goodsReceivedNote;
    }

    public void setGoodsReceivedNote(GoodsReceivedNote goodsReceivedNote) {
        this.goodsReceivedNote = goodsReceivedNote;
    }

    public PurchaseOrderItem getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        this.purchaseOrderItem = purchaseOrderItem;
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

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
        if (this.unitCost != null && this.quantityReceived != null) {
            this.totalCost = this.unitCost.multiply(BigDecimal.valueOf(this.quantityReceived));
        }
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
        if (this.unitCost != null && this.quantityReceived != null) {
            this.totalCost = this.unitCost.multiply(BigDecimal.valueOf(this.quantityReceived));
        }
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
