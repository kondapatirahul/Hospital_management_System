package com.healthcare.hospital.enterprise.admin.dashboard;

/** Domain policy engine for budget planning. Rules are deterministic and safe to run on dashboard read models. */
public final class BudgetPlanning {
    public static final String DOMAIN = "budget planning";
    public BudgetPlanning() {}

    public AdminAssessment evaluateBudget(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
        evidence.put("control", "budget");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Budget status is missing");
            actions.add("Assign an owner and record the current budget status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Budget item is past its due date");
            actions.add("Escalate the overdue budget item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for budget");
            actions.add("Require a documented review for the budget exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 1 * 1000.0) {
            findings.add("Value exceeds the budget monitoring threshold");
            actions.add("Validate supporting evidence and approval for budget");
            score -= 12;
        }
        String reference = signal.attribute("budget");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for budget is not recorded");
            actions.add("Attach traceable evidence for budget");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to budget");
            actions.add("Assign a named accountable owner for budget");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateForecast(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
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
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for forecast");
            actions.add("Require a documented review for the forecast exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 2 * 1000.0) {
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
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateActual(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
        evidence.put("control", "actual");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Actual status is missing");
            actions.add("Assign an owner and record the current actual status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Actual item is past its due date");
            actions.add("Escalate the overdue actual item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for actual");
            actions.add("Require a documented review for the actual exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 3 * 1000.0) {
            findings.add("Value exceeds the actual monitoring threshold");
            actions.add("Validate supporting evidence and approval for actual");
            score -= 12;
        }
        String reference = signal.attribute("actual");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for actual is not recorded");
            actions.add("Attach traceable evidence for actual");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to actual");
            actions.add("Assign a named accountable owner for actual");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateCommitment(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
        evidence.put("control", "commitment");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Commitment status is missing");
            actions.add("Assign an owner and record the current commitment status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Commitment item is past its due date");
            actions.add("Escalate the overdue commitment item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 4) {
            findings.add("Severity threshold reached for commitment");
            actions.add("Require a documented review for the commitment exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 5 * 1000.0) {
            findings.add("Value exceeds the commitment monitoring threshold");
            actions.add("Validate supporting evidence and approval for commitment");
            score -= 12;
        }
        String reference = signal.attribute("commitment");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for commitment is not recorded");
            actions.add("Attach traceable evidence for commitment");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to commitment");
            actions.add("Assign a named accountable owner for commitment");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateVariance(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
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
        if (signal.severity() >= 5) {
            findings.add("Severity threshold reached for variance");
            actions.add("Require a documented review for the variance exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 7 * 1000.0) {
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
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateAllocation(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
        evidence.put("control", "allocation");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Allocation status is missing");
            actions.add("Assign an owner and record the current allocation status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Allocation item is past its due date");
            actions.add("Escalate the overdue allocation item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for allocation");
            actions.add("Require a documented review for the allocation exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 10 * 1000.0) {
            findings.add("Value exceeds the allocation monitoring threshold");
            actions.add("Validate supporting evidence and approval for allocation");
            score -= 12;
        }
        String reference = signal.attribute("allocation");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for allocation is not recorded");
            actions.add("Attach traceable evidence for allocation");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to allocation");
            actions.add("Assign a named accountable owner for allocation");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateThreshold(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
        evidence.put("control", "threshold");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Threshold status is missing");
            actions.add("Assign an owner and record the current threshold status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Threshold item is past its due date");
            actions.add("Escalate the overdue threshold item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for threshold");
            actions.add("Require a documented review for the threshold exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 14 * 1000.0) {
            findings.add("Value exceeds the threshold monitoring threshold");
            actions.add("Validate supporting evidence and approval for threshold");
            score -= 12;
        }
        String reference = signal.attribute("threshold");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for threshold is not recorded");
            actions.add("Attach traceable evidence for threshold");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to threshold");
            actions.add("Assign a named accountable owner for threshold");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateApproval(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "budget planning");
        evidence.put("control", "approval");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Approval status is missing");
            actions.add("Assign an owner and record the current approval status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Approval item is past its due date");
            actions.add("Escalate the overdue approval item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for approval");
            actions.add("Require a documented review for the approval exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 21 * 1000.0) {
            findings.add("Value exceeds the approval monitoring threshold");
            actions.add("Validate supporting evidence and approval for approval");
            score -= 12;
        }
        String reference = signal.attribute("approval");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for approval is not recorded");
            actions.add("Attach traceable evidence for approval");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to approval");
            actions.add("Assign a named accountable owner for approval");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("budget planning", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
        checks.add(evaluateBudget(signal, today));
        checks.add(evaluateForecast(signal, today));
        checks.add(evaluateActual(signal, today));
        checks.add(evaluateCommitment(signal, today));
        checks.add(evaluateVariance(signal, today));
        checks.add(evaluateAllocation(signal, today));
        checks.add(evaluateThreshold(signal, today));
        checks.add(evaluateApproval(signal, today));
        int score = checks.stream().mapToInt(AdminAssessment::score).sum() / Math.max(1, checks.size());
        java.util.List<String> findings = checks.stream().flatMap(item -> item.findings().stream()).distinct().toList();
        java.util.List<String> actions = checks.stream().flatMap(item -> item.recommendedActions().stream()).distinct().toList();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("checks", Integer.toString(checks.size()));
        evidence.put("findings", Integer.toString(findings.size()));
        evidence.put("domain", "budget planning ");
        String status = checks.stream().anyMatch(item -> "CRITICAL".equals(item.status())) ? "CRITICAL" : checks.stream().anyMatch(item -> "OVERDUE".equals(item.status())) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("budget planning", signal == null ? null : signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
