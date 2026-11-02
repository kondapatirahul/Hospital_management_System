package com.healthcare.hospital.modules.doctor.dashboard;
import java.util.Objects;
/** Immutable, auditable rule definition used by physician worklist prioritization. */
public record DoctorDashboardRule(String key, String description, String metric, double threshold,
                                  DoctorDashboardSeverity severity, String recommendedAction) {
    public DoctorDashboardRule {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("Rule key is required");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required");
        if (metric == null || metric.isBlank()) throw new IllegalArgumentException("Metric is required");
        if (threshold < 0) throw new IllegalArgumentException("Threshold cannot be negative");
        Objects.requireNonNull(severity, "severity");
        if (recommendedAction == null || recommendedAction.isBlank()) throw new IllegalArgumentException("Action is required");
    }
    public static DoctorDashboardRule of(String key, String description, String metric, double threshold,
                                         DoctorDashboardSeverity severity, String action) {
        return new DoctorDashboardRule(key, description, metric, threshold, severity, action);
    }
}
