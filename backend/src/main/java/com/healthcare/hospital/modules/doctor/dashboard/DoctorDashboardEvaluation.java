package com.healthcare.hospital.modules.doctor.dashboard;
import java.util.List;
import java.util.Map;
/** Immutable output of a domain evaluation with explicit data coverage. */
public record DoctorDashboardEvaluation(String domain, List<DoctorDashboardInsight> insights,
                                        int rulesEvaluated, int metricsAvailable, Map<String, Double> coverage) {
    public DoctorDashboardEvaluation {
        insights = List.copyOf(insights == null ? List.of() : insights);
        coverage = Map.copyOf(coverage == null ? Map.of() : coverage);
    }
    public int criticalCount() { return (int) insights.stream().filter(DoctorDashboardInsight::isCritical).count(); }
    public int highPriorityCount() { return (int) insights.stream().filter(DoctorDashboardInsight::isHighPriority).count(); }
    public boolean hasActionableItems() { return !insights.isEmpty(); }
}
