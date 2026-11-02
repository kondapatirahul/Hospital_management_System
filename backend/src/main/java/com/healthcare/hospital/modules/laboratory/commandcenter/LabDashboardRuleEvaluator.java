package com.healthcare.hospital.modules.laboratory.commandcenter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** Deterministic rule evaluator shared by all laboratory operational workstreams. */
public final class LabDashboardRuleEvaluator{
 private LabDashboardRuleEvaluator(){}
 public static LabDashboardEvaluation evaluate(String domain,List<LabDashboardRule> rules,LabDashboardSignalSnapshot snapshot){
  if(rules==null||snapshot==null)throw new IllegalArgumentException("Rules and snapshot required");
  List<LabDashboardInsight> insights=new ArrayList<>();
  Map<String,Double> coverage=new LinkedHashMap<>();
  int available=0;
  for(LabDashboardRule rule:rules){
   boolean present=snapshot.contains(rule.metric());
   coverage.put(rule.metric(),present?1.0:0.0);
   if(!present)continue;
   available++;
   double observed=snapshot.value(rule.metric());
   if(observed>0&&observed>=rule.threshold()){
    insights.add(new LabDashboardInsight(domain,rule.key(),rule.description(),rule.metric(),observed,
     rule.threshold(),rule.severity(),rule.recommendedAction(),snapshot.capturedAt()));
   }
  }
  insights.sort(Comparator.comparingInt((LabDashboardInsight i)->i.severity().weight()).reversed()
   .thenComparing(LabDashboardInsight::excess,Comparator.reverseOrder()));
  return new LabDashboardEvaluation(domain,insights,rules.size(),available,coverage);
 }
 public static List<LabDashboardInsight> merge(List<LabDashboardEvaluation> evaluations){
  List<LabDashboardInsight> result=new ArrayList<>();
  if(evaluations!=null)evaluations.forEach(e->result.addAll(e.insights()));
  result.sort(Comparator.comparingInt((LabDashboardInsight i)->i.severity().weight()).reversed()
   .thenComparing(LabDashboardInsight::excess,Comparator.reverseOrder()));
  return List.copyOf(result);
 }
}
