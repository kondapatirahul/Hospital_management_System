package com.healthcare.hospital.dto;

import java.math.BigDecimal;
import java.util.Map;

public class SurgeryReportStatsDto {

    private long totalTheatres;
    private long availableTheatres;
    private long inUseTheatres;
    private long maintenanceTheatres;

    private long totalSurgeries;
    private long scheduledSurgeries;
    private long inProgressSurgeries;
    private long completedSurgeries;
    private long cancelledSurgeries;
    private long postponedSurgeries;

    private double averageDelayMinutes;
    private double overallUtilizationRatePercent;
    private BigDecimal totalSurgeryRevenue;

    private Map<String, Long> procedureCategoryCounts;
    private Map<String, Long> theatreSurgeryCounts;

    public SurgeryReportStatsDto() {
    }

    public long getTotalTheatres() {
        return totalTheatres;
    }

    public void setTotalTheatres(long totalTheatres) {
        this.totalTheatres = totalTheatres;
    }

    public long getAvailableTheatres() {
        return availableTheatres;
    }

    public void setAvailableTheatres(long availableTheatres) {
        this.availableTheatres = availableTheatres;
    }

    public long getInUseTheatres() {
        return inUseTheatres;
    }

    public void setInUseTheatres(long inUseTheatres) {
        this.inUseTheatres = inUseTheatres;
    }

    public long getMaintenanceTheatres() {
        return maintenanceTheatres;
    }

    public void setMaintenanceTheatres(long maintenanceTheatres) {
        this.maintenanceTheatres = maintenanceTheatres;
    }

    public long getTotalSurgeries() {
        return totalSurgeries;
    }

    public void setTotalSurgeries(long totalSurgeries) {
        this.totalSurgeries = totalSurgeries;
    }

    public long getScheduledSurgeries() {
        return scheduledSurgeries;
    }

    public void setScheduledSurgeries(long scheduledSurgeries) {
        this.scheduledSurgeries = scheduledSurgeries;
    }

    public long getInProgressSurgeries() {
        return inProgressSurgeries;
    }

    public void setInProgressSurgeries(long inProgressSurgeries) {
        this.inProgressSurgeries = inProgressSurgeries;
    }

    public long getCompletedSurgeries() {
        return completedSurgeries;
    }

    public void setCompletedSurgeries(long completedSurgeries) {
        this.completedSurgeries = completedSurgeries;
    }

    public long getCancelledSurgeries() {
        return cancelledSurgeries;
    }

    public void setCancelledSurgeries(long cancelledSurgeries) {
        this.cancelledSurgeries = cancelledSurgeries;
    }

    public long getPostponedSurgeries() {
        return postponedSurgeries;
    }

    public void setPostponedSurgeries(long postponedSurgeries) {
        this.postponedSurgeries = postponedSurgeries;
    }

    public double getAverageDelayMinutes() {
        return averageDelayMinutes;
    }

    public void setAverageDelayMinutes(double averageDelayMinutes) {
        this.averageDelayMinutes = averageDelayMinutes;
    }

    public double getOverallUtilizationRatePercent() {
        return overallUtilizationRatePercent;
    }

    public void setOverallUtilizationRatePercent(double overallUtilizationRatePercent) {
        this.overallUtilizationRatePercent = overallUtilizationRatePercent;
    }

    public BigDecimal getTotalSurgeryRevenue() {
        return totalSurgeryRevenue;
    }

    public void setTotalSurgeryRevenue(BigDecimal totalSurgeryRevenue) {
        this.totalSurgeryRevenue = totalSurgeryRevenue;
    }

    public Map<String, Long> getProcedureCategoryCounts() {
        return procedureCategoryCounts;
    }

    public void setProcedureCategoryCounts(Map<String, Long> procedureCategoryCounts) {
        this.procedureCategoryCounts = procedureCategoryCounts;
    }

    public Map<String, Long> getTheatreSurgeryCounts() {
        return theatreSurgeryCounts;
    }

    public void setTheatreSurgeryCounts(Map<String, Long> theatreSurgeryCounts) {
        this.theatreSurgeryCounts = theatreSurgeryCounts;
    }
}
