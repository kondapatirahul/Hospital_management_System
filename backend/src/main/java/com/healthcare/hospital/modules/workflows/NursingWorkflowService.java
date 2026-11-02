package com.healthcare.hospital.modules.workflows;

import com.healthcare.hospital.modules.legacy.entity.CareTask;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;

/** Nursing task-board policies built on the existing versioned CareTask entity. */
@Service
public class NursingWorkflowService {
    private static final Set<String> TERMINAL=Set.of("COMPLETED","CANCELLED","CLOSED");
    public List<CareTask> search(Collection<CareTask> tasks,String query) {
        return WorkflowAnalytics.safe(tasks).stream().filter(t->WorkflowAnalytics.contains(t.getTitle(),query)||
            WorkflowAnalytics.contains(t.getTaskType(),query)||WorkflowAnalytics.contains(t.getAssignedTo(),query)||
            WorkflowAnalytics.contains(t.getWard(),query)||WorkflowAnalytics.contains(t.getInstructions(),query))
            .sorted(Comparator.comparing(CareTask::getDueAt,Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    }
    public List<CareTask> assignedTo(Collection<CareTask> tasks,String staff) {
        return WorkflowAnalytics.safe(tasks).stream().filter(t->staff!=null&&staff.equalsIgnoreCase(WorkflowAnalytics.text(t.getAssignedTo()))).toList();
    }
    public List<CareTask> forWard(Collection<CareTask> tasks,String ward) {
        return WorkflowAnalytics.safe(tasks).stream().filter(t->ward!=null&&ward.equalsIgnoreCase(WorkflowAnalytics.text(t.getWard()))).toList();
    }
    public List<CareTask> overdue(Collection<CareTask> tasks,LocalDateTime now) {
        return WorkflowAnalytics.safe(tasks).stream().filter(t->WorkflowAnalytics.overdue(t.getDueAt(),now,t.getStatus()))
            .sorted(Comparator.comparing(CareTask::getDueAt)).toList();
    }
    public List<CareTask> dueWithin(Collection<CareTask> tasks,LocalDateTime now,int minutes) {
        if(now==null||minutes<0)return List.of();
        LocalDateTime end=now.plusMinutes(minutes);
        return WorkflowAnalytics.safe(tasks).stream().filter(t->t.getDueAt()!=null&&!t.getDueAt().isBefore(now)&&!t.getDueAt().isAfter(end))
            .filter(t->!TERMINAL.contains(WorkflowAnalytics.key(t.getStatus()))).sorted(Comparator.comparing(CareTask::getDueAt)).toList();
    }
    public List<String> validate(CareTask task) {
        if(task==null)return List.of("Task is required");
        List<String> errors=new ArrayList<>();
        if(task.getTitle()==null||task.getTitle().isBlank())errors.add("Task title is required");
        if(task.getTaskType()==null||task.getTaskType().isBlank())errors.add("Task type is required");
        if(task.getPriority()==null||!Set.of("LOW","NORMAL","HIGH","URGENT","CRITICAL").contains(WorkflowAnalytics.key(task.getPriority())))errors.add("Priority is invalid");
        if("COMPLETED".equalsIgnoreCase(task.getStatus())&&task.getCompletedAt()==null)errors.add("Completed tasks must record completion time");
        return List.copyOf(errors);
    }
    public Map<String,Long> statusCounts(Collection<CareTask> tasks){return WorkflowAnalytics.count(WorkflowAnalytics.safe(tasks),CareTask::getStatus);}
    public Map<String,Long> priorityCounts(Collection<CareTask> tasks){return WorkflowAnalytics.count(WorkflowAnalytics.safe(tasks),CareTask::getPriority);}
    public Map<String,Long> workloadByStaff(Collection<CareTask> tasks) {
        Map<String,Long> result=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        WorkflowAnalytics.safe(tasks).stream().filter(t->!TERMINAL.contains(WorkflowAnalytics.key(t.getStatus())))
            .filter(t->t.getAssignedTo()!=null&&!t.getAssignedTo().isBlank()).forEach(t->result.merge(t.getAssignedTo(),1L,Long::sum));
        return Map.copyOf(result);
    }
    public List<CareTask> unassigned(Collection<CareTask> tasks) {
        return WorkflowAnalytics.safe(tasks).stream().filter(t->t.getAssignedTo()==null||t.getAssignedTo().isBlank())
            .filter(t->!TERMINAL.contains(WorkflowAnalytics.key(t.getStatus()))).toList();
    }
    public List<CareTask> highRisk(Collection<CareTask> tasks) {
        return WorkflowAnalytics.safe(tasks).stream().filter(t->Set.of("URGENT","CRITICAL").contains(WorkflowAnalytics.key(t.getPriority())))
            .filter(t->!TERMINAL.contains(WorkflowAnalytics.key(t.getStatus()))).toList();
    }
    public WorkflowSnapshot snapshot(Collection<CareTask> tasks,LocalDateTime now) {
        List<CareTask> list=WorkflowAnalytics.safe(tasks);
        long active=list.stream().filter(t->!TERMINAL.contains(WorkflowAnalytics.key(t.getStatus()))).count();
        long attention=overdue(list,now).size()+unassigned(list).size()+highRisk(list).size();
        return WorkflowAnalytics.snapshot("NURSING",list.stream().map(CareTask::getStatus).toList(),list.size(),active,attention,List.of());
    }
    public Map<String,Object> wardBoard(Collection<CareTask> tasks,String ward,LocalDateTime now) {
        List<CareTask> selected=forWard(tasks,ward);
        return Map.of("ward",WorkflowAnalytics.text(ward),"total",selected.size(),"overdue",overdue(selected,now).size(),
            "unassigned",unassigned(selected).size(),"highRisk",highRisk(selected).size(),"byStatus",statusCounts(selected));
    }
    public List<CareTask> oldestOpen(Collection<CareTask> tasks,int limit) {
        return WorkflowAnalytics.safe(tasks).stream().filter(t->!TERMINAL.contains(WorkflowAnalytics.key(t.getStatus())))
            .sorted(Comparator.comparing(CareTask::getCreatedAt,Comparator.nullsLast(Comparator.naturalOrder())))
            .limit(Math.max(0,Math.min(limit,500))).toList();
    }
}
