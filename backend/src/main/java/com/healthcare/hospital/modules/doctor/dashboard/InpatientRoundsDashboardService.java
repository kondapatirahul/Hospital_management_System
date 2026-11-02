package com.healthcare.hospital.modules.doctor.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Application policy service for inpatient rounds.
 * It exposes stable domain-specific operations to the doctor dashboard UI.
 * The service is stateless and safe to use across concurrent web requests.
 */
public final class InpatientRoundsDashboardService {
    public static final String DOMAIN = InpatientRoundsPolicyCatalog.DOMAIN;
    private static final List<DoctorDashboardRule> RULES = InpatientRoundsPolicyCatalog.rules();

    public DoctorDashboardEvaluation evaluate(DoctorDashboardSignalSnapshot snapshot) {
        return DoctorDashboardRuleEvaluator.evaluate(DOMAIN, RULES, snapshot);
    }

    public List<DoctorDashboardRule> rules() {
        return RULES;
    }

    public int ruleCount() {
        return RULES.size();
    }

    public List<String> metricNames() {
        return RULES.stream().map(DoctorDashboardRule::metric).distinct().toList();
    }

    public List<DoctorDashboardInsight> actionable(DoctorDashboardSignalSnapshot snapshot) {
        return evaluate(snapshot).insights();
    }

    public List<DoctorDashboardInsight> critical(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream().filter(DoctorDashboardInsight::isCritical).toList();
    }

    public List<DoctorDashboardInsight> highPriority(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream().filter(DoctorDashboardInsight::isHighPriority).toList();
    }

    public List<DoctorDashboardInsight> bySeverity(DoctorDashboardSignalSnapshot snapshot,
                                                     DoctorDashboardSeverity severity) {
        if (severity == null) return List.of();
        return actionable(snapshot).stream().filter(item -> item.severity() == severity).toList();
    }

    public long countBySeverity(DoctorDashboardSignalSnapshot snapshot,
                                DoctorDashboardSeverity severity) {
        return bySeverity(snapshot, severity).size();
    }

    public double weightedRiskScore(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream()
                .mapToDouble(item -> item.severity().weight() * Math.max(1.0, item.thresholdExcess()))
                .sum();
    }

    public double normalizedRiskScore(DoctorDashboardSignalSnapshot snapshot) {
        double maximum = RULES.stream().mapToDouble(rule -> rule.severity().weight() * 100.0).sum();
        if (maximum == 0) return 0;
        return Math.min(100.0, weightedRiskScore(snapshot) / maximum * 100.0);
    }

    public Map<String, Long> severityBreakdown(DoctorDashboardSignalSnapshot snapshot) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (DoctorDashboardSeverity severity : DoctorDashboardSeverity.values()) {
            result.put(severity.name(), countBySeverity(snapshot, severity));
        }
        return result;
    }

    public Map<String, Double> metricCoverage(DoctorDashboardSignalSnapshot snapshot) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (String metric : metricNames()) result.put(metric, snapshot.contains(metric) ? 1.0 : 0.0);
        return result;
    }

    public double coveragePercent(DoctorDashboardSignalSnapshot snapshot) {
        if (metricNames().isEmpty()) return 100.0;
        long present = metricNames().stream().filter(snapshot::contains).count();
        return present * 100.0 / metricNames().size();
    }

    public List<String> missingMetrics(DoctorDashboardSignalSnapshot snapshot) {
        return metricNames().stream().filter(metric -> !snapshot.contains(metric)).toList();
    }

    public List<DoctorDashboardInsight> topItems(DoctorDashboardSignalSnapshot snapshot, int limit) {
        if (limit <= 0) return List.of();
        return actionable(snapshot).stream().limit(limit).toList();
    }

    public List<DoctorDashboardInsight> aboveExcess(DoctorDashboardSignalSnapshot snapshot, double minimumExcess) {
        if (minimumExcess < 0) throw new IllegalArgumentException("Minimum excess cannot be negative");
        return actionable(snapshot).stream()
                .filter(item -> item.thresholdExcess() >= minimumExcess).toList();
    }

    public Map<String, Double> observedMetrics(DoctorDashboardSignalSnapshot snapshot) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (String metric : metricNames()) {
            if (snapshot.contains(metric)) result.put(metric, snapshot.value(metric));
        }
        return result;
    }

    public Map<String, Double> thresholdMap() {
        Map<String, Double> result = new LinkedHashMap<>();
        for (DoctorDashboardRule rule : RULES) result.putIfAbsent(rule.metric(), rule.threshold());
        return result;
    }

    public Map<String, String> actionMap() {
        Map<String, String> result = new LinkedHashMap<>();
        for (DoctorDashboardRule rule : RULES) result.putIfAbsent(rule.metric(), rule.recommendedAction());
        return result;
    }

    public List<DoctorDashboardRule> rulesForMetric(String metric) {
        if (metric == null || metric.isBlank()) return List.of();
        return RULES.stream().filter(rule -> rule.metric().equalsIgnoreCase(metric.trim())).toList();
    }

    public List<DoctorDashboardRule> rulesForSeverity(DoctorDashboardSeverity severity) {
        if (severity == null) return List.of();
        return RULES.stream().filter(rule -> rule.severity() == severity).toList();
    }

    public List<DoctorDashboardInsight> sortedByMetric(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream().sorted(Comparator.comparing(DoctorDashboardInsight::metric)).toList();
    }

    public Map<String, List<DoctorDashboardInsight>> groupByMetric(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream().collect(Collectors.groupingBy(
                DoctorDashboardInsight::metric, LinkedHashMap::new, Collectors.toList()));
    }

    public Map<String, List<DoctorDashboardInsight>> groupBySeverity(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream().collect(Collectors.groupingBy(
                item -> item.severity().name(), LinkedHashMap::new, Collectors.toList()));
    }

    public Set<String> triggeredRuleKeys(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream().map(DoctorDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
    }

    public boolean hasCriticalItems(DoctorDashboardSignalSnapshot snapshot) {
        return !critical(snapshot).isEmpty();
    }

    public boolean hasIncompleteSignalCoverage(DoctorDashboardSignalSnapshot snapshot) {
        return coveragePercent(snapshot) < 100.0;
    }

    public String statusLabel(DoctorDashboardSignalSnapshot snapshot) {
        if (hasCriticalItems(snapshot)) return "REQUIRES_IMMEDIATE_REVIEW";
        if (!highPriority(snapshot).isEmpty()) return "PRIORITY_REVIEW";
        if (!actionable(snapshot).isEmpty()) return "ROUTINE_REVIEW";
        if (hasIncompleteSignalCoverage(snapshot)) return "LIMITED_DATA";
        return "NO_CURRENT_RULE_BREACH";
    }

    public List<DoctorDashboardRule> rulesWithThresholdAtLeast(double threshold) {
        return RULES.stream().filter(rule -> rule.threshold() >= threshold).toList();
    }

    public List<DoctorDashboardRule> rulesWithThresholdBelow(double threshold) {
        return RULES.stream().filter(rule -> rule.threshold() < threshold).toList();
    }

    public Map<String, Integer> ruleCountsBySeverity() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (DoctorDashboardSeverity severity : DoctorDashboardSeverity.values()) {
            counts.put(severity.name(), (int) RULES.stream().filter(rule -> rule.severity() == severity).count());
        }
        return counts;
    }

    public List<String> explainCoverage(DoctorDashboardSignalSnapshot snapshot) {
        List<String> messages = new ArrayList<>();
        for (String metric : missingMetrics(snapshot)) {
            messages.add("No current operational signal supplied for " + metric + ".");
        }
        if (messages.isEmpty()) messages.add("All configured metrics have a supplied signal.");
        return List.copyOf(messages);
    }

    public DoctorDashboardEvaluation evaluateWithMinimumCoverage(DoctorDashboardSignalSnapshot snapshot,
                                                                    double minimumPercent) {
        if (minimumPercent < 0 || minimumPercent > 100) {
            throw new IllegalArgumentException("Coverage must be between zero and one hundred");
        }
        DoctorDashboardEvaluation evaluation = evaluate(snapshot);
        if (coveragePercent(snapshot) >= minimumPercent) return evaluation;
        return new DoctorDashboardEvaluation(DOMAIN, evaluation.insights(), evaluation.rulesEvaluated(),
                evaluation.metricsAvailable(), evaluation.coverage());
    }

    public List<DoctorDashboardInsight> actionableForMetric(DoctorDashboardSignalSnapshot snapshot,
                                                              String metric) {
        if (metric == null || metric.isBlank()) return List.of();
        return actionable(snapshot).stream()
                .filter(item -> item.metric().equalsIgnoreCase(metric.trim())).toList();
    }

    public double maximumObservedValue(DoctorDashboardSignalSnapshot snapshot) {
        return observedMetrics(snapshot).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    public double totalObservedValue(DoctorDashboardSignalSnapshot snapshot) {
        return observedMetrics(snapshot).values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public long breachedMetricCount(DoctorDashboardSignalSnapshot snapshot) {
        return actionable(snapshot).stream().map(DoctorDashboardInsight::metric).distinct().count();
    }

    public String dashboardSectionTitle() {
        return "Inpatient Rounds";
    }

    public String dashboardSectionDescription() {
        return "Operational signals and explainable actions for inpatient rounds.";
    }
}
