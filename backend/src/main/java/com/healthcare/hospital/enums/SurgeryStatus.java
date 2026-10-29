package com.healthcare.hospital.enums;

/**
 * Lifecycle states of an Operation Theatre surgery workflow.
 */
public enum SurgeryStatus {
    SCHEDULED,
    PREPARATION,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    POSTPONED
}
