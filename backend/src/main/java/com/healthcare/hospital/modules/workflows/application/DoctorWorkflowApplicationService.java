package com.healthcare.hospital.modules.workflows.application;
import com.healthcare.hospital.modules.workflows.dto.DoctorWorkflowDto;
import java.util.List;
public interface DoctorWorkflowApplicationService {
    List<DoctorWorkflowDto> search(String query);
    List<DoctorWorkflowDto> availableToday();
}
