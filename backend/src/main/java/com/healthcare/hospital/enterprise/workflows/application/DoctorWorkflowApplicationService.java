package com.healthcare.hospital.enterprise.workflows.application;
import com.healthcare.hospital.enterprise.workflows.dto.DoctorWorkflowDto;
import java.util.List;
public interface DoctorWorkflowApplicationService {
    List<DoctorWorkflowDto> search(String query);
    List<DoctorWorkflowDto> availableToday();
}
