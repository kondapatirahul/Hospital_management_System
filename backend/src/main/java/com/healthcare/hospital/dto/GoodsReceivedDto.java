package com.healthcare.hospital.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GoodsReceivedDto {

    @NotNull(message = "Purchase order is required")
    private Long purchaseOrderId;

    private String supplierInvoiceNumber;
    private LocalDate supplierInvoiceDate;
    private LocalDate receivedDate = LocalDate.now();
    private String remarks;

    @NotEmpty(message = "At least one received item is required")
    @Valid
    private List<GoodsReceivedItemDto> items = new ArrayList<>();

    public GoodsReceivedDto() {
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
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

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<GoodsReceivedItemDto> getItems() {
        return items;
    }

    public void setItems(List<GoodsReceivedItemDto> items) {
        this.items = items;
    }
}
