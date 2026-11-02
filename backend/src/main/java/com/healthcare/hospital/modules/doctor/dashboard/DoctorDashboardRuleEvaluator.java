package com.healthcare.hospital.modules.doctor.dashboard;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** Shared threshold evaluator. Missing signals are reported as coverage gaps, never silently treated as clinical facts. */
public final class DoctorDashboardRuleEvaluator {
    private DoctorDashboardRuleEvaluator() {}
    public static DoctorDashboardEvaluation evaluate(String domain, List<DoctorDashboardRule> rules,
                                                       DoctorDashboardSignalSnapshot snapshot) {
        if (rules == null || snapshot == null) throw new IllegalArgumentException("Rules and snapshot are required");
        List<DoctorDashboardInsight> insights = new ArrayList<>();
        Map<String,Double> coverage = new LinkedHashMap<>();
        int available = 0;
        for (DoctorDashboardRule rule : rules) {
            boolean present = snapshot.contains(rule.metric());
            coverage.put(rule.metric(), present ? 1.0 : 0.0);
            if (!present) continue;
            available++;
            double observed = snapshot.value(rule.metric());
            if (observed >= rule.threshold() && observed > 0) {
                insights.add(new DoctorDashboardInsight(domain, rule.key(), rule.description(), rule.metric(),
                        observed, rule.threshold(), rule.severity(), rule.recommendedAction(), snapshot.capturedAt()));
            }
        }
        insights.sort(Comparator.comparingInt((DoctorDashboardInsight i) -> i.severity().weight()).reversed()
                .thenComparing(DoctorDashboardInsight::thresholdExcess, Comparator.reverseOrder())
                .thenComparing(DoctorDashboardInsight::ruleKey));
        return new DoctorDashboardEvaluation(domain, insights, rules.size(), available, coverage);
    }
    public static List<DoctorDashboardInsight> merge(List<DoctorDashboardEvaluation> evaluations) {
        List<DoctorDashboardInsight> result = new ArrayList<>();
        if (evaluations != null) for (DoctorDashboardEvaluation evaluation : evaluations) result.addAll(evaluation.insights());
        result.sort(Comparator.comparingInt((DoctorDashboardInsight i) -> i.severity().weight()).reversed()
                .thenComparing(DoctorDashboardInsight::thresholdExcess, Comparator.reverseOrder()));
        return List.copyOf(result);
    }
}
