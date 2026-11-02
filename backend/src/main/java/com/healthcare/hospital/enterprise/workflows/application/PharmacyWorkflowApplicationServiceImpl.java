package com.healthcare.hospital.enterprise.workflows.application;
import com.healthcare.hospital.enterprise.workflows.PharmacyWorkflowService;
import com.healthcare.hospital.enterprise.workflows.dto.PharmacyBoardDto;
import com.healthcare.hospital.repository.MedicineBatchRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class PharmacyWorkflowApplicationServiceImpl implements PharmacyWorkflowApplicationService {
    private final MedicineBatchRepository repository; private final PharmacyWorkflowService workflow;
    public PharmacyWorkflowApplicationServiceImpl(MedicineBatchRepository repository,PharmacyWorkflowService workflow) {
        this.repository=repository; this.workflow=workflow;
    }
    @Override public PharmacyBoardDto board() {
        List<com.healthcare.hospital.entity.MedicineBatch> batches=repository.findAll();
        return new PharmacyBoardDto(batches.size(),workflow.totalUnits(batches),workflow.inventoryValue(batches),
            workflow.expired(batches,LocalDate.now()).size(),workflow.expiringWithin(batches,LocalDate.now(),30).size(),
            workflow.lowStock(batches,10).size(),workflow.batchStatusCounts(batches));
    }
}
