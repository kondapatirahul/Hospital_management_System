package com.healthcare.hospital.enums;

public enum LabReportStatus {
    PENDING("Processing", "badge-warning"),
    COMPLETED("Ready", "badge-primary"),
    REVIEWED("Reviewed by Doctor", "badge-success");

    private final String displayName;
    private final String badgeClass;

    LabReportStatus(String displayName, String badgeClass) {
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
