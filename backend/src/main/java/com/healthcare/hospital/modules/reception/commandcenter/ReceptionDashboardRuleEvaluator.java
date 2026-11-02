package com.healthcare.hospital.modules.reception.commandcenter;
import java.util.*;
/** Shared deterministic evaluator for front-desk worklists and coverage. */
public final class ReceptionDashboardRuleEvaluator{
 private ReceptionDashboardRuleEvaluator(){}
 public static ReceptionDashboardEvaluation evaluate(String domain,List<ReceptionDashboardRule> rules,ReceptionDashboardSignalSnapshot snapshot){
  if(rules==null||snapshot==null)throw new IllegalArgumentException("Rules and snapshot required");
  List<ReceptionDashboardInsight> items=new ArrayList<>();Map<String,Double> coverage=new LinkedHashMap<>();int available=0;
  for(ReceptionDashboardRule r:rules){boolean present=snapshot.contains(r.metric());coverage.put(r.metric(),present?1.0:0.0);if(!present)continue;
   available++;double value=snapshot.value(r.metric());if(value>0&&value>=r.threshold())
    items.add(new ReceptionDashboardInsight(domain,r.key(),r.description(),r.metric(),value,r.threshold(),r.severity(),r.recommendedAction(),snapshot.capturedAt()));
  }
  items.sort(Comparator.comparingInt((ReceptionDashboardInsight i)->i.severity().weight()).reversed().thenComparing(ReceptionDashboardInsight::excess,Comparator.reverseOrder()));
  return new ReceptionDashboardEvaluation(domain,items,rules.size(),available,coverage);
 }
 public static List<ReceptionDashboardInsight> merge(List<ReceptionDashboardEvaluation> evaluations){
  List<ReceptionDashboardInsight> all=new ArrayList<>();if(evaluations!=null)evaluations.forEach(e->all.addAll(e.insights()));
  all.sort(Comparator.comparingInt((ReceptionDashboardInsight i)->i.severity().weight()).reversed().thenComparing(ReceptionDashboardInsight::excess,Comparator.reverseOrder()));
  return List.copyOf(all);
 }
}
