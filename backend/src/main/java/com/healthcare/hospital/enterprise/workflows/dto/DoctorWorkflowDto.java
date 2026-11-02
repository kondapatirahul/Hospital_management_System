package com.healthcare.hospital.enterprise.workflows.dto;
public record DoctorWorkflowDto(Long id, String fullName, String qualification, String department,
                                String email, String phone, String roomNumber, boolean availableToday) {}
