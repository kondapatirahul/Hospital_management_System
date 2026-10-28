package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;

/** Daily attendance record with guarded check-in/check-out transitions. */
@Entity
@Table(name = "staff_attendance", indexes = {
    @Index(name = "idx_attendance_date", columnList = "attendance_date"),
    @Index(name = "idx_attendance_staff", columnList = "staff_username")
}, uniqueConstraints = @UniqueConstraint(name = "uk_attendance_staff_date", columnNames = {"staff_username", "attendance_date"}))
public class StaffAttendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_username", nullable = false, length = 100)
    private String staffUsername;

    @Column(name = "staff_display_name", nullable = false, length = 120)
    private String staffDisplayName;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in_at")
    private LocalDateTime checkInAt;

    @Column(name = "check_out_at")
    private LocalDateTime checkOutAt;

    @Column(nullable = false, length = 24)
    private String status = "NOT_CHECKED_IN";

    @Column(length = 500)
    private String notes;

    @Version
    private Long version;

    public StaffAttendance() {}
    public Long getId() { return id; }
    public String getStaffUsername() { return staffUsername; }
    public void setStaffUsername(String v) { staffUsername = v; }
    public String getStaffDisplayName() { return staffDisplayName; }
    public void setStaffDisplayName(String v) { staffDisplayName = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { department = v; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate v) { attendanceDate = v; }
    public LocalDateTime getCheckInAt() { return checkInAt; }
    public void setCheckInAt(LocalDateTime v) { checkInAt = v; }
    public LocalDateTime getCheckOutAt() { return checkOutAt; }
    public void setCheckOutAt(LocalDateTime v) { checkOutAt = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { notes = v; }
    public Long getVersion() { return version; }
    public long getWorkedMinutes() {
        if (checkInAt == null) return 0;
        return Duration.between(checkInAt, checkOutAt == null ? LocalDateTime.now() : checkOutAt).toMinutes();
    }
}
