package com.healthcare.hospital.dto;

import java.math.BigDecimal;

public class DispensingSummaryDto {

    private String medicineCode;
    private String medicineName;
    private String dosageForm;
    private Long totalQuantity;
    private BigDecimal totalRevenue;

    public DispensingSummaryDto() {
    }

    public DispensingSummaryDto(String medicineCode, String medicineName, String dosageForm, Long totalQuantity, BigDecimal totalRevenue) {
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.dosageForm = dosageForm;
        this.totalQuantity = totalQuantity != null ? totalQuantity : 0L;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
