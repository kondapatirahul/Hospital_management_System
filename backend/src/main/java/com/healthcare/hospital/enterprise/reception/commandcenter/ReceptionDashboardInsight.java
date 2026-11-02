package com.healthcare.hospital.enterprise.reception.commandcenter;
import java.time.Instant;
/** Explainable operational signal for reception teams; not a clinical decision. */
public record ReceptionDashboardInsight(String domain,String ruleKey,String description,String metric,
 double observedValue,double threshold,ReceptionDashboardSeverity severity,String recommendedAction,Instant capturedAt){
 public boolean critical(){return severity==ReceptionDashboardSeverity.CRITICAL;}
 public boolean highPriority(){return severity.weight()>=3;}
 public double excess(){return Math.max(0,observedValue-threshold);}
}
