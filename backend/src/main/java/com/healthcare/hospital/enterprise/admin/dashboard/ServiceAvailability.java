package com.healthcare.hospital.enterprise.admin.dashboard;

/** Domain policy engine for service availability. Rules are deterministic and safe to run on dashboard read models. */
public final class ServiceAvailability {
    public static final String DOMAIN = "service availability";
    public ServiceAvailability() {}

    public AdminAssessment evaluateService(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "service");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Service status is missing");
            actions.add("Assign an owner and record the current service status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Service item is past its due date");
            actions.add("Escalate the overdue service item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for service");
            actions.add("Require a documented review for the service exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 1 * 1000.0) {
            findings.add("Value exceeds the service monitoring threshold");
            actions.add("Validate supporting evidence and approval for service");
            score -= 12;
        }
        String reference = signal.attribute("service");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for service is not recorded");
            actions.add("Attach traceable evidence for service");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to service");
            actions.add("Assign a named accountable owner for service");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateUptime(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "uptime");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Uptime status is missing");
            actions.add("Assign an owner and record the current uptime status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Uptime item is past its due date");
            actions.add("Escalate the overdue uptime item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for uptime");
            actions.add("Require a documented review for the uptime exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 2 * 1000.0) {
            findings.add("Value exceeds the uptime monitoring threshold");
            actions.add("Validate supporting evidence and approval for uptime");
            score -= 12;
        }
        String reference = signal.attribute("uptime");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for uptime is not recorded");
            actions.add("Attach traceable evidence for uptime");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to uptime");
            actions.add("Assign a named accountable owner for uptime");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateOutage(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "outage");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Outage status is missing");
            actions.add("Assign an owner and record the current outage status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Outage item is past its due date");
            actions.add("Escalate the overdue outage item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for outage");
            actions.add("Require a documented review for the outage exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 3 * 1000.0) {
            findings.add("Value exceeds the outage monitoring threshold");
            actions.add("Validate supporting evidence and approval for outage");
            score -= 12;
        }
        String reference = signal.attribute("outage");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for outage is not recorded");
            actions.add("Attach traceable evidence for outage");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to outage");
            actions.add("Assign a named accountable owner for outage");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateMaintenanceWindow(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "maintenance window");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Maintenance window status is missing");
            actions.add("Assign an owner and record the current maintenance window status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Maintenance window item is past its due date");
            actions.add("Escalate the overdue maintenance window item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 4) {
            findings.add("Severity threshold reached for maintenance window");
            actions.add("Require a documented review for the maintenance window exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 5 * 1000.0) {
            findings.add("Value exceeds the maintenance window monitoring threshold");
            actions.add("Validate supporting evidence and approval for maintenance window");
            score -= 12;
        }
        String reference = signal.attribute("maintenance_window");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for maintenance window is not recorded");
            actions.add("Attach traceable evidence for maintenance window");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to maintenance window");
            actions.add("Assign a named accountable owner for maintenance window");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateDependency(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "dependency");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Dependency status is missing");
            actions.add("Assign an owner and record the current dependency status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Dependency item is past its due date");
            actions.add("Escalate the overdue dependency item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 5) {
            findings.add("Severity threshold reached for dependency");
            actions.add("Require a documented review for the dependency exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 7 * 1000.0) {
            findings.add("Value exceeds the dependency monitoring threshold");
            actions.add("Validate supporting evidence and approval for dependency");
            score -= 12;
        }
        String reference = signal.attribute("dependency");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for dependency is not recorded");
            actions.add("Attach traceable evidence for dependency");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to dependency");
            actions.add("Assign a named accountable owner for dependency");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateRecovery(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "recovery");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Recovery status is missing");
            actions.add("Assign an owner and record the current recovery status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Recovery item is past its due date");
            actions.add("Escalate the overdue recovery item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for recovery");
            actions.add("Require a documented review for the recovery exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 10 * 1000.0) {
            findings.add("Value exceeds the recovery monitoring threshold");
            actions.add("Validate supporting evidence and approval for recovery");
            score -= 12;
        }
        String reference = signal.attribute("recovery");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for recovery is not recorded");
            actions.add("Attach traceable evidence for recovery");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to recovery");
            actions.add("Assign a named accountable owner for recovery");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateStatus(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "status");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Status status is missing");
            actions.add("Assign an owner and record the current status status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Status item is past its due date");
            actions.add("Escalate the overdue status item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for status");
            actions.add("Require a documented review for the status exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 14 * 1000.0) {
            findings.add("Value exceeds the status monitoring threshold");
            actions.add("Validate supporting evidence and approval for status");
            score -= 12;
        }
        String reference = signal.attribute("status");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for status is not recorded");
            actions.add("Attach traceable evidence for status");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to status");
            actions.add("Assign a named accountable owner for status");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateCommunication(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "service availability");
        evidence.put("control", "communication");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Communication status is missing");
            actions.add("Assign an owner and record the current communication status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Communication item is past its due date");
            actions.add("Escalate the overdue communication item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for communication");
            actions.add("Require a documented review for the communication exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 21 * 1000.0) {
            findings.add("Value exceeds the communication monitoring threshold");
            actions.add("Validate supporting evidence and approval for communication");
            score -= 12;
        }
        String reference = signal.attribute("communication");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for communication is not recorded");
            actions.add("Attach traceable evidence for communication");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to communication");
            actions.add("Assign a named accountable owner for communication");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
        checks.add(evaluateService(signal, today));
        checks.add(evaluateUptime(signal, today));
        checks.add(evaluateOutage(signal, today));
        checks.add(evaluateMaintenanceWindow(signal, today));
        checks.add(evaluateDependency(signal, today));
        checks.add(evaluateRecovery(signal, today));
        checks.add(evaluateStatus(signal, today));
        checks.add(evaluateCommunication(signal, today));
        int score = checks.stream().mapToInt(AdminAssessment::score).sum() / Math.max(1, checks.size());
        java.util.List<String> findings = checks.stream().flatMap(item -> item.findings().stream()).distinct().toList();
        java.util.List<String> actions = checks.stream().flatMap(item -> item.recommendedActions().stream()).distinct().toList();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("checks", Integer.toString(checks.size()));
        evidence.put("findings", Integer.toString(findings.size()));
        evidence.put("domain", "service availability ");
        String status = checks.stream().anyMatch(item -> "CRITICAL".equals(item.status())) ? "CRITICAL" : checks.stream().anyMatch(item -> "OVERDUE".equals(item.status())) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("service availability", signal == null ? null : signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
