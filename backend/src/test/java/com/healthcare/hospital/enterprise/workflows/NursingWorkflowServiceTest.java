package com.healthcare.hospital.enterprise.workflows;
import static org.junit.jupiter.api.Assertions.*;
import com.healthcare.hospital.entity.CareTask;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
class NursingWorkflowServiceTest {
    private final NursingWorkflowService service=new NursingWorkflowService();
    private CareTask task(String title,String status,String priority,String assignee,LocalDateTime due) {
        CareTask t=new CareTask(); t.setTitle(title); t.setTaskType("OBSERVATION"); t.setStatus(status); t.setPriority(priority); t.setAssignedTo(assignee); t.setWard("Ward A"); t.setDueAt(due); return t;
    }
    @Test void detectsOverdueOpenTask() { assertEquals(1,service.overdue(List.of(task("Vitals","OPEN","HIGH","Nurse",LocalDateTime.now().minusHours(1))),LocalDateTime.now()).size()); }
    @Test void ignoresCompletedTaskInOverdueList() { assertTrue(service.overdue(List.of(task("Vitals","COMPLETED","HIGH","Nurse",LocalDateTime.now().minusHours(1))),LocalDateTime.now()).isEmpty()); }
    @Test void identifiesUnassignedTasks() { assertEquals(1,service.unassigned(List.of(task("Vitals","OPEN","NORMAL","",null))).size()); }
    @Test void identifiesCriticalTasks() { assertEquals(1,service.highRisk(List.of(task("Review","OPEN","CRITICAL","Nurse",null))).size()); }
    @Test void filtersTasksByWard() { assertEquals(1,service.forWard(List.of(task("A","OPEN","NORMAL","N",null)),"ward a").size()); }
    @Test void rejectsMissingTitle() { assertTrue(service.validate(task("","OPEN","NORMAL","N",null)).contains("Task title is required")); }
    @Test void rejectsInvalidPriority() { assertTrue(service.validate(task("Vitals","OPEN","SOMEDAY","N",null)).contains("Priority is invalid")); }
    @Test void acceptsValidOpenTask() { assertTrue(service.validate(task("Vitals","OPEN","NORMAL","N",null)).isEmpty()); }
    @Test void workloadCountsOpenAssignments() { assertEquals(2L,service.workloadByStaff(List.of(task("A","OPEN","NORMAL","Nurse",null),task("B","IN_PROGRESS","HIGH","Nurse",null))).get("Nurse")); }
    @Test void upcomingDueTasksRespectWindow() {
        var now=LocalDateTime.now();
        assertEquals(1,service.dueWithin(List.of(task("A","OPEN","NORMAL","N",now.plusMinutes(5)),task("B","OPEN","NORMAL","N",now.plusHours(2))),now,10).size());
    }
}
