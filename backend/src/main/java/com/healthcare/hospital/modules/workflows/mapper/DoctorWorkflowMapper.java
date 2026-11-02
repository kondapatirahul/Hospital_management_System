package com.healthcare.hospital.modules.workflows.mapper;
import com.healthcare.hospital.modules.legacy.entity.Doctor;
import com.healthcare.hospital.modules.workflows.dto.DoctorWorkflowDto;
import org.springframework.stereotype.Component;
@Component
public class DoctorWorkflowMapper {
    public DoctorWorkflowDto toDto(Doctor entity) {
        if (entity == null) throw new IllegalArgumentException("Doctor entity is required");
        return new DoctorWorkflowDto(entity.getId(), entity.getFullName(), entity.getQualification(),
            (entity.getDepartment() == null ? null : entity.getDepartment().getName()), entity.getEmail(), entity.getPhone(), entity.getRoomNumber(), entity.isAvailableToday());
    }
}
