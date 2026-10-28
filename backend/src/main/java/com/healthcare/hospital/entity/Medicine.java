package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String medicineCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String genericName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private MedicineCategory category;

    @Column(nullable = false, length = 50)
    private String dosageForm; // Tablet, Capsule, Syrup, Injection, Inhaler, Cream, etc.

    @Column(nullable = false, length = 50)
    private String strength; // 500 mg, 10 mg/5ml, 250 mg, etc.

    @Column(nullable = false, length = 50)
    private String unitOfMeasure; // Strip, Bottle, Box, Vial, Tube, Inhaler

    @Column(nullable = false)
    private Integer reorderLevel = 20;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO; // Selling / Dispense price

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice = BigDecimal.ZERO; // Standard purchase cost

    @Column(length = 150)
    private String manufacturer;

    @Column(length = 150)
    private String storageConditions = "Store below 25°C in a dry place";

    @Column(nullable = false)
    private boolean prescriptionRequired = true;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "total_stock", nullable = false)
    private Integer totalStock = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Medicine() {
    }

    public Medicine(String medicineCode, String name, String genericName, MedicineCategory category,
                    String dosageForm, String strength, String unitOfMeasure, Integer reorderLevel,
                    BigDecimal unitPrice, BigDecimal purchasePrice, String manufacturer) {
        this.medicineCode = medicineCode;
        this.name = name;
        this.genericName = genericName;
        this.category = category;
        this.dosageForm = dosageForm;
        this.strength = strength;
        this.unitOfMeasure = unitOfMeasure;
        this.reorderLevel = reorderLevel != null ? reorderLevel : 20;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.purchasePrice = purchasePrice != null ? purchasePrice : BigDecimal.ZERO;
        this.manufacturer = manufacturer;
        this.active = true;
        this.prescriptionRequired = true;
        this.totalStock = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public MedicineCategory getCategory() {
        return category;
    }

    public void setCategory(MedicineCategory category) {
        this.category = category;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }

    public boolean isPrescriptionRequired() {
        return prescriptionRequired;
    }

    public void setPrescriptionRequired(boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getTotalStock() {
        return totalStock != null ? totalStock : 0;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isLowStock() {
        return getTotalStock() > 0 && getTotalStock() <= getReorderLevel();
    }

    public boolean isOutOfStock() {
        return getTotalStock() <= 0;
    }
}
