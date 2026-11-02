package com.healthcare.hospital.enterprise.staff.commandcenter;

import java.util.List;

/** Immutable aggregate for a staff governance review. */
public record StaffControlEvaluation(String domain, int total, int covered, int review, int critical, int score, List<StaffControlFinding> findings) {
    public StaffControlEvaluation { findings = List.copyOf(findings); }
}
