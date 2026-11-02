package com.healthcare.hospital.enterprise.workflows.application;
import com.healthcare.hospital.enterprise.workflows.NursingWorkflowService;
import com.healthcare.hospital.enterprise.workflows.dto.NursingBoardDto;
import com.healthcare.hospital.repository.CareTaskRepository;
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
        List<com.healthcare.hospital.entity.CareTask> tasks=workflow.forWard(repository.findAll(),ward);
        long active=tasks.stream().filter(t->!List.of("COMPLETED","CANCELLED","CLOSED").contains(
            com.healthcare.hospital.enterprise.workflows.WorkflowAnalytics.key(t.getStatus()))).count();
        return new NursingBoardDto(tasks.size(),active,workflow.overdue(tasks,LocalDateTime.now()).size(),
            workflow.unassigned(tasks).size(),workflow.highRisk(tasks).size(),workflow.statusCounts(tasks),workflow.priorityCounts(tasks));
    }
}
