package com.healthcare.hospital.shared.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PatientFeedbackDto {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer doctorRating;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer serviceRating;

    @Size(max = 2000)
    private String comments;

    public Integer getDoctorRating() { return doctorRating; }
    public void setDoctorRating(Integer doctorRating) { this.doctorRating = doctorRating; }
    public Integer getServiceRating() { return serviceRating; }
    public void setServiceRating(Integer serviceRating) { this.serviceRating = serviceRating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
