package com.healthcare.hospital.modules.staff.commandcenter;

import java.util.Objects;

/** Immutable operational control definition for the staff command centre. */
public record StaffControlRule(String id, String domain, String title, String evidence, String action, int priority, boolean mandatory) {
    public StaffControlRule {
        Objects.requireNonNull(id); Objects.requireNonNull(domain); Objects.requireNonNull(title);
        Objects.requireNonNull(evidence); Objects.requireNonNull(action);
        if (priority < 1 || priority > 5) throw new IllegalArgumentException("priority must be 1..5");
    }
}
