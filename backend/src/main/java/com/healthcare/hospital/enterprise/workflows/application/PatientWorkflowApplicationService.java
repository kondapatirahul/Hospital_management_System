package com.healthcare.hospital.enterprise.workflows.application;
import com.healthcare.hospital.enterprise.workflows.dto.PatientWorkflowDto;
import java.util.List;
public interface PatientWorkflowApplicationService {
    List<PatientWorkflowDto> search(String query);
    PatientWorkflowDto getById(Long id);
}
