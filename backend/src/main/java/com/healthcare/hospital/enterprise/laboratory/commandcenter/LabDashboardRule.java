package com.healthcare.hospital.enterprise.laboratory.commandcenter;
public record LabDashboardRule(String key,String description,String metric,double threshold,
 LabDashboardSeverity severity,String recommendedAction){
 public LabDashboardRule{
  if(key==null||key.isBlank())throw new IllegalArgumentException("Rule key required");
  if(description==null||description.isBlank())throw new IllegalArgumentException("Description required");
  if(metric==null||metric.isBlank())throw new IllegalArgumentException("Metric required");
  if(threshold<0||!Double.isFinite(threshold))throw new IllegalArgumentException("Invalid threshold");
  if(severity==null)throw new IllegalArgumentException("Severity required");
  if(recommendedAction==null||recommendedAction.isBlank())throw new IllegalArgumentException("Action required");
 }
 public static LabDashboardRule of(String key,String description,String metric,double threshold,
                                   LabDashboardSeverity severity,String action){
  return new LabDashboardRule(key,description,metric,threshold,severity,action);
 }
}
