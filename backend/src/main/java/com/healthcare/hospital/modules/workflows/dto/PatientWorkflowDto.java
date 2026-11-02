package com.healthcare.hospital.modules.workflows.dto;
public record PatientWorkflowDto(Long id, String patientId, String fullName, String email, String phone,
                                 String gender, String bloodGroup, int profileCompletion, boolean identityValid) {}
