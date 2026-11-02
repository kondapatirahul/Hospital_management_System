package com.healthcare.hospital.modules.laboratory.commandcenter;
import java.util.List;
import java.util.Map;
public record LabDashboardEvaluation(String domain,List<LabDashboardInsight> insights,int rulesEvaluated,
 int metricsAvailable,Map<String,Double> coverage){
 public LabDashboardEvaluation{
  insights=List.copyOf(insights==null?List.of():insights);
  coverage=Map.copyOf(coverage==null?Map.of():coverage);
 }
 public long criticalCount(){return insights.stream().filter(LabDashboardInsight::critical).count();}
 public long highPriorityCount(){return insights.stream().filter(LabDashboardInsight::highPriority).count();}
}
