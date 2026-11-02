package com.healthcare.hospital.enterprise.admin.dashboard;

/** Domain policy engine for document control. Rules are deterministic and safe to run on dashboard read models. */
public final class DocumentControl {
    public static final String DOMAIN = "document control";
    public DocumentControl() {}

    public AdminAssessment evaluateDocument(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
        evidence.put("control", "document");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Document status is missing");
            actions.add("Assign an owner and record the current document status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Document item is past its due date");
            actions.add("Escalate the overdue document item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for document");
            actions.add("Require a documented review for the document exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 1 * 1000.0) {
            findings.add("Value exceeds the document monitoring threshold");
            actions.add("Validate supporting evidence and approval for document");
            score -= 12;
        }
        String reference = signal.attribute("document");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for document is not recorded");
            actions.add("Attach traceable evidence for document");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to document");
            actions.add("Assign a named accountable owner for document");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateVersion(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
        evidence.put("control", "version");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Version status is missing");
            actions.add("Assign an owner and record the current version status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Version item is past its due date");
            actions.add("Escalate the overdue version item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for version");
            actions.add("Require a documented review for the version exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 2 * 1000.0) {
            findings.add("Value exceeds the version monitoring threshold");
            actions.add("Validate supporting evidence and approval for version");
            score -= 12;
        }
        String reference = signal.attribute("version");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for version is not recorded");
            actions.add("Attach traceable evidence for version");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to version");
            actions.add("Assign a named accountable owner for version");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateApproval(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
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
        if (signal.value() > 3 * 1000.0) {
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
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateExpiry(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
        evidence.put("control", "expiry");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Expiry status is missing");
            actions.add("Assign an owner and record the current expiry status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Expiry item is past its due date");
            actions.add("Escalate the overdue expiry item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 4) {
            findings.add("Severity threshold reached for expiry");
            actions.add("Require a documented review for the expiry exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 5 * 1000.0) {
            findings.add("Value exceeds the expiry monitoring threshold");
            actions.add("Validate supporting evidence and approval for expiry");
            score -= 12;
        }
        String reference = signal.attribute("expiry");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for expiry is not recorded");
            actions.add("Attach traceable evidence for expiry");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to expiry");
            actions.add("Assign a named accountable owner for expiry");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateClassification(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
        evidence.put("control", "classification");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Classification status is missing");
            actions.add("Assign an owner and record the current classification status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Classification item is past its due date");
            actions.add("Escalate the overdue classification item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 5) {
            findings.add("Severity threshold reached for classification");
            actions.add("Require a documented review for the classification exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 7 * 1000.0) {
            findings.add("Value exceeds the classification monitoring threshold");
            actions.add("Validate supporting evidence and approval for classification");
            score -= 12;
        }
        String reference = signal.attribute("classification");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for classification is not recorded");
            actions.add("Attach traceable evidence for classification");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to classification");
            actions.add("Assign a named accountable owner for classification");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateOwner(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
        evidence.put("control", "owner");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Owner status is missing");
            actions.add("Assign an owner and record the current owner status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Owner item is past its due date");
            actions.add("Escalate the overdue owner item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 1) {
            findings.add("Severity threshold reached for owner");
            actions.add("Require a documented review for the owner exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 10 * 1000.0) {
            findings.add("Value exceeds the owner monitoring threshold");
            actions.add("Validate supporting evidence and approval for owner");
            score -= 12;
        }
        String reference = signal.attribute("owner");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for owner is not recorded");
            actions.add("Attach traceable evidence for owner");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to owner");
            actions.add("Assign a named accountable owner for owner");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateRetention(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
        evidence.put("control", "retention");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Retention status is missing");
            actions.add("Assign an owner and record the current retention status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Retention item is past its due date");
            actions.add("Escalate the overdue retention item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 2) {
            findings.add("Severity threshold reached for retention");
            actions.add("Require a documented review for the retention exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 14 * 1000.0) {
            findings.add("Value exceeds the retention monitoring threshold");
            actions.add("Validate supporting evidence and approval for retention");
            score -= 12;
        }
        String reference = signal.attribute("retention");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for retention is not recorded");
            actions.add("Attach traceable evidence for retention");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to retention");
            actions.add("Assign a named accountable owner for retention");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
    }

    public AdminAssessment evaluateDistribution(AdminDashboardSignal signal, java.time.LocalDate today) {
        java.util.Objects.requireNonNull(signal, "signal");
        java.util.Objects.requireNonNull(today, "today");
        java.util.List<String> findings = new java.util.ArrayList<>();
        java.util.List<String> actions = new java.util.ArrayList<>();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("signalId", signal.id() == null ? "unassigned" : signal.id());
        evidence.put("domain", "document control");
        evidence.put("control", "distribution");
        evidence.put("owner", signal.owner() == null ? "unassigned" : signal.owner());
        evidence.put("status", signal.status() == null ? "unknown" : signal.status());
        evidence.put("dueDate", signal.dueDate() == null ? "not-set" : signal.dueDate().toString());
        int score = 100;
        if (signal.status() == null || signal.status().isBlank()) {
            findings.add("Distribution status is missing");
            actions.add("Assign an owner and record the current distribution status");
            score -= 24;
        }
        if (signal.overdue(today)) {
            findings.add("Distribution item is past its due date");
            actions.add("Escalate the overdue distribution item and agree a recovery date");
            score -= 30;
        }
        if (signal.severity() >= 3) {
            findings.add("Severity threshold reached for distribution");
            actions.add("Require a documented review for the distribution exception");
            score -= signal.severity() * 4;
        }
        if (signal.value() > 21 * 1000.0) {
            findings.add("Value exceeds the distribution monitoring threshold");
            actions.add("Validate supporting evidence and approval for distribution");
            score -= 12;
        }
        String reference = signal.attribute("distribution");
        if (reference == null || reference.isBlank()) {
            findings.add("Evidence reference for distribution is not recorded");
            actions.add("Attach traceable evidence for distribution");
            score -= 8;
        }
        if (signal.owner() == null || signal.owner().isBlank()) {
            findings.add("No accountable owner is assigned to distribution");
            actions.add("Assign a named accountable owner for distribution");
            score -= 10;
        }
        evidence.put("evidenceReference", reference == null ? "missing" : reference);
        evidence.put("severity", Integer.toString(signal.severity()));
        evidence.put("value", Double.toString(signal.value()));
        score = Math.max(0, Math.min(100, score));
        String status = score < 40 ? "CRITICAL" : signal.overdue(today) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
        checks.add(evaluateDocument(signal, today));
        checks.add(evaluateVersion(signal, today));
        checks.add(evaluateApproval(signal, today));
        checks.add(evaluateExpiry(signal, today));
        checks.add(evaluateClassification(signal, today));
        checks.add(evaluateOwner(signal, today));
        checks.add(evaluateRetention(signal, today));
        checks.add(evaluateDistribution(signal, today));
        int score = checks.stream().mapToInt(AdminAssessment::score).sum() / Math.max(1, checks.size());
        java.util.List<String> findings = checks.stream().flatMap(item -> item.findings().stream()).distinct().toList();
        java.util.List<String> actions = checks.stream().flatMap(item -> item.recommendedActions().stream()).distinct().toList();
        java.util.Map<String, String> evidence = new java.util.LinkedHashMap<>();
        evidence.put("checks", Integer.toString(checks.size()));
        evidence.put("findings", Integer.toString(findings.size()));
        evidence.put("domain", "document control ");
        String status = checks.stream().anyMatch(item -> "CRITICAL".equals(item.status())) ? "CRITICAL" : checks.stream().anyMatch(item -> "OVERDUE".equals(item.status())) ? "OVERDUE" : score < 70 ? "REVIEW" : "HEALTHY";
        return new AdminAssessment("document control", signal == null ? null : signal.id(), status, score, findings, actions, evidence, java.time.Instant.now());
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
