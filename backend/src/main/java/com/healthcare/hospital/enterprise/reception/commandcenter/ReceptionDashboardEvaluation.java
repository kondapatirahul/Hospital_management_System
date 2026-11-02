package com.healthcare.hospital.enterprise.reception.commandcenter;
import java.util.List;import java.util.Map;
public record ReceptionDashboardEvaluation(String domain,List<ReceptionDashboardInsight> insights,int rulesEvaluated,
 int metricsAvailable,Map<String,Double> coverage){
 public ReceptionDashboardEvaluation{insights=List.copyOf(insights==null?List.of():insights);coverage=Map.copyOf(coverage==null?Map.of():coverage);}
 public long criticalCount(){return insights.stream().filter(ReceptionDashboardInsight::critical).count();}
 public long highPriorityCount(){return insights.stream().filter(ReceptionDashboardInsight::highPriority).count();}
}
