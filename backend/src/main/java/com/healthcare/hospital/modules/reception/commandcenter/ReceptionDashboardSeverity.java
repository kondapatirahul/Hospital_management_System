package com.healthcare.hospital.modules.reception.commandcenter;
public enum ReceptionDashboardSeverity{
 LOW(1),MEDIUM(2),HIGH(3),CRITICAL(4);
 private final int weight;
 ReceptionDashboardSeverity(int weight){this.weight=weight;}
 public int weight(){return weight;}
}
