package com.healthcare.hospital.enterprise.admin.dashboard;

/** Domain policy engine for queue and access oversight. Rules are deterministic and safe to run on dashboard read models. */
public final class QueueAndAccessOversight {
    public static final String DOMAIN = "queue and access oversight";
    public QueueAndAccessOversight() {}

    public AdminAssessment evaluateQueue(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
        evidence.put("control", "queue");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Queue status is missing");
            actions.add("Assign an owner and record the current queue status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Queue item is past its due date");
            actions.add("Escalate the overdue queue item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for queue");
            actions.add("Require a documented review for the queue exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 1 * 1000.0) {
            findings.add("Value exceeds the queue monitoring threshold");
            actions.add("Validate supporting evidence and approval for queue");
            score -= 12;
        }
        String reference = signal.attribute("queue");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for queue is not recorded");
            actions.add("Attach traceable evidence for queue");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to queue");
            actions.add("Assign a named accountable owner for queue");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateWait(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
        evidence.put("control", "wait");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Wait status is missing");
            actions.add("Assign an owner and record the current wait status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Wait item is past its due date");
            actions.add("Escalate the overdue wait item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for wait");
            actions.add("Require a documented review for the wait exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 2 * 1000.0) {
            findings.add("Value exceeds the wait monitoring threshold");
            actions.add("Validate supporting evidence and approval for wait");
            score -= 12;
        }
        String reference = signal.attribute("wait");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for wait is not recorded");
            actions.add("Attach traceable evidence for wait");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to wait");
            actions.add("Assign a named accountable owner for wait");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateAppointmentAccess(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
        evidence.put("control", "appointment access");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Appointment access status is missing");
            actions.add("Assign an owner and record the current appointment access status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Appointment access item is past its due date");
            actions.add("Escalate the overdue appointment access item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for appointment access");
            actions.add("Require a documented review for the appointment access exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 3 * 1000.0) {
            findings.add("Value exceeds the appointment access monitoring threshold");
            actions.add("Validate supporting evidence and approval for appointment access");
            score -= 12;
        }
        String reference = signal.attribute("appointment_access");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for appointment access is not recorded");
            actions.add("Attach traceable evidence for appointment access");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to appointment access");
            actions.add("Assign a named accountable owner for appointment access");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateNoShow(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
        evidence.put("control", "no show");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("No show status is missing");
            actions.add("Assign an owner and record the current no show status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("No show item is past its due date");
            actions.add("Escalate the overdue no show item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 4) {
            findings.add("Severity threshold reached for no show");
            actions.add("Require a documented review for the no show exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 5 * 1000.0) {
            findings.add("Value exceeds the no show monitoring threshold");
            actions.add("Validate supporting evidence and approval for no show");
            score -= 12;
        }
        String reference = signal.attribute("no_show");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for no show is not recorded");
            actions.add("Attach traceable evidence for no show");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to no show");
            actions.add("Assign a named accountable owner for no show");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateCapacity(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
        evidence.put("control", "capacity");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Capacity status is missing");
            actions.add("Assign an owner and record the current capacity status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Capacity item is past its due date");
            actions.add("Escalate the overdue capacity item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 5) {
            findings.add("Severity threshold reached for capacity");
            actions.add("Require a documented review for the capacity exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 7 * 1000.0) {
            findings.add("Value exceeds the capacity monitoring threshold");
            actions.add("Validate supporting evidence and approval for capacity");
            score -= 12;
        }
        String reference = signal.attribute("capacity");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for capacity is not recorded");
            actions.add("Attach traceable evidence for capacity");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to capacity");
            actions.add("Assign a named accountable owner for capacity");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluatePriority(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
        evidence.put("control", "priority");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Priority status is missing");
            actions.add("Assign an owner and record the current priority status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Priority item is past its due date");
            actions.add("Escalate the overdue priority item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for priority");
            actions.add("Require a documented review for the priority exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 10 * 1000.0) {
            findings.add("Value exceeds the priority monitoring threshold");
            actions.add("Validate supporting evidence and approval for priority");
            score -= 12;
        }
        String reference = signal.attribute("priority");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for priority is not recorded");
            actions.add("Attach traceable evidence for priority");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to priority");
            actions.add("Assign a named accountable owner for priority");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateBacklog(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
        evidence.put("control", "backlog");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Backlog status is missing");
            actions.add("Assign an owner and record the current backlog status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Backlog item is past its due date");
            actions.add("Escalate the overdue backlog item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for backlog");
            actions.add("Require a documented review for the backlog exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 14 * 1000.0) {
            findings.add("Value exceeds the backlog monitoring threshold");
            actions.add("Validate supporting evidence and approval for backlog");
            score -= 12;
        }
        String reference = signal.attribute("backlog");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for backlog is not recorded");
            actions.add("Attach traceable evidence for backlog");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to backlog");
            actions.add("Assign a named accountable owner for backlog");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateRecovery(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "queue and access oversight");
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
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for recovery");
            actions.add("Require a documented review for the recovery exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 21 * 1000.0) {
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
        return new AdminAssessment("queue and access oversight", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
        checks.add(evaluateQueue(signal, today));
        checks.add(evaluateWait(signal, today));
        checks.add(evaluateAppointmentAccess(signal, today));
        checks.add(evaluateNoShow(signal, today));
        checks.add(evaluateCapacity(signal, today));
        checks.add(evaluatePriority(signal, today));
        checks.add(evaluateBacklog(signal, today));
        checks.add(evaluateRecovery(signal, today));
        int score = checks.stream().mapToInt(AdminAssessment::score).sum() / Math.max(1, checks.size());
        java.util.List<String> findings = checks.stream().flatMap(item -> item.findings().stream()).distinct().toList();
        java.util.List<String> actions = checks.stream().flatMap(item -> item.recommendedActions().stream()).distinct().toList();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("checks", Integer.toString(checks.size()));
        evidence.put("findings", Integer.toString(findings.size()));
        evidence.put("domain", "queue and access oversight ");
        String status = checks.stream().anyMatch(item -> "CRITICAL".equals(item.status())) ? "CRITICAL" : checks.stream().anyMatch(item -> "OVERDUE".equals(item.status())) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("queue and access oversight", signal == null ? null : signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
