package com.healthcare.hospital.modules.admin.dashboard;

import java.time.LocalDate;
import java.util.Map;

/** Normalized, source-neutral dashboard signal; persistence adapters can populate it. */
public record AdminDashboardSignal(String id, String category, String owner, String status,
        LocalDate dueDate, int severity, double value, Map<String, String> attributes) {
    public AdminDashboardSignal {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        severity = Math.max(0, Math.min(5, severity));
    }
    public boolean overdue(LocalDate today) { return dueDate != null && dueDate.isBefore(today) && !closed(); }
    public boolean closed() { return status != null && (status.equalsIgnoreCase("closed") || status.equalsIgnoreCase("complete") || status.equalsIgnoreCase("cancelled")); }
    public String attribute(String key) { return attributes.getOrDefault(key, ""); }
}
