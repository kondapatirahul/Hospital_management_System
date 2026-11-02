package com.healthcare.hospital.enterprise.workflows.dto;
import java.util.Map;
public record NursingBoardDto(long totalTasks, long activeTasks, long overdueTasks, long unassignedTasks,
                              long urgentTasks, Map<String,Long> statuses, Map<String,Long> priorities) {
    public NursingBoardDto { statuses = Map.copyOf(statuses); priorities = Map.copyOf(priorities); }
}
