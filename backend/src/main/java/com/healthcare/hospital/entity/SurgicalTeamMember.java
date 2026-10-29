package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Surgical team member assignment for an Operation Theatre booking.
 * Reuses existing Doctor and User entities.
 */
@Entity
@Table(name = "surgical_team_members", indexes = {
    @Index(name = "idx_team_surgery", columnList = "surgery_booking_id"),
    @Index(name = "idx_team_doctor", columnList = "doctor_id"),
    @Index(name = "idx_team_user", columnList = "user_id")
})
public class SurgicalTeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgery_booking_id", nullable = false)
    private SurgeryBooking surgeryBooking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "member_role", nullable = false, length = 50)
    private String memberRole; // LEAD_SURGEON, ASSISTANT_SURGEON, ANESTHESIOLOGIST, SCRUB_NURSE, CIRCULATING_NURSE, OT_TECHNICIAN

    @Column(name = "member_name", nullable = false, length = 120)
    private String memberName;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Column(name = "assigned_by", length = 100)
    private String assignedBy;

    public SurgicalTeamMember() {
    }

    public SurgicalTeamMember(SurgeryBooking surgeryBooking, Doctor doctor, String memberRole, String assignedBy) {
        this.surgeryBooking = surgeryBooking;
        this.doctor = doctor;
        this.memberRole = memberRole;
        this.memberName = doctor != null ? doctor.getFullName() : "Unknown Doctor";
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
    }

    public SurgicalTeamMember(SurgeryBooking surgeryBooking, User user, String memberRole, String assignedBy) {
        this.surgeryBooking = surgeryBooking;
        this.user = user;
        this.memberRole = memberRole;
        this.memberName = user != null ? user.getFullName() : "Unknown Staff";
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SurgeryBooking getSurgeryBooking() {
        return surgeryBooking;
    }

    public void setSurgeryBooking(SurgeryBooking surgeryBooking) {
        this.surgeryBooking = surgeryBooking;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor != null) {
            this.memberName = doctor.getFullName();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.memberName = user.getFullName();
        }
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
}
