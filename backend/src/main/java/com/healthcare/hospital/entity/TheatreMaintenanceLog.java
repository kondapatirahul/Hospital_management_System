package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Historical record of maintenance, cleaning, equipment calibration, and downtime.
 */
@Entity
@Table(name = "theatre_maintenance_logs", indexes = {
    @Index(name = "idx_theatre_maint_theatre", columnList = "theatre_id"),
    @Index(name = "idx_theatre_maint_time", columnList = "start_time")
})
public class TheatreMaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theatre_id", nullable = false)
    private OperationTheatre theatre;

    @Column(name = "maintenance_type", nullable = false, length = 50)
    private String maintenanceType; // ROUTINE_SERVICE, EQUIPMENT_REPAIR, DEEP_CLEANING, CALIBRATION, EMERGENCY_DOWNTIME

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(length = 1500)
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TheatreMaintenanceLog() {
    }

    public TheatreMaintenanceLog(OperationTheatre theatre, String maintenanceType, LocalDateTime startTime, LocalDateTime endTime, String performedBy, String details) {
        this.theatre = theatre;
        this.maintenanceType = maintenanceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.performedBy = performedBy;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationTheatre getTheatre() {
        return theatre;
    }

    public void setTheatre(OperationTheatre theatre) {
        this.theatre = theatre;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
