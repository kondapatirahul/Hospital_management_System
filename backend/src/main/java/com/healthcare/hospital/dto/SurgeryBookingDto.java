package com.healthcare.hospital.dto;

import com.healthcare.hospital.enums.SurgeryPriority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class SurgeryBookingDto {

    private Long id;

    @NotNull(message = "Patient is required")
    private Long patientId;

    @NotNull(message = "Operation Theatre is required")
    private Long theatreId;

    @NotNull(message = "Lead Surgeon is required")
    private Long leadSurgeonId;

    @NotBlank(message = "Procedure name is required")
    @Size(max = 200, message = "Procedure name must not exceed 200 characters")
    private String procedureName;

    @Size(max = 80, message = "Procedure category must not exceed 80 characters")
    private String procedureCategory = "General";

    @NotNull(message = "Planned date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate plannedDate;

    @NotNull(message = "Planned start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime plannedStartTime;

    @NotNull(message = "Expected duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    private Integer expectedDurationMinutes = 120;

    private SurgeryPriority priority = SurgeryPriority.ROUTINE;

    private Long appointmentId;

    private Long wardBedId;

    @Size(max = 1000, message = "Clinical indication must not exceed 1000 characters")
    private String clinicalIndication;

    @Size(max = 1000, message = "Preoperative diagnosis must not exceed 1000 characters")
    private String preoperativeDiagnosis;

    public SurgeryBookingDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(Long theatreId) {
        this.theatreId = theatreId;
    }

    public Long getLeadSurgeonId() {
        return leadSurgeonId;
    }

    public void setLeadSurgeonId(Long leadSurgeonId) {
        this.leadSurgeonId = leadSurgeonId;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProcedureCategory() {
        return procedureCategory;
    }

    public void setProcedureCategory(String procedureCategory) {
        this.procedureCategory = procedureCategory;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public LocalTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public Integer getExpectedDurationMinutes() {
        return expectedDurationMinutes;
    }

    public void setExpectedDurationMinutes(Integer expectedDurationMinutes) {
        this.expectedDurationMinutes = expectedDurationMinutes;
    }

    public SurgeryPriority getPriority() {
        return priority;
    }

    public void setPriority(SurgeryPriority priority) {
        this.priority = priority;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getWardBedId() {
        return wardBedId;
    }

    public void setWardBedId(Long wardBedId) {
        this.wardBedId = wardBedId;
    }

    public String getClinicalIndication() {
        return clinicalIndication;
    }

    public void setClinicalIndication(String clinicalIndication) {
        this.clinicalIndication = clinicalIndication;
    }

    public String getPreoperativeDiagnosis() {
        return preoperativeDiagnosis;
    }

    public void setPreoperativeDiagnosis(String preoperativeDiagnosis) {
        this.preoperativeDiagnosis = preoperativeDiagnosis;
    }
}
