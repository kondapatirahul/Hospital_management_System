package com.healthcare.hospital.enterprise.admin.dashboard;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Immutable result produced by dashboard policy engines. */
public record AdminAssessment(String domain, String subject, String status, int score,
        List<String> findings, List<String> recommendedActions, Map<String, String> evidence, Instant assessedAt) {
    public AdminAssessment {
        findings = findings == null ? List.of() : List.copyOf(findings);
        recommendedActions = recommendedActions == null ? List.of() : List.copyOf(recommendedActions);
        evidence = evidence == null ? Map.of() : Map.copyOf(evidence);
        assessedAt = assessedAt == null ? Instant.now() : assessedAt;
        score = Math.max(0, Math.min(100, score));
    }
    public boolean requiresAttention() { return score < 70 || "CRITICAL".equals(status) || "OVERDUE".equals(status); }
    public boolean isHealthy() { return score >= 85 && findings.isEmpty(); }
}
