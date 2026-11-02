package com.healthcare.hospital.modules.reception.commandcenter;
public record ReceptionDashboardRule(String key,String description,String metric,double threshold,
 ReceptionDashboardSeverity severity,String recommendedAction){
 public ReceptionDashboardRule{
  if(key==null||key.isBlank())throw new IllegalArgumentException("Rule key required");
  if(description==null||description.isBlank())throw new IllegalArgumentException("Description required");
  if(metric==null||metric.isBlank())throw new IllegalArgumentException("Metric required");
  if(threshold<0||!Double.isFinite(threshold))throw new IllegalArgumentException("Invalid threshold");
  if(severity==null)throw new IllegalArgumentException("Severity required");
  if(recommendedAction==null||recommendedAction.isBlank())throw new IllegalArgumentException("Action required");
 }
 public static ReceptionDashboardRule of(String k,String d,String m,double t,ReceptionDashboardSeverity s,String a){
  return new ReceptionDashboardRule(k,d,m,t,s,a);
 }
}
