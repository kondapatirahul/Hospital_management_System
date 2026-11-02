package com.healthcare.hospital.modules.laboratory.commandcenter;

import java.util.*;
import java.util.stream.Collectors;

/** Domain service for Reagent Inventory; exposes explainable worklists, coverage and operational indicators. */
public final class ReagentInventoryDashboardService {
 public static final String DOMAIN=ReagentInventoryPolicyCatalog.DOMAIN;
 private static final List<LabDashboardRule> RULES=ReagentInventoryPolicyCatalog.rules();
 public ReagentInventoryDashboardService() { }
 /** Reagent Inventory dashboard operation: rules. */
 public List<LabDashboardRule> rules() {
  return RULES;
 }
 /** Reagent Inventory dashboard operation: ruleCount. */
 public int ruleCount() {
  return RULES.size();
 }
 /** Reagent Inventory dashboard operation: metricNames. */
 public List<String> metricNames() {
  return RULES.stream().map(LabDashboardRule::metric).distinct().toList();
 }
 /** Reagent Inventory dashboard operation: evaluate. */
 public LabDashboardEvaluation evaluate(LabDashboardSignalSnapshot s) {
  return LabDashboardRuleEvaluator.evaluate(DOMAIN,RULES,s);
 }
 /** Reagent Inventory dashboard operation: actionable. */
 public List<LabDashboardInsight> actionable(LabDashboardSignalSnapshot s) {
  return evaluate(s).insights();
 }
 /** Reagent Inventory dashboard operation: critical. */
 public List<LabDashboardInsight> critical(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(LabDashboardInsight::critical).toList();
 }
 /** Reagent Inventory dashboard operation: highPriority. */
 public List<LabDashboardInsight> highPriority(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(LabDashboardInsight::highPriority).toList();
 }
 /** Reagent Inventory dashboard operation: coveragePercent. */
 public double coveragePercent(LabDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).count()*100.0/Math.max(1,metricNames().size());
 }
 /** Reagent Inventory dashboard operation: missingMetrics. */
 public List<String> missingMetrics(LabDashboardSignalSnapshot s) {
  return metricNames().stream().filter(m->!s.contains(m)).toList();
 }
 /** Reagent Inventory dashboard operation: observedMetrics. */
 public Map<String,Double> observedMetrics(LabDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).collect(Collectors.toMap(m->m,s::value,(a,b)->a,LinkedHashMap::new));
 }
 /** Reagent Inventory dashboard operation: thresholds. */
 public Map<String,Double> thresholds() {
  return RULES.stream().collect(Collectors.toMap(LabDashboardRule::metric,LabDashboardRule::threshold,(a,b)->a,LinkedHashMap::new));
 }
 /** Reagent Inventory dashboard operation: actions. */
 public Map<String,String> actions() {
  return RULES.stream().collect(Collectors.toMap(LabDashboardRule::metric,LabDashboardRule::recommendedAction,(a,b)->a,LinkedHashMap::new));
 }
 /** Reagent Inventory dashboard operation: severityBreakdown. */
 public Map<String,Long> severityBreakdown(LabDashboardSignalSnapshot s) {
  return Arrays.stream(LabDashboardSeverity.values()).collect(Collectors.toMap(Enum::name,v->actionable(s).stream().filter(x->x.severity()==v).count(),(a,b)->a,LinkedHashMap::new));
 }
 /** Reagent Inventory dashboard operation: criticalCount. */
 public long criticalCount(LabDashboardSignalSnapshot s) {
  return critical(s).size();
 }
 /** Reagent Inventory dashboard operation: highPriorityCount. */
 public long highPriorityCount(LabDashboardSignalSnapshot s) {
  return highPriority(s).size();
 }
 /** Reagent Inventory dashboard operation: breachedMetricCount. */
 public long breachedMetricCount(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().map(LabDashboardInsight::metric).distinct().count();
 }
 /** Reagent Inventory dashboard operation: riskScore. */
 public double riskScore(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().mapToDouble(x->x.severity().weight()*Math.max(1,x.excess())).sum();
 }
 /** Reagent Inventory dashboard operation: normalizedRiskScore. */
 public double normalizedRiskScore(LabDashboardSignalSnapshot s) {
  return Math.min(100,riskScore(s)/Math.max(1,RULES.size()*400.0)*100.0);
 }
 /** Reagent Inventory dashboard operation: topItems. */
 public List<LabDashboardInsight> topItems(LabDashboardSignalSnapshot s,int limit) {
  return limit<=0?List.of():actionable(s).stream().limit(limit).toList();
 }
 /** Reagent Inventory dashboard operation: bySeverity. */
 public List<LabDashboardInsight> bySeverity(LabDashboardSignalSnapshot s,LabDashboardSeverity severity) {
  return severity==null?List.of():actionable(s).stream().filter(x->x.severity()==severity).toList();
 }
 /** Reagent Inventory dashboard operation: byMetric. */
 public List<LabDashboardInsight> byMetric(LabDashboardSignalSnapshot s,String metric) {
  return metric==null?List.of():actionable(s).stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Reagent Inventory dashboard operation: rulesByMetric. */
 public List<LabDashboardRule> rulesByMetric(String metric) {
  return metric==null?List.of():RULES.stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Reagent Inventory dashboard operation: rulesBySeverity. */
 public List<LabDashboardRule> rulesBySeverity(LabDashboardSeverity severity) {
  return severity==null?List.of():RULES.stream().filter(x->x.severity()==severity).toList();
 }
 /** Reagent Inventory dashboard operation: rulesAboveThreshold. */
 public List<LabDashboardRule> rulesAboveThreshold(double minimum) {
  return RULES.stream().filter(x->x.threshold()>=minimum).toList();
 }
 /** Reagent Inventory dashboard operation: rulesBelowThreshold. */
 public List<LabDashboardRule> rulesBelowThreshold(double maximum) {
  return RULES.stream().filter(x->x.threshold()<maximum).toList();
 }
 /** Reagent Inventory dashboard operation: metricCoverage. */
 public Map<String,Double> metricCoverage(LabDashboardSignalSnapshot s) {
  return metricNames().stream().collect(Collectors.toMap(m->m,m->s.contains(m)?1.0:0.0,(a,b)->a,LinkedHashMap::new));
 }
 /** Reagent Inventory dashboard operation: coverageComplete. */
 public boolean coverageComplete(LabDashboardSignalSnapshot s) {
  return coveragePercent(s)>=100.0;
 }
 /** Reagent Inventory dashboard operation: hasCritical. */
 public boolean hasCritical(LabDashboardSignalSnapshot s) {
  return criticalCount(s)>0;
 }
 /** Reagent Inventory dashboard operation: hasActionable. */
 public boolean hasActionable(LabDashboardSignalSnapshot s) {
  return !actionable(s).isEmpty();
 }
 /** Reagent Inventory dashboard operation: status. */
 public String status(LabDashboardSignalSnapshot s) {
  return hasCritical(s)?"CRITICAL_REVIEW":hasActionable(s)?"ACTION_REQUIRED":coverageComplete(s)?"NO_CURRENT_BREACH":"LIMITED_SIGNAL_COVERAGE";
 }
 /** Reagent Inventory dashboard operation: domainName. */
 public String domainName() {
  return "Reagent Inventory";
 }
 /** Reagent Inventory dashboard operation: domainDescription. */
 public String domainDescription() {
  return "Operational indicators for reagent inventory.";
 }
 /** Reagent Inventory dashboard operation: capturedAt. */
 public java.time.Instant capturedAt(LabDashboardSignalSnapshot s) {
  return s.capturedAt();
 }
 /** Reagent Inventory dashboard operation: maximumObserved. */
 public double maximumObserved(LabDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
 }
 /** Reagent Inventory dashboard operation: totalObserved. */
 public double totalObserved(LabDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).sum();
 }
 /** Reagent Inventory dashboard operation: ruleKeys. */
 public List<String> ruleKeys() {
  return RULES.stream().map(LabDashboardRule::key).toList();
 }
 /** Reagent Inventory dashboard operation: uniqueMetricCount. */
 public int uniqueMetricCount() {
  return metricNames().size();
 }
 /** Reagent Inventory dashboard operation: rulesByKey. */
 public List<LabDashboardRule> rulesByKey(String key) {
  return key==null?List.of():RULES.stream().filter(x->x.key().equalsIgnoreCase(key)).toList();
 }
 /** Reagent Inventory dashboard operation: excessItems. */
 public List<LabDashboardInsight> excessItems(LabDashboardSignalSnapshot s,double minimum) {
  return actionable(s).stream().filter(x->x.excess()>=minimum).toList();
 }
 /** Reagent Inventory dashboard operation: groupByMetric. */
 public Map<String,List<LabDashboardInsight>> groupByMetric(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(LabDashboardInsight::metric,LinkedHashMap::new,Collectors.toList()));
 }
 /** Reagent Inventory dashboard operation: groupBySeverity. */
 public Map<String,List<LabDashboardInsight>> groupBySeverity(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(x->x.severity().name(),LinkedHashMap::new,Collectors.toList()));
 }
 /** Reagent Inventory dashboard operation: triggeredKeys. */
 public Set<String> triggeredKeys(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().map(LabDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
 }
 /** Reagent Inventory dashboard operation: signalValue. */
 public double signalValue(LabDashboardSignalSnapshot s,String metric) {
  return s.value(metric);
 }
 /** Reagent Inventory dashboard operation: hasSignal. */
 public boolean hasSignal(LabDashboardSignalSnapshot s,String metric) {
  return s.contains(metric);
 }
 /** Reagent Inventory dashboard operation: thresholdFor. */
 public double thresholdFor(String metric) {
  return thresholds().getOrDefault(metric,Double.NaN);
 }
 /** Reagent Inventory dashboard operation: actionFor. */
 public String actionFor(String metric) {
  return actions().getOrDefault(metric,"Review the laboratory worklist.");
 }
 /** Reagent Inventory dashboard operation: ruleCoverageCount. */
 public int ruleCoverageCount(LabDashboardSignalSnapshot s) {
  return (int)metricNames().stream().filter(s::contains).count();
 }
 /** Reagent Inventory dashboard operation: rulesForMetricCount. */
 public int rulesForMetricCount(String metric) {
  return rulesByMetric(metric).size();
 }
 /** Reagent Inventory dashboard operation: priorityQueue. */
 public List<LabDashboardInsight> priorityQueue(LabDashboardSignalSnapshot s) {
  return highPriority(s);
 }
 /** Reagent Inventory dashboard operation: routineQueue. */
 public List<LabDashboardInsight> routineQueue(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(x->x.severity().weight()<3).toList();
 }
 /** Reagent Inventory dashboard operation: requiresAttention. */
 public boolean requiresAttention(LabDashboardSignalSnapshot s) {
  return hasCritical(s)||hasActionable(s);
 }
 /** Reagent Inventory dashboard operation: coverageLabel. */
 public String coverageLabel(LabDashboardSignalSnapshot s) {
  return String.format(Locale.ROOT,"%.1f%% signals connected",coveragePercent(s));
 }
 /** Reagent Inventory dashboard operation: evaluation. */
 public LabDashboardEvaluation evaluation(LabDashboardSignalSnapshot s) {
  return evaluate(s);
 }
}
