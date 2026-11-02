package com.healthcare.hospital.modules.workflows.application;
import com.healthcare.hospital.modules.workflows.PatientWorkflowService;
import com.healthcare.hospital.modules.workflows.dto.PatientWorkflowDto;
import com.healthcare.hospital.modules.workflows.exception.WorkflowResourceNotFoundException;
import com.healthcare.hospital.modules.workflows.mapper.PatientWorkflowMapper;
import com.healthcare.hospital.modules.legacy.repository.PatientRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class PatientWorkflowApplicationServiceImpl implements PatientWorkflowApplicationService {
    private final PatientRepository repository;
    private final PatientWorkflowService workflow;
    private final PatientWorkflowMapper mapper;
    public PatientWorkflowApplicationServiceImpl(PatientRepository repository, PatientWorkflowService workflow, PatientWorkflowMapper mapper) {
        this.repository=repository; this.workflow=workflow; this.mapper=mapper;
    }
    @Override public List<PatientWorkflowDto> search(String query) {
        return workflow.search(repository.findAll(), query).stream().map(mapper::toDto).toList();
    }
    @Override public PatientWorkflowDto getById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Patient id must be positive");
        return repository.findById(id).map(mapper::toDto).orElseThrow(() -> new WorkflowResourceNotFoundException("Patient",id));
    }
}
