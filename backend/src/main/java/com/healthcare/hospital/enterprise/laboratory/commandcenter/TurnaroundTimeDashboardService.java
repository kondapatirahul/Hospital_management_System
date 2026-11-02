package com.healthcare.hospital.enterprise.laboratory.commandcenter;

import java.util.*;
import java.util.stream.Collectors;

/** Domain service for Turnaround Time; exposes explainable worklists, coverage and operational indicators. */
public final class TurnaroundTimeDashboardService {
 public static final String DOMAIN=TurnaroundTimePolicyCatalog.DOMAIN;
 private static final List<LabDashboardRule> RULES=TurnaroundTimePolicyCatalog.rules();
 public TurnaroundTimeDashboardService() { }
 /** Turnaround Time dashboard operation: rules. */
 public List<LabDashboardRule> rules() {
  return RULES;
 }
 /** Turnaround Time dashboard operation: ruleCount. */
 public int ruleCount() {
  return RULES.size();
 }
 /** Turnaround Time dashboard operation: metricNames. */
 public List<String> metricNames() {
  return RULES.stream().map(LabDashboardRule::metric).distinct().toList();
 }
 /** Turnaround Time dashboard operation: evaluate. */
 public LabDashboardEvaluation evaluate(LabDashboardSignalSnapshot s) {
  return LabDashboardRuleEvaluator.evaluate(DOMAIN,RULES,s);
 }
 /** Turnaround Time dashboard operation: actionable. */
 public List<LabDashboardInsight> actionable(LabDashboardSignalSnapshot s) {
  return evaluate(s).insights();
 }
 /** Turnaround Time dashboard operation: critical. */
 public List<LabDashboardInsight> critical(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(LabDashboardInsight::critical).toList();
 }
 /** Turnaround Time dashboard operation: highPriority. */
 public List<LabDashboardInsight> highPriority(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(LabDashboardInsight::highPriority).toList();
 }
 /** Turnaround Time dashboard operation: coveragePercent. */
 public double coveragePercent(LabDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).count()*100.0/Math.max(1,metricNames().size());
 }
 /** Turnaround Time dashboard operation: missingMetrics. */
 public List<String> missingMetrics(LabDashboardSignalSnapshot s) {
  return metricNames().stream().filter(m->!s.contains(m)).toList();
 }
 /** Turnaround Time dashboard operation: observedMetrics. */
 public Map<String,Double> observedMetrics(LabDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).collect(Collectors.toMap(m->m,s::value,(a,b)->a,LinkedHashMap::new));
 }
 /** Turnaround Time dashboard operation: thresholds. */
 public Map<String,Double> thresholds() {
  return RULES.stream().collect(Collectors.toMap(LabDashboardRule::metric,LabDashboardRule::threshold,(a,b)->a,LinkedHashMap::new));
 }
 /** Turnaround Time dashboard operation: actions. */
 public Map<String,String> actions() {
  return RULES.stream().collect(Collectors.toMap(LabDashboardRule::metric,LabDashboardRule::recommendedAction,(a,b)->a,LinkedHashMap::new));
 }
 /** Turnaround Time dashboard operation: severityBreakdown. */
 public Map<String,Long> severityBreakdown(LabDashboardSignalSnapshot s) {
  return Arrays.stream(LabDashboardSeverity.values()).collect(Collectors.toMap(Enum::name,v->actionable(s).stream().filter(x->x.severity()==v).count(),(a,b)->a,LinkedHashMap::new));
 }
 /** Turnaround Time dashboard operation: criticalCount. */
 public long criticalCount(LabDashboardSignalSnapshot s) {
  return critical(s).size();
 }
 /** Turnaround Time dashboard operation: highPriorityCount. */
 public long highPriorityCount(LabDashboardSignalSnapshot s) {
  return highPriority(s).size();
 }
 /** Turnaround Time dashboard operation: breachedMetricCount. */
 public long breachedMetricCount(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().map(LabDashboardInsight::metric).distinct().count();
 }
 /** Turnaround Time dashboard operation: riskScore. */
 public double riskScore(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().mapToDouble(x->x.severity().weight()*Math.max(1,x.excess())).sum();
 }
 /** Turnaround Time dashboard operation: normalizedRiskScore. */
 public double normalizedRiskScore(LabDashboardSignalSnapshot s) {
  return Math.min(100,riskScore(s)/Math.max(1,RULES.size()*400.0)*100.0);
 }
 /** Turnaround Time dashboard operation: topItems. */
 public List<LabDashboardInsight> topItems(LabDashboardSignalSnapshot s,int limit) {
  return limit<=0?List.of():actionable(s).stream().limit(limit).toList();
 }
 /** Turnaround Time dashboard operation: bySeverity. */
 public List<LabDashboardInsight> bySeverity(LabDashboardSignalSnapshot s,LabDashboardSeverity severity) {
  return severity==null?List.of():actionable(s).stream().filter(x->x.severity()==severity).toList();
 }
 /** Turnaround Time dashboard operation: byMetric. */
 public List<LabDashboardInsight> byMetric(LabDashboardSignalSnapshot s,String metric) {
  return metric==null?List.of():actionable(s).stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Turnaround Time dashboard operation: rulesByMetric. */
 public List<LabDashboardRule> rulesByMetric(String metric) {
  return metric==null?List.of():RULES.stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Turnaround Time dashboard operation: rulesBySeverity. */
 public List<LabDashboardRule> rulesBySeverity(LabDashboardSeverity severity) {
  return severity==null?List.of():RULES.stream().filter(x->x.severity()==severity).toList();
 }
 /** Turnaround Time dashboard operation: rulesAboveThreshold. */
 public List<LabDashboardRule> rulesAboveThreshold(double minimum) {
  return RULES.stream().filter(x->x.threshold()>=minimum).toList();
 }
 /** Turnaround Time dashboard operation: rulesBelowThreshold. */
 public List<LabDashboardRule> rulesBelowThreshold(double maximum) {
  return RULES.stream().filter(x->x.threshold()<maximum).toList();
 }
 /** Turnaround Time dashboard operation: metricCoverage. */
 public Map<String,Double> metricCoverage(LabDashboardSignalSnapshot s) {
  return metricNames().stream().collect(Collectors.toMap(m->m,m->s.contains(m)?1.0:0.0,(a,b)->a,LinkedHashMap::new));
 }
 /** Turnaround Time dashboard operation: coverageComplete. */
 public boolean coverageComplete(LabDashboardSignalSnapshot s) {
  return coveragePercent(s)>=100.0;
 }
 /** Turnaround Time dashboard operation: hasCritical. */
 public boolean hasCritical(LabDashboardSignalSnapshot s) {
  return criticalCount(s)>0;
 }
 /** Turnaround Time dashboard operation: hasActionable. */
 public boolean hasActionable(LabDashboardSignalSnapshot s) {
  return !actionable(s).isEmpty();
 }
 /** Turnaround Time dashboard operation: status. */
 public String status(LabDashboardSignalSnapshot s) {
  return hasCritical(s)?"CRITICAL_REVIEW":hasActionable(s)?"ACTION_REQUIRED":coverageComplete(s)?"NO_CURRENT_BREACH":"LIMITED_SIGNAL_COVERAGE";
 }
 /** Turnaround Time dashboard operation: domainName. */
 public String domainName() {
  return "Turnaround Time";
 }
 /** Turnaround Time dashboard operation: domainDescription. */
 public String domainDescription() {
  return "Operational indicators for turnaround time.";
 }
 /** Turnaround Time dashboard operation: capturedAt. */
 public java.time.Instant capturedAt(LabDashboardSignalSnapshot s) {
  return s.capturedAt();
 }
 /** Turnaround Time dashboard operation: maximumObserved. */
 public double maximumObserved(LabDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
 }
 /** Turnaround Time dashboard operation: totalObserved. */
 public double totalObserved(LabDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).sum();
 }
 /** Turnaround Time dashboard operation: ruleKeys. */
 public List<String> ruleKeys() {
  return RULES.stream().map(LabDashboardRule::key).toList();
 }
 /** Turnaround Time dashboard operation: uniqueMetricCount. */
 public int uniqueMetricCount() {
  return metricNames().size();
 }
 /** Turnaround Time dashboard operation: rulesByKey. */
 public List<LabDashboardRule> rulesByKey(String key) {
  return key==null?List.of():RULES.stream().filter(x->x.key().equalsIgnoreCase(key)).toList();
 }
 /** Turnaround Time dashboard operation: excessItems. */
 public List<LabDashboardInsight> excessItems(LabDashboardSignalSnapshot s,double minimum) {
  return actionable(s).stream().filter(x->x.excess()>=minimum).toList();
 }
 /** Turnaround Time dashboard operation: groupByMetric. */
 public Map<String,List<LabDashboardInsight>> groupByMetric(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(LabDashboardInsight::metric,LinkedHashMap::new,Collectors.toList()));
 }
 /** Turnaround Time dashboard operation: groupBySeverity. */
 public Map<String,List<LabDashboardInsight>> groupBySeverity(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(x->x.severity().name(),LinkedHashMap::new,Collectors.toList()));
 }
 /** Turnaround Time dashboard operation: triggeredKeys. */
 public Set<String> triggeredKeys(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().map(LabDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
 }
 /** Turnaround Time dashboard operation: signalValue. */
 public double signalValue(LabDashboardSignalSnapshot s,String metric) {
  return s.value(metric);
 }
 /** Turnaround Time dashboard operation: hasSignal. */
 public boolean hasSignal(LabDashboardSignalSnapshot s,String metric) {
  return s.contains(metric);
 }
 /** Turnaround Time dashboard operation: thresholdFor. */
 public double thresholdFor(String metric) {
  return thresholds().getOrDefault(metric,Double.NaN);
 }
 /** Turnaround Time dashboard operation: actionFor. */
 public String actionFor(String metric) {
  return actions().getOrDefault(metric,"Review the laboratory worklist.");
 }
 /** Turnaround Time dashboard operation: ruleCoverageCount. */
 public int ruleCoverageCount(LabDashboardSignalSnapshot s) {
  return (int)metricNames().stream().filter(s::contains).count();
 }
 /** Turnaround Time dashboard operation: rulesForMetricCount. */
 public int rulesForMetricCount(String metric) {
  return rulesByMetric(metric).size();
 }
 /** Turnaround Time dashboard operation: priorityQueue. */
 public List<LabDashboardInsight> priorityQueue(LabDashboardSignalSnapshot s) {
  return highPriority(s);
 }
 /** Turnaround Time dashboard operation: routineQueue. */
 public List<LabDashboardInsight> routineQueue(LabDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(x->x.severity().weight()<3).toList();
 }
 /** Turnaround Time dashboard operation: requiresAttention. */
 public boolean requiresAttention(LabDashboardSignalSnapshot s) {
  return hasCritical(s)||hasActionable(s);
 }
 /** Turnaround Time dashboard operation: coverageLabel. */
 public String coverageLabel(LabDashboardSignalSnapshot s) {
  return String.format(Locale.ROOT,"%.1f%% signals connected",coveragePercent(s));
 }
 /** Turnaround Time dashboard operation: evaluation. */
 public LabDashboardEvaluation evaluation(LabDashboardSignalSnapshot s) {
  return evaluate(s);
 }
}
