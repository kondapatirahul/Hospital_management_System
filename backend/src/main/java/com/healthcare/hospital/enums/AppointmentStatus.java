package com.healthcare.hospital.enums;

public enum AppointmentStatus {
    SCHEDULED("Scheduled", "badge-primary"),
    CHECKED_IN("Checked In", "badge-info"),
    IN_CONSULTATION("In Consultation", "badge-warning"),
    COMPLETED("Completed", "badge-success"),
    CANCELLED("Cancelled", "badge-danger"),
    RESCHEDULED("Rescheduled", "badge-secondary");

    private final String displayName;
    private final String badgeClass;

    AppointmentStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
