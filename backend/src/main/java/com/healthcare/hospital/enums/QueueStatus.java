package com.healthcare.hospital.enums;

public enum QueueStatus {
    WAITING("Waiting in Lobby", "badge-warning"),
    IN_CONSULTATION("In Consultation", "badge-info"),
    COMPLETED("Finished Consultation", "badge-success"),
    SKIPPED("Skipped", "badge-secondary");

    private final String displayName;
    private final String badgeClass;

    QueueStatus(String displayName, String badgeClass) {
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
