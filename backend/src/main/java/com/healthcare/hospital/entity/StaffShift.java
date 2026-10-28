package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Staff rota entry and shift handover record. */
@Entity
@Table(name = "staff_shifts", indexes = {
    @Index(name = "idx_shift_date", columnList = "shift_date"),
    @Index(name = "idx_shift_staff", columnList = "staff_username")
})
public class StaffShift {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_username", nullable = false, length = 100)
    private String staffUsername;

    @Column(nullable = false, length = 120)
    private String staffDisplayName;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, length = 24)
    private String status = "SCHEDULED";

    @Column(length = 2000)
    private String handoverNotes;

    @Column(length = 100)
    private String handedOverBy;

    @Column(name = "handover_at")
    private LocalDateTime handoverAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StaffShift() {}

    public Long getId() { return id; }
    public String getStaffUsername() { return staffUsername; }
    public void setStaffUsername(String staffUsername) { this.staffUsername = staffUsername; }
    public String getStaffDisplayName() { return staffDisplayName; }
    public void setStaffDisplayName(String staffDisplayName) { this.staffDisplayName = staffDisplayName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }
    public String getHandedOverBy() { return handedOverBy; }
    public void setHandedOverBy(String handedOverBy) { this.handedOverBy = handedOverBy; }
    public LocalDateTime getHandoverAt() { return handoverAt; }
    public void setHandoverAt(LocalDateTime handoverAt) { this.handoverAt = handoverAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
