package com.healthcare.hospital.enterprise.laboratory.commandcenter;
public enum LabDashboardSeverity {
 LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);
 private final int weight;
 LabDashboardSeverity(int weight){this.weight=weight;}
 public int weight(){return weight;}
}
