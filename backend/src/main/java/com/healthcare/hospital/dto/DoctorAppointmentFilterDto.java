package com.healthcare.hospital.dto;

import com.healthcare.hospital.enums.AppointmentStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class DoctorAppointmentFilterDto {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private AppointmentStatus status;

    private String keyword;

    public DoctorAppointmentFilterDto() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
