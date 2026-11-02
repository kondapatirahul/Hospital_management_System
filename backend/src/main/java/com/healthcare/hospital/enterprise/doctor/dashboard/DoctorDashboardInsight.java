package com.healthcare.hospital.enterprise.doctor.dashboard;
import java.time.Instant;
/** A single explainable dashboard insight; it never represents a diagnosis or treatment recommendation. */
public record DoctorDashboardInsight(String domain, String ruleKey, String description, String metric,
                                     double observedValue, double threshold, DoctorDashboardSeverity severity,
                                     String recommendedAction, Instant capturedAt) {
    public boolean isCritical() { return severity == DoctorDashboardSeverity.CRITICAL; }
    public boolean isHighPriority() { return severity.weight() >= DoctorDashboardSeverity.HIGH.weight(); }
    public double thresholdExcess() { return Math.max(0, observedValue - threshold); }
}
