package com.healthcare.hospital.enterprise.laboratory.commandcenter;
import java.time.Instant;
/** Explainable operational signal; not a substitute for laboratory professional review. */
public record LabDashboardInsight(String domain,String ruleKey,String description,String metric,
 double observedValue,double threshold,LabDashboardSeverity severity,String recommendedAction,Instant capturedAt){
 public boolean critical(){return severity==LabDashboardSeverity.CRITICAL;}
 public boolean highPriority(){return severity.weight()>=LabDashboardSeverity.HIGH.weight();}
 public double excess(){return Math.max(0,observedValue-threshold);}
}
