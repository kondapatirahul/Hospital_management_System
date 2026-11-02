package com.healthcare.hospital.modules.workflows.application;
import com.healthcare.hospital.modules.workflows.dto.PatientWorkflowDto;
import java.util.List;
public interface PatientWorkflowApplicationService {
    List<PatientWorkflowDto> search(String query);
    PatientWorkflowDto getById(Long id);
}
