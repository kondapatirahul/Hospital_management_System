package com.healthcare.hospital.modules.workflows.application;
import com.healthcare.hospital.modules.workflows.DoctorWorkflowService;
import com.healthcare.hospital.modules.workflows.dto.DoctorWorkflowDto;
import com.healthcare.hospital.modules.workflows.mapper.DoctorWorkflowMapper;
import com.healthcare.hospital.modules.legacy.repository.DoctorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class DoctorWorkflowApplicationServiceImpl implements DoctorWorkflowApplicationService {
    private final DoctorRepository repository; private final DoctorWorkflowService workflow; private final DoctorWorkflowMapper mapper;
    public DoctorWorkflowApplicationServiceImpl(DoctorRepository repository, DoctorWorkflowService workflow, DoctorWorkflowMapper mapper) {
        this.repository=repository; this.workflow=workflow; this.mapper=mapper;
    }
    @Override public List<DoctorWorkflowDto> search(String query) {
        return workflow.search(repository.findAll(),query).stream().map(mapper::toDto).toList();
    }
    @Override public List<DoctorWorkflowDto> availableToday() {
        return workflow.availableToday(repository.findAll()).stream().map(mapper::toDto).toList();
    }
}
