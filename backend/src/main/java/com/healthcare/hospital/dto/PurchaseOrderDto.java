package com.healthcare.hospital.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDto {

    private Long id;
    private String poNumber;

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    private LocalDate orderDate = LocalDate.now();
    private LocalDate expectedDeliveryDate;
    private String remarks;

    @NotEmpty(message = "At least one purchase item is required")
    @Valid
    private List<PurchaseOrderItemDto> items = new ArrayList<>();

    public PurchaseOrderDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<PurchaseOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItemDto> items) {
        this.items = items;
    }
}
