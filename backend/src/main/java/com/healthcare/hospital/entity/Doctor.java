package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 150)
    private String qualification;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 50)
    private String roomNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal consultationFee = BigDecimal.valueOf(50.00);

    @Column(length = 100)
    private String workingDays = "Mon, Tue, Wed, Thu, Fri";

    @Column(length = 50)
    private String workingHours = "09:00 AM - 05:00 PM";

    @Column(nullable = false)
    private boolean availableToday = true;

    public Doctor() {
    }

    public Doctor(String fullName, String qualification, Department department, String email, String phone, String roomNumber, BigDecimal consultationFee, String workingDays, String workingHours) {
        this.fullName = fullName;
        this.qualification = qualification;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.consultationFee = consultationFee;
        this.workingDays = workingDays;
        this.workingHours = workingHours;
        this.availableToday = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public boolean isAvailableToday() {
        return availableToday;
    }

    public void setAvailableToday(boolean availableToday) {
        this.availableToday = availableToday;
    }
}
