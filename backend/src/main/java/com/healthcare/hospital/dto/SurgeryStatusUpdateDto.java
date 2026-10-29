package com.healthcare.hospital.dto;

import com.healthcare.hospital.enums.SurgeryStatus;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class SurgeryStatusUpdateDto {

    private SurgeryStatus targetStatus;

    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate newDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime newStartTime;

    private Integer newDurationMinutes;

    private Long newTheatreId;

    private String operativeNotes;

    private String anesthesiaNotes;

    private String postoperativeDiagnosis;

    private String recoveryDestination;

    public SurgeryStatusUpdateDto() {
    }

    public SurgeryStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(SurgeryStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public LocalTime getNewStartTime() {
        return newStartTime;
    }

    public void setNewStartTime(LocalTime newStartTime) {
        this.newStartTime = newStartTime;
    }

    public Integer getNewDurationMinutes() {
        return newDurationMinutes;
    }

    public void setNewDurationMinutes(Integer newDurationMinutes) {
        this.newDurationMinutes = newDurationMinutes;
    }

    public Long getNewTheatreId() {
        return newTheatreId;
    }

    public void setNewTheatreId(Long newTheatreId) {
        this.newTheatreId = newTheatreId;
    }

    public String getOperativeNotes() {
        return operativeNotes;
    }

    public void setOperativeNotes(String operativeNotes) {
        this.operativeNotes = operativeNotes;
    }

    public String getAnesthesiaNotes() {
        return anesthesiaNotes;
    }

    public void setAnesthesiaNotes(String anesthesiaNotes) {
        this.anesthesiaNotes = anesthesiaNotes;
    }

    public String getPostoperativeDiagnosis() {
        return postoperativeDiagnosis;
    }

    public void setPostoperativeDiagnosis(String postoperativeDiagnosis) {
        this.postoperativeDiagnosis = postoperativeDiagnosis;
    }

    public String getRecoveryDestination() {
        return recoveryDestination;
    }

    public void setRecoveryDestination(String recoveryDestination) {
        this.recoveryDestination = recoveryDestination;
    }
}
