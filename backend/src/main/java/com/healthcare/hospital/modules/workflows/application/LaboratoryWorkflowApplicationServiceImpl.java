package com.healthcare.hospital.modules.workflows.application;
import com.healthcare.hospital.modules.workflows.LaboratoryWorkflowService;
import com.healthcare.hospital.modules.workflows.dto.LaboratoryBoardDto;
import com.healthcare.hospital.modules.legacy.repository.LabSampleRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class LaboratoryWorkflowApplicationServiceImpl implements LaboratoryWorkflowApplicationService {
    private final LabSampleRepository repository; private final LaboratoryWorkflowService workflow;
    public LaboratoryWorkflowApplicationServiceImpl(LabSampleRepository repository,LaboratoryWorkflowService workflow) {
        this.repository=repository; this.workflow=workflow;
    }
    @Override public LaboratoryBoardDto board() {
        List<com.healthcare.hospital.modules.legacy.entity.LabSample> samples=repository.findAll();
        return new LaboratoryBoardDto(samples.size(),workflow.pendingReceipt(samples).size(),
            workflow.receivedAwaitingProcessing(samples),workflow.agingSamples(samples,LocalDateTime.now(),24).size(),
            workflow.rejected(samples).size(),workflow.sampleTypeCounts(samples));
    }
}
