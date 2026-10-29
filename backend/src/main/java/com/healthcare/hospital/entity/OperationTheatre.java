package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.TheatreStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Operation Theatre room and resource entity.
 */
@Entity
@Table(name = "operation_theatres", indexes = {
    @Index(name = "idx_ot_code", columnList = "theatre_code", unique = true),
    @Index(name = "idx_ot_status", columnList = "status"),
    @Index(name = "idx_ot_type", columnList = "theatre_type")
})
public class OperationTheatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theatre_code", nullable = false, unique = true, length = 30)
    private String theatreCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(name = "theatre_type", nullable = false, length = 50)
    private String theatreType = "GENERAL";

    @Column(nullable = false)
    private Integer capacity = 1;

    @Column(name = "equipment_details", length = 1500)
    private String equipmentDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TheatreStatus status = TheatreStatus.AVAILABLE;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    private Long version;

    public OperationTheatre() {
    }

    public OperationTheatre(String theatreCode, String name, String location, String theatreType, Integer capacity, String equipmentDetails) {
        this.theatreCode = theatreCode;
        this.name = name;
        this.location = location;
        this.theatreType = theatreType != null ? theatreType : "GENERAL";
        this.capacity = capacity != null ? capacity : 1;
        this.equipmentDetails = equipmentDetails;
        this.status = TheatreStatus.AVAILABLE;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheatreCode() {
        return theatreCode;
    }

    public void setTheatreCode(String theatreCode) {
        this.theatreCode = theatreCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTheatreType() {
        return theatreType;
    }

    public void setTheatreType(String theatreType) {
        this.theatreType = theatreType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getEquipmentDetails() {
        return equipmentDetails;
    }

    public void setEquipmentDetails(String equipmentDetails) {
        this.equipmentDetails = equipmentDetails;
    }

    public TheatreStatus getStatus() {
        return status;
    }

    public void setStatus(TheatreStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isAvailableForBooking() {
        return this.active && (this.status == TheatreStatus.AVAILABLE || this.status == TheatreStatus.RESERVED);
    }
}
