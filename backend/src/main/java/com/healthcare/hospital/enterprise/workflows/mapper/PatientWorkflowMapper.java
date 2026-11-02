package com.healthcare.hospital.enterprise.workflows.mapper;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.enterprise.workflows.PatientWorkflowService;
import com.healthcare.hospital.enterprise.workflows.dto.PatientWorkflowDto;
import java.util.Map;
import org.springframework.stereotype.Component;
@Component
public class PatientWorkflowMapper {
    private final PatientWorkflowService workflow;
    public PatientWorkflowMapper(PatientWorkflowService workflow) { this.workflow = workflow; }
    public PatientWorkflowDto toDto(Patient entity) {
        if (entity == null) throw new IllegalArgumentException("Patient entity is required");
        Map<String,Object> completeness = workflow.profileCompleteness(entity);
        int percentage = ((Number) completeness.get("percentage")).intValue();
        return new PatientWorkflowDto(entity.getId(), entity.getPatientId(), entity.getFullName(), entity.getEmail(),
            entity.getPhone(), entity.getGender(), entity.getBloodGroup(), percentage,
            workflow.validateIdentity(entity).isEmpty());
    }
}
