package com.healthcare.hospital.enums;

public enum PaymentStatus {
    PAID("Paid", "badge-success"),
    PENDING("Pending", "badge-warning"),
    PARTIAL("Partial", "badge-info"),
    CANCELLED("Cancelled", "badge-danger");

    private final String displayName;
    private final String badgeClass;

    PaymentStatus(String displayName, String badgeClass) {
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
