package com.healthcare.hospital.enterprise.workflows;

import java.util.List;
import java.util.Map;

public record WorkflowSnapshot(String domain, long total, long active, long attention,
                               Map<String, Long> statusCounts, List<String> warnings) {
    public WorkflowSnapshot {
        statusCounts = Map.copyOf(statusCounts);
        warnings = List.copyOf(warnings);
    }
}
