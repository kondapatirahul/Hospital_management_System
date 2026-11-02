package com.healthcare.hospital.enterprise.admin.dashboard;

/** Domain policy engine for operational analytics. Rules are deterministic and safe to run on dashboard read models. */
public final class OperationalAnalytics {
    public static final String DOMAIN = "operational analytics";
    public OperationalAnalytics() {}

    public AdminAssessment evaluateMetric(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "metric");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Metric status is missing");
            actions.add("Assign an owner and record the current metric status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Metric item is past its due date");
            actions.add("Escalate the overdue metric item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for metric");
            actions.add("Require a documented review for the metric exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 1 * 1000.0) {
            findings.add("Value exceeds the metric monitoring threshold");
            actions.add("Validate supporting evidence and approval for metric");
            score -= 12;
        }
        String reference = signal.attribute("metric");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for metric is not recorded");
            actions.add("Attach traceable evidence for metric");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to metric");
            actions.add("Assign a named accountable owner for metric");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateTarget(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "target");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Target status is missing");
            actions.add("Assign an owner and record the current target status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Target item is past its due date");
            actions.add("Escalate the overdue target item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for target");
            actions.add("Require a documented review for the target exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 2 * 1000.0) {
            findings.add("Value exceeds the target monitoring threshold");
            actions.add("Validate supporting evidence and approval for target");
            score -= 12;
        }
        String reference = signal.attribute("target");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for target is not recorded");
            actions.add("Attach traceable evidence for target");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to target");
            actions.add("Assign a named accountable owner for target");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateTrend(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "trend");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Trend status is missing");
            actions.add("Assign an owner and record the current trend status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Trend item is past its due date");
            actions.add("Escalate the overdue trend item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for trend");
            actions.add("Require a documented review for the trend exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 3 * 1000.0) {
            findings.add("Value exceeds the trend monitoring threshold");
            actions.add("Validate supporting evidence and approval for trend");
            score -= 12;
        }
        String reference = signal.attribute("trend");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for trend is not recorded");
            actions.add("Attach traceable evidence for trend");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to trend");
            actions.add("Assign a named accountable owner for trend");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateVariance(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "variance");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Variance status is missing");
            actions.add("Assign an owner and record the current variance status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Variance item is past its due date");
            actions.add("Escalate the overdue variance item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 4) {
            findings.add("Severity threshold reached for variance");
            actions.add("Require a documented review for the variance exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 5 * 1000.0) {
            findings.add("Value exceeds the variance monitoring threshold");
            actions.add("Validate supporting evidence and approval for variance");
            score -= 12;
        }
        String reference = signal.attribute("variance");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for variance is not recorded");
            actions.add("Attach traceable evidence for variance");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to variance");
            actions.add("Assign a named accountable owner for variance");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateForecast(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "forecast");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Forecast status is missing");
            actions.add("Assign an owner and record the current forecast status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Forecast item is past its due date");
            actions.add("Escalate the overdue forecast item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 5) {
            findings.add("Severity threshold reached for forecast");
            actions.add("Require a documented review for the forecast exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 7 * 1000.0) {
            findings.add("Value exceeds the forecast monitoring threshold");
            actions.add("Validate supporting evidence and approval for forecast");
            score -= 12;
        }
        String reference = signal.attribute("forecast");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for forecast is not recorded");
            actions.add("Attach traceable evidence for forecast");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to forecast");
            actions.add("Assign a named accountable owner for forecast");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateOutlier(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "outlier");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Outlier status is missing");
            actions.add("Assign an owner and record the current outlier status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Outlier item is past its due date");
            actions.add("Escalate the overdue outlier item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for outlier");
            actions.add("Require a documented review for the outlier exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 10 * 1000.0) {
            findings.add("Value exceeds the outlier monitoring threshold");
            actions.add("Validate supporting evidence and approval for outlier");
            score -= 12;
        }
        String reference = signal.attribute("outlier");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for outlier is not recorded");
            actions.add("Attach traceable evidence for outlier");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to outlier");
            actions.add("Assign a named accountable owner for outlier");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateCohort(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "cohort");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Cohort status is missing");
            actions.add("Assign an owner and record the current cohort status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Cohort item is past its due date");
            actions.add("Escalate the overdue cohort item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for cohort");
            actions.add("Require a documented review for the cohort exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 14 * 1000.0) {
            findings.add("Value exceeds the cohort monitoring threshold");
            actions.add("Validate supporting evidence and approval for cohort");
            score -= 12;
        }
        String reference = signal.attribute("cohort");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for cohort is not recorded");
            actions.add("Attach traceable evidence for cohort");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to cohort");
            actions.add("Assign a named accountable owner for cohort");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluatePerformance(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "operational analytics");
        evidence.put("control", "performance");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Performance status is missing");
            actions.add("Assign an owner and record the current performance status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Performance item is past its due date");
            actions.add("Escalate the overdue performance item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for performance");
            actions.add("Require a documented review for the performance exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 21 * 1000.0) {
            findings.add("Value exceeds the performance monitoring threshold");
            actions.add("Validate supporting evidence and approval for performance");
            score -= 12;
        }
        String reference = signal.attribute("performance");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for performance is not recorded");
            actions.add("Attach traceable evidence for performance");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to performance");
            actions.add("Assign a named accountable owner for performance");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public java.util.List<AdminAssessment> assessAll(java.util.Collection<AdminDashboardSignal> signals, java.time.LocalDate today) {
        java.util.List<AdminAssessment> results = new java.util.ArrayList<>();
        if (signals == null) return results;
        for (AdminDashboardSignal signal : signals) {
            if (signal == null) continue;
            results.add(assess(signal, today));
        }
        results.sort(java.util.Comparator.comparingInt(AdminAssessment::score).thenComparing(AdminAssessment::subject, java.util.Comparator.nullsLast(String::compareTo)));
        return java.util.List.copyOf(results);
    }

    public AdminAssessment assess(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.List<AdminAssessment> checks = new java.util.ArrayList<>();
        checks.add(evaluateMetric(signal, today));
        checks.add(evaluateTarget(signal, today));
        checks.add(evaluateTrend(signal, today));
        checks.add(evaluateVariance(signal, today));
        checks.add(evaluateForecast(signal, today));
        checks.add(evaluateOutlier(signal, today));
        checks.add(evaluateCohort(signal, today));
        checks.add(evaluatePerformance(signal, today));
        int score = checks.stream().mapToInt(AdminAssessment::score).sum() / Math.max(1, checks.size());
        java.util.List<String> findings = checks.stream().flatMap(item -> item.findings().stream()).distinct().toList();
        java.util.List<String> actions = checks.stream().flatMap(item -> item.recommendedActions().stream()).distinct().toList();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("checks", Integer.toString(checks.size()));
        evidence.put("findings", Integer.toString(findings.size()));
        evidence.put("domain", "operational analytics ");
        String status = checks.stream().anyMatch(item -> "CRITICAL".equals(item.status())) ? "CRITICAL" : checks.stream().anyMatch(item -> "OVERDUE".equals(item.status())) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("operational analytics", signal == null ? null : signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public java.util.Map<String, Long> statusBreakdown(java.util.Collection<AdminDashboardSignal> signals) {
        java.util.Map<String, Long> counts = new java.util.TreeMap<>();
        for (AdminAssessment item : assessAll(signals, java.time.LocalDate.now())) counts.merge(item.status(), 1L, Long::sum);
        for (String key : java.util.List.of("HEALTHY", "REVIEW", "OVERDUE", "CRITICAL")) counts.putIfAbsent(key, 0L);
        return java.util.Map.copyOf(counts);
    }

    public java.util.List<AdminAssessment> requiringAttention(java.util.Collection<AdminDashboardSignal> signals, java.time.LocalDate today) {
        return assessAll(signals, today).stream().filter(AdminAssessment::requiresAttention).toList();
    }

    public double healthIndex(java.util.Collection<AdminDashboardSignal> signals, java.time.LocalDate today) {
        java.util.List<AdminAssessment> assessments = assessAll(signals, today);
        if (assessments.isEmpty()) return 100.0;
        return assessments.stream().mapToInt(AdminAssessment::score).average().orElse(100.0);
    }

    public java.util.List<String> actionPlan(java.util.Collection<AdminDashboardSignal> signals, java.time.LocalDate today) {
        return requiringAttention(signals, today).stream().flatMap(item -> item.recommendedActions().stream()).distinct().limit(100).toList();
    }

}
