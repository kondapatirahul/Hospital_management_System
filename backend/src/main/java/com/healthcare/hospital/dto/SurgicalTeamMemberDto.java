package com.healthcare.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SurgicalTeamMemberDto {

    private Long doctorId;

    private Long userId;

    @NotBlank(message = "Member role is required")
    @Size(max = 50, message = "Member role must not exceed 50 characters")
    private String memberRole; // ASSISTANT_SURGEON, ANESTHESIOLOGIST, SCRUB_NURSE, CIRCULATING_NURSE, OT_TECHNICIAN

    public SurgicalTeamMemberDto() {
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }
}
