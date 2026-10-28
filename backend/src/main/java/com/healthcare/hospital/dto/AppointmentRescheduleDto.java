package com.healthcare.hospital.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class AppointmentRescheduleDto {

    @NotNull(message = "New appointment date is required")
    @FutureOrPresent(message = "Rescheduled date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate newDate;

    @NotBlank(message = "New time slot is required")
    private String newSlot;

    private String rescheduleReason;

    public AppointmentRescheduleDto() {
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public String getNewSlot() {
        return newSlot;
    }

    public void setNewSlot(String newSlot) {
        this.newSlot = newSlot;
    }

    public String getRescheduleReason() {
        return rescheduleReason;
    }

    public void setRescheduleReason(String rescheduleReason) {
        this.rescheduleReason = rescheduleReason;
    }
}
