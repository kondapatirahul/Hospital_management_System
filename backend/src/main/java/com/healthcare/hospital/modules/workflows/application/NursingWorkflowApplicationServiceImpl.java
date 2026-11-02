package com.healthcare.hospital.modules.workflows.application;
import com.healthcare.hospital.modules.workflows.NursingWorkflowService;
import com.healthcare.hospital.modules.workflows.dto.NursingBoardDto;
import com.healthcare.hospital.modules.legacy.repository.CareTaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class NursingWorkflowApplicationServiceImpl implements NursingWorkflowApplicationService {
    private final CareTaskRepository repository; private final NursingWorkflowService workflow;
    public NursingWorkflowApplicationServiceImpl(CareTaskRepository repository,NursingWorkflowService workflow) {
        this.repository=repository; this.workflow=workflow;
    }
    @Override public NursingBoardDto board(String ward) {
        List<com.healthcare.hospital.modules.legacy.entity.CareTask> tasks=workflow.forWard(repository.findAll(),ward);
        long active=tasks.stream().filter(t->!List.of("COMPLETED","CANCELLED","CLOSED").contains(
            com.healthcare.hospital.modules.workflows.WorkflowAnalytics.key(t.getStatus()))).count();
        return new NursingBoardDto(tasks.size(),active,workflow.overdue(tasks,LocalDateTime.now()).size(),
            workflow.unassigned(tasks).size(),workflow.highRisk(tasks).size(),workflow.statusCounts(tasks),workflow.priorityCounts(tasks));
    }
}
