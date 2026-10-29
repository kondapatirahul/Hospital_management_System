package com.healthcare.hospital.dto;

import com.healthcare.hospital.enums.TheatreStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OperationTheatreDto {

    private Long id;

    @NotBlank(message = "Theatre code is required")
    @Size(max = 30, message = "Theatre code must not exceed 30 characters")
    private String theatreCode;

    @NotBlank(message = "Theatre name is required")
    @Size(max = 120, message = "Theatre name must not exceed 120 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 150, message = "Location must not exceed 150 characters")
    private String location;

    @NotBlank(message = "Theatre type is required")
    @Size(max = 50, message = "Theatre type must not exceed 50 characters")
    private String theatreType = "GENERAL";

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity = 1;

    @Size(max = 1500, message = "Equipment details must not exceed 1500 characters")
    private String equipmentDetails;

    private TheatreStatus status = TheatreStatus.AVAILABLE;

    private boolean active = true;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    public OperationTheatreDto() {
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
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
