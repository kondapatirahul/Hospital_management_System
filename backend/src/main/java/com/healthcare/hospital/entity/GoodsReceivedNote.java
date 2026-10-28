package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goods_received_notes")
public class GoodsReceivedNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate = LocalDate.now();

    @Column(length = 100)
    private String supplierInvoiceNumber;

    @Column(name = "supplier_invoice_date")
    private LocalDate supplierInvoiceDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(length = 100)
    private String receivedBy;

    @OneToMany(mappedBy = "goodsReceivedNote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GoodsReceivedItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public GoodsReceivedNote() {
    }

    public GoodsReceivedNote(String grnNumber, PurchaseOrder purchaseOrder, Supplier supplier, LocalDate receivedDate,
                             String supplierInvoiceNumber, LocalDate supplierInvoiceDate, String remarks, String receivedBy) {
        this.grnNumber = grnNumber;
        this.purchaseOrder = purchaseOrder;
        this.supplier = supplier;
        this.receivedDate = receivedDate != null ? receivedDate : LocalDate.now();
        this.supplierInvoiceNumber = supplierInvoiceNumber;
        this.supplierInvoiceDate = supplierInvoiceDate;
        this.remarks = remarks;
        this.receivedBy = receivedBy;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(GoodsReceivedItem item) {
        items.add(item);
        item.setGoodsReceivedNote(this);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(GoodsReceivedItem::getTotalCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrnNumber() {
        return grnNumber;
    }

    public void setGrnNumber(String grnNumber) {
        this.grnNumber = grnNumber;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSupplierInvoiceNumber() {
        return supplierInvoiceNumber;
    }

    public void setSupplierInvoiceNumber(String supplierInvoiceNumber) {
        this.supplierInvoiceNumber = supplierInvoiceNumber;
    }

    public LocalDate getSupplierInvoiceDate() {
        return supplierInvoiceDate;
    }

    public void setSupplierInvoiceDate(LocalDate supplierInvoiceDate) {
        this.supplierInvoiceDate = supplierInvoiceDate;
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

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public List<GoodsReceivedItem> getItems() {
        return items;
    }

    public void setItems(List<GoodsReceivedItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
