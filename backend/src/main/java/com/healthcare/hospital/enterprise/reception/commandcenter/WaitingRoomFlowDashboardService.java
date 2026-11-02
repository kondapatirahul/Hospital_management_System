package com.healthcare.hospital.enterprise.reception.commandcenter;

import java.util.*;
import java.util.stream.Collectors;

/** Domain service for Waiting Room Flow; exposes front-desk worklists, explainable indicators and coverage. */
public final class WaitingRoomFlowDashboardService {
 public static final String DOMAIN=WaitingRoomFlowPolicyCatalog.DOMAIN;
 private static final List<ReceptionDashboardRule> RULES=WaitingRoomFlowPolicyCatalog.rules();
 public WaitingRoomFlowDashboardService() { }
 /** Waiting Room Flow operation: rules. */
 public List<ReceptionDashboardRule> rules() {
  return RULES;
 }
 /** Waiting Room Flow operation: ruleCount. */
 public int ruleCount() {
  return RULES.size();
 }
 /** Waiting Room Flow operation: metricNames. */
 public List<String> metricNames() {
  return RULES.stream().map(ReceptionDashboardRule::metric).distinct().toList();
 }
 /** Waiting Room Flow operation: evaluate. */
 public ReceptionDashboardEvaluation evaluate(ReceptionDashboardSignalSnapshot s) {
  return ReceptionDashboardRuleEvaluator.evaluate(DOMAIN,RULES,s);
 }
 /** Waiting Room Flow operation: actionable. */
 public List<ReceptionDashboardInsight> actionable(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s).insights();
 }
 /** Waiting Room Flow operation: critical. */
 public List<ReceptionDashboardInsight> critical(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::critical).toList();
 }
 /** Waiting Room Flow operation: highPriority. */
 public List<ReceptionDashboardInsight> highPriority(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::highPriority).toList();
 }
 /** Waiting Room Flow operation: coveragePercent. */
 public double coveragePercent(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).count()*100.0/Math.max(1,metricNames().size());
 }
 /** Waiting Room Flow operation: missingMetrics. */
 public List<String> missingMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(m->!s.contains(m)).toList();
 }
 /** Waiting Room Flow operation: observedMetrics. */
 public Map<String,Double> observedMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).collect(Collectors.toMap(m->m,s::value,(a,b)->a,LinkedHashMap::new));
 }
 /** Waiting Room Flow operation: thresholds. */
 public Map<String,Double> thresholds() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::threshold,(a,b)->a,LinkedHashMap::new));
 }
 /** Waiting Room Flow operation: actions. */
 public Map<String,String> actions() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::recommendedAction,(a,b)->a,LinkedHashMap::new));
 }
 /** Waiting Room Flow operation: severityBreakdown. */
 public Map<String,Long> severityBreakdown(ReceptionDashboardSignalSnapshot s) {
  return Arrays.stream(ReceptionDashboardSeverity.values()).collect(Collectors.toMap(Enum::name,v->actionable(s).stream().filter(x->x.severity()==v).count(),(a,b)->a,LinkedHashMap::new));
 }
 /** Waiting Room Flow operation: criticalCount. */
 public long criticalCount(ReceptionDashboardSignalSnapshot s) {
  return critical(s).size();
 }
 /** Waiting Room Flow operation: highPriorityCount. */
 public long highPriorityCount(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s).size();
 }
 /** Waiting Room Flow operation: breachedMetricCount. */
 public long breachedMetricCount(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::metric).distinct().count();
 }
 /** Waiting Room Flow operation: riskScore. */
 public double riskScore(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().mapToDouble(x->x.severity().weight()*Math.max(1,x.excess())).sum();
 }
 /** Waiting Room Flow operation: normalizedRiskScore. */
 public double normalizedRiskScore(ReceptionDashboardSignalSnapshot s) {
  return Math.min(100,riskScore(s)/Math.max(1,RULES.size()*400.0)*100.0);
 }
 /** Waiting Room Flow operation: topItems. */
 public List<ReceptionDashboardInsight> topItems(ReceptionDashboardSignalSnapshot s,int limit) {
  return limit<=0?List.of():actionable(s).stream().limit(limit).toList();
 }
 /** Waiting Room Flow operation: bySeverity. */
 public List<ReceptionDashboardInsight> bySeverity(ReceptionDashboardSignalSnapshot s,ReceptionDashboardSeverity severity) {
  return severity==null?List.of():actionable(s).stream().filter(x->x.severity()==severity).toList();
 }
 /** Waiting Room Flow operation: byMetric. */
 public List<ReceptionDashboardInsight> byMetric(ReceptionDashboardSignalSnapshot s,String metric) {
  return metric==null?List.of():actionable(s).stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Waiting Room Flow operation: rulesByMetric. */
 public List<ReceptionDashboardRule> rulesByMetric(String metric) {
  return metric==null?List.of():RULES.stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Waiting Room Flow operation: rulesBySeverity. */
 public List<ReceptionDashboardRule> rulesBySeverity(ReceptionDashboardSeverity severity) {
  return severity==null?List.of():RULES.stream().filter(x->x.severity()==severity).toList();
 }
 /** Waiting Room Flow operation: rulesAboveThreshold. */
 public List<ReceptionDashboardRule> rulesAboveThreshold(double minimum) {
  return RULES.stream().filter(x->x.threshold()>=minimum).toList();
 }
 /** Waiting Room Flow operation: rulesBelowThreshold. */
 public List<ReceptionDashboardRule> rulesBelowThreshold(double maximum) {
  return RULES.stream().filter(x->x.threshold()<maximum).toList();
 }
 /** Waiting Room Flow operation: metricCoverage. */
 public Map<String,Double> metricCoverage(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().collect(Collectors.toMap(m->m,m->s.contains(m)?1.0:0.0,(a,b)->a,LinkedHashMap::new));
 }
 /** Waiting Room Flow operation: coverageComplete. */
 public boolean coverageComplete(ReceptionDashboardSignalSnapshot s) {
  return coveragePercent(s)>=100;
 }
 /** Waiting Room Flow operation: hasCritical. */
 public boolean hasCritical(ReceptionDashboardSignalSnapshot s) {
  return criticalCount(s)>0;
 }
 /** Waiting Room Flow operation: hasActionable. */
 public boolean hasActionable(ReceptionDashboardSignalSnapshot s) {
  return !actionable(s).isEmpty();
 }
 /** Waiting Room Flow operation: status. */
 public String status(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)?"CRITICAL_REVIEW":hasActionable(s)?"ACTION_REQUIRED":coverageComplete(s)?"NO_CURRENT_BREACH":"LIMITED_SIGNAL_COVERAGE";
 }
 /** Waiting Room Flow operation: domainName. */
 public String domainName() {
  return "Waiting Room Flow";
 }
 /** Waiting Room Flow operation: domainDescription. */
 public String domainDescription() {
  return "Operational indicators for waiting room flow.";
 }
 /** Waiting Room Flow operation: capturedAt. */
 public java.time.Instant capturedAt(ReceptionDashboardSignalSnapshot s) {
  return s.capturedAt();
 }
 /** Waiting Room Flow operation: maximumObserved. */
 public double maximumObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
 }
 /** Waiting Room Flow operation: totalObserved. */
 public double totalObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).sum();
 }
 /** Waiting Room Flow operation: ruleKeys. */
 public List<String> ruleKeys() {
  return RULES.stream().map(ReceptionDashboardRule::key).toList();
 }
 /** Waiting Room Flow operation: uniqueMetricCount. */
 public int uniqueMetricCount() {
  return metricNames().size();
 }
 /** Waiting Room Flow operation: rulesByKey. */
 public List<ReceptionDashboardRule> rulesByKey(String key) {
  return key==null?List.of():RULES.stream().filter(x->x.key().equalsIgnoreCase(key)).toList();
 }
 /** Waiting Room Flow operation: excessItems. */
 public List<ReceptionDashboardInsight> excessItems(ReceptionDashboardSignalSnapshot s,double minimum) {
  return actionable(s).stream().filter(x->x.excess()>=minimum).toList();
 }
 /** Waiting Room Flow operation: groupByMetric. */
 public Map<String,List<ReceptionDashboardInsight>> groupByMetric(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(ReceptionDashboardInsight::metric,LinkedHashMap::new,Collectors.toList()));
 }
 /** Waiting Room Flow operation: groupBySeverity. */
 public Map<String,List<ReceptionDashboardInsight>> groupBySeverity(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(x->x.severity().name(),LinkedHashMap::new,Collectors.toList()));
 }
 /** Waiting Room Flow operation: triggeredKeys. */
 public Set<String> triggeredKeys(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
 }
 /** Waiting Room Flow operation: signalValue. */
 public double signalValue(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.value(metric);
 }
 /** Waiting Room Flow operation: hasSignal. */
 public boolean hasSignal(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.contains(metric);
 }
 /** Waiting Room Flow operation: thresholdFor. */
 public double thresholdFor(String metric) {
  return thresholds().getOrDefault(metric,Double.NaN);
 }
 /** Waiting Room Flow operation: actionFor. */
 public String actionFor(String metric) {
  return actions().getOrDefault(metric,"Review the reception worklist.");
 }
 /** Waiting Room Flow operation: ruleCoverageCount. */
 public int ruleCoverageCount(ReceptionDashboardSignalSnapshot s) {
  return (int)metricNames().stream().filter(s::contains).count();
 }
 /** Waiting Room Flow operation: rulesForMetricCount. */
 public int rulesForMetricCount(String metric) {
  return rulesByMetric(metric).size();
 }
 /** Waiting Room Flow operation: priorityQueue. */
 public List<ReceptionDashboardInsight> priorityQueue(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s);
 }
 /** Waiting Room Flow operation: routineQueue. */
 public List<ReceptionDashboardInsight> routineQueue(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(x->x.severity().weight()<3).toList();
 }
 /** Waiting Room Flow operation: requiresAttention. */
 public boolean requiresAttention(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)||hasActionable(s);
 }
 /** Waiting Room Flow operation: coverageLabel. */
 public String coverageLabel(ReceptionDashboardSignalSnapshot s) {
  return String.format(Locale.ROOT,"%.1f%% signals connected",coveragePercent(s));
 }
 /** Waiting Room Flow operation: evaluation. */
 public ReceptionDashboardEvaluation evaluation(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s);
 }
}
