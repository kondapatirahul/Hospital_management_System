package com.healthcare.hospital.dto;

import com.healthcare.hospital.entity.Prescription;
import com.healthcare.hospital.entity.StockTransaction;

import java.util.List;

public class PharmacyDashboardStatsDto {

    private long totalMedicines;
    private long availableMedicines;
    private long lowStockMedicines;
    private long outOfStockMedicines;
    private long expiredBatches;
    private long nearExpiryBatches;
    private long pendingPrescriptions;
    private long completedDispensations;

    private List<StockTransaction> recentStockTransactions;
    private List<Prescription> recentPrescriptionActivity;

    public PharmacyDashboardStatsDto() {
    }

    public long getTotalMedicines() {
        return totalMedicines;
    }

    public void setTotalMedicines(long totalMedicines) {
        this.totalMedicines = totalMedicines;
    }

    public long getAvailableMedicines() {
        return availableMedicines;
    }

    public void setAvailableMedicines(long availableMedicines) {
        this.availableMedicines = availableMedicines;
    }

    public long getLowStockMedicines() {
        return lowStockMedicines;
    }

    public void setLowStockMedicines(long lowStockMedicines) {
        this.lowStockMedicines = lowStockMedicines;
    }

    public long getOutOfStockMedicines() {
        return outOfStockMedicines;
    }

    public void setOutOfStockMedicines(long outOfStockMedicines) {
        this.outOfStockMedicines = outOfStockMedicines;
    }

    public long getExpiredBatches() {
        return expiredBatches;
    }

    public void setExpiredBatches(long expiredBatches) {
        this.expiredBatches = expiredBatches;
    }

    public long getNearExpiryBatches() {
        return nearExpiryBatches;
    }

    public void setNearExpiryBatches(long nearExpiryBatches) {
        this.nearExpiryBatches = nearExpiryBatches;
    }

    public long getPendingPrescriptions() {
        return pendingPrescriptions;
    }

    public void setPendingPrescriptions(long pendingPrescriptions) {
        this.pendingPrescriptions = pendingPrescriptions;
    }

    public long getCompletedDispensations() {
        return completedDispensations;
    }

    public void setCompletedDispensations(long completedDispensations) {
        this.completedDispensations = completedDispensations;
    }

    public List<StockTransaction> getRecentStockTransactions() {
        return recentStockTransactions;
    }

    public void setRecentStockTransactions(List<StockTransaction> recentStockTransactions) {
        this.recentStockTransactions = recentStockTransactions;
    }

    public List<Prescription> getRecentPrescriptionActivity() {
        return recentPrescriptionActivity;
    }

    public void setRecentPrescriptionActivity(List<Prescription> recentPrescriptionActivity) {
        this.recentPrescriptionActivity = recentPrescriptionActivity;
    }
}
