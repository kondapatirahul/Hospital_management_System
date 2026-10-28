package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer orderedQuantity;

    @Column(nullable = false)
    private Integer receivedQuantity = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    public PurchaseOrderItem() {
    }

    public PurchaseOrderItem(PurchaseOrder purchaseOrder, Medicine medicine, Integer orderedQuantity, BigDecimal unitCost) {
        this.purchaseOrder = purchaseOrder;
        this.medicine = medicine;
        this.orderedQuantity = orderedQuantity != null ? orderedQuantity : 0;
        this.receivedQuantity = 0;
        this.unitCost = unitCost != null ? unitCost : BigDecimal.ZERO;
        this.totalCost = this.unitCost.multiply(BigDecimal.valueOf(this.orderedQuantity));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
        if (this.unitCost != null && this.orderedQuantity != null) {
            this.totalCost = this.unitCost.multiply(BigDecimal.valueOf(this.orderedQuantity));
        }
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity != null ? receivedQuantity : 0;
    }

    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
        if (this.unitCost != null && this.orderedQuantity != null) {
            this.totalCost = this.unitCost.multiply(BigDecimal.valueOf(this.orderedQuantity));
        }
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public int getPendingQuantity() {
        return Math.max(0, getOrderedQuantity() - getReceivedQuantity());
    }

    public boolean isFullyReceived() {
        return getPendingQuantity() == 0;
    }
}
