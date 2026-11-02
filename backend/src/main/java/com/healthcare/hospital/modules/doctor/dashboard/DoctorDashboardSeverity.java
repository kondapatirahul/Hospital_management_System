package com.healthcare.hospital.modules.doctor.dashboard;
public enum DoctorDashboardSeverity {
    LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);
    private final int weight;
    DoctorDashboardSeverity(int weight) { this.weight = weight; }
    public int weight() { return weight; }
    public static DoctorDashboardSeverity fromWeight(int weight) {
        for (DoctorDashboardSeverity value : values()) if (value.weight == weight) return value;
        return LOW;
    }
}
