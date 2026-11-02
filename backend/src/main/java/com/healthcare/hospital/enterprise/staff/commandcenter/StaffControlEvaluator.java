package com.healthcare.hospital.enterprise.staff.commandcenter;

import java.util.*;

/** Deterministic evaluator. Missing evidence is never represented as a healthy result. */
public final class StaffControlEvaluator {
    public StaffControlEvaluation evaluate(String domain, List<StaffControlRule> rules, Map<String,String> evidence) {
        List<StaffControlFinding> findings = new ArrayList<>(); int covered=0, review=0, critical=0, earned=0, possible=0;
        for (StaffControlRule rule : rules) {
            String value = evidence.get(rule.id());
            String status;
            if (value == null || value.isBlank()) status = rule.mandatory() ? "CRITICAL" : "REVIEW";
            else if (value.equalsIgnoreCase("pass") || value.equalsIgnoreCase("complete") || value.equalsIgnoreCase("verified")) status="HEALTHY";
            else if (value.equalsIgnoreCase("fail") || value.equalsIgnoreCase("overdue") || value.equalsIgnoreCase("blocked")) status="CRITICAL";
            else status="REVIEW";
            if (status.equals("HEALTHY")) { covered++; earned += 100; }
            else if (status.equals("REVIEW")) review++; else critical++;
            possible += 100;
            findings.add(new StaffControlFinding(rule.id(), domain, rule.title(), status, rule.action(), rule.priority(), value == null ? "Evidence not connected" : value));
        }
        int score = possible == 0 ? 0 : (int)Math.round(100.0 * earned / possible);
        findings.sort(Comparator.comparingInt(StaffControlFinding::priority).reversed().thenComparing(StaffControlFinding::status));
        return new StaffControlEvaluation(domain, rules.size(), covered, review, critical, score, findings);
    }
}
