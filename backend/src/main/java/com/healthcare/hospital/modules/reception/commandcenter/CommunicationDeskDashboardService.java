package com.healthcare.hospital.modules.reception.commandcenter;

import java.util.*;
import java.util.stream.Collectors;

/** Domain service for Communication Desk; exposes front-desk worklists, explainable indicators and coverage. */
public final class CommunicationDeskDashboardService {
 public static final String DOMAIN=CommunicationDeskPolicyCatalog.DOMAIN;
 private static final List<ReceptionDashboardRule> RULES=CommunicationDeskPolicyCatalog.rules();
 public CommunicationDeskDashboardService() { }
 /** Communication Desk operation: rules. */
 public List<ReceptionDashboardRule> rules() {
  return RULES;
 }
 /** Communication Desk operation: ruleCount. */
 public int ruleCount() {
  return RULES.size();
 }
 /** Communication Desk operation: metricNames. */
 public List<String> metricNames() {
  return RULES.stream().map(ReceptionDashboardRule::metric).distinct().toList();
 }
 /** Communication Desk operation: evaluate. */
 public ReceptionDashboardEvaluation evaluate(ReceptionDashboardSignalSnapshot s) {
  return ReceptionDashboardRuleEvaluator.evaluate(DOMAIN,RULES,s);
 }
 /** Communication Desk operation: actionable. */
 public List<ReceptionDashboardInsight> actionable(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s).insights();
 }
 /** Communication Desk operation: critical. */
 public List<ReceptionDashboardInsight> critical(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::critical).toList();
 }
 /** Communication Desk operation: highPriority. */
 public List<ReceptionDashboardInsight> highPriority(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::highPriority).toList();
 }
 /** Communication Desk operation: coveragePercent. */
 public double coveragePercent(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).count()*100.0/Math.max(1,metricNames().size());
 }
 /** Communication Desk operation: missingMetrics. */
 public List<String> missingMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(m->!s.contains(m)).toList();
 }
 /** Communication Desk operation: observedMetrics. */
 public Map<String,Double> observedMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).collect(Collectors.toMap(m->m,s::value,(a,b)->a,LinkedHashMap::new));
 }
 /** Communication Desk operation: thresholds. */
 public Map<String,Double> thresholds() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::threshold,(a,b)->a,LinkedHashMap::new));
 }
 /** Communication Desk operation: actions. */
 public Map<String,String> actions() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::recommendedAction,(a,b)->a,LinkedHashMap::new));
 }
 /** Communication Desk operation: severityBreakdown. */
 public Map<String,Long> severityBreakdown(ReceptionDashboardSignalSnapshot s) {
  return Arrays.stream(ReceptionDashboardSeverity.values()).collect(Collectors.toMap(Enum::name,v->actionable(s).stream().filter(x->x.severity()==v).count(),(a,b)->a,LinkedHashMap::new));
 }
 /** Communication Desk operation: criticalCount. */
 public long criticalCount(ReceptionDashboardSignalSnapshot s) {
  return critical(s).size();
 }
 /** Communication Desk operation: highPriorityCount. */
 public long highPriorityCount(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s).size();
 }
 /** Communication Desk operation: breachedMetricCount. */
 public long breachedMetricCount(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::metric).distinct().count();
 }
 /** Communication Desk operation: riskScore. */
 public double riskScore(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().mapToDouble(x->x.severity().weight()*Math.max(1,x.excess())).sum();
 }
 /** Communication Desk operation: normalizedRiskScore. */
 public double normalizedRiskScore(ReceptionDashboardSignalSnapshot s) {
  return Math.min(100,riskScore(s)/Math.max(1,RULES.size()*400.0)*100.0);
 }
 /** Communication Desk operation: topItems. */
 public List<ReceptionDashboardInsight> topItems(ReceptionDashboardSignalSnapshot s,int limit) {
  return limit<=0?List.of():actionable(s).stream().limit(limit).toList();
 }
 /** Communication Desk operation: bySeverity. */
 public List<ReceptionDashboardInsight> bySeverity(ReceptionDashboardSignalSnapshot s,ReceptionDashboardSeverity severity) {
  return severity==null?List.of():actionable(s).stream().filter(x->x.severity()==severity).toList();
 }
 /** Communication Desk operation: byMetric. */
 public List<ReceptionDashboardInsight> byMetric(ReceptionDashboardSignalSnapshot s,String metric) {
  return metric==null?List.of():actionable(s).stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Communication Desk operation: rulesByMetric. */
 public List<ReceptionDashboardRule> rulesByMetric(String metric) {
  return metric==null?List.of():RULES.stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Communication Desk operation: rulesBySeverity. */
 public List<ReceptionDashboardRule> rulesBySeverity(ReceptionDashboardSeverity severity) {
  return severity==null?List.of():RULES.stream().filter(x->x.severity()==severity).toList();
 }
 /** Communication Desk operation: rulesAboveThreshold. */
 public List<ReceptionDashboardRule> rulesAboveThreshold(double minimum) {
  return RULES.stream().filter(x->x.threshold()>=minimum).toList();
 }
 /** Communication Desk operation: rulesBelowThreshold. */
 public List<ReceptionDashboardRule> rulesBelowThreshold(double maximum) {
  return RULES.stream().filter(x->x.threshold()<maximum).toList();
 }
 /** Communication Desk operation: metricCoverage. */
 public Map<String,Double> metricCoverage(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().collect(Collectors.toMap(m->m,m->s.contains(m)?1.0:0.0,(a,b)->a,LinkedHashMap::new));
 }
 /** Communication Desk operation: coverageComplete. */
 public boolean coverageComplete(ReceptionDashboardSignalSnapshot s) {
  return coveragePercent(s)>=100;
 }
 /** Communication Desk operation: hasCritical. */
 public boolean hasCritical(ReceptionDashboardSignalSnapshot s) {
  return criticalCount(s)>0;
 }
 /** Communication Desk operation: hasActionable. */
 public boolean hasActionable(ReceptionDashboardSignalSnapshot s) {
  return !actionable(s).isEmpty();
 }
 /** Communication Desk operation: status. */
 public String status(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)?"CRITICAL_REVIEW":hasActionable(s)?"ACTION_REQUIRED":coverageComplete(s)?"NO_CURRENT_BREACH":"LIMITED_SIGNAL_COVERAGE";
 }
 /** Communication Desk operation: domainName. */
 public String domainName() {
  return "Communication Desk";
 }
 /** Communication Desk operation: domainDescription. */
 public String domainDescription() {
  return "Operational indicators for communication desk.";
 }
 /** Communication Desk operation: capturedAt. */
 public java.time.Instant capturedAt(ReceptionDashboardSignalSnapshot s) {
  return s.capturedAt();
 }
 /** Communication Desk operation: maximumObserved. */
 public double maximumObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
 }
 /** Communication Desk operation: totalObserved. */
 public double totalObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).sum();
 }
 /** Communication Desk operation: ruleKeys. */
 public List<String> ruleKeys() {
  return RULES.stream().map(ReceptionDashboardRule::key).toList();
 }
 /** Communication Desk operation: uniqueMetricCount. */
 public int uniqueMetricCount() {
  return metricNames().size();
 }
 /** Communication Desk operation: rulesByKey. */
 public List<ReceptionDashboardRule> rulesByKey(String key) {
  return key==null?List.of():RULES.stream().filter(x->x.key().equalsIgnoreCase(key)).toList();
 }
 /** Communication Desk operation: excessItems. */
 public List<ReceptionDashboardInsight> excessItems(ReceptionDashboardSignalSnapshot s,double minimum) {
  return actionable(s).stream().filter(x->x.excess()>=minimum).toList();
 }
 /** Communication Desk operation: groupByMetric. */
 public Map<String,List<ReceptionDashboardInsight>> groupByMetric(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(ReceptionDashboardInsight::metric,LinkedHashMap::new,Collectors.toList()));
 }
 /** Communication Desk operation: groupBySeverity. */
 public Map<String,List<ReceptionDashboardInsight>> groupBySeverity(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(x->x.severity().name(),LinkedHashMap::new,Collectors.toList()));
 }
 /** Communication Desk operation: triggeredKeys. */
 public Set<String> triggeredKeys(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
 }
 /** Communication Desk operation: signalValue. */
 public double signalValue(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.value(metric);
 }
 /** Communication Desk operation: hasSignal. */
 public boolean hasSignal(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.contains(metric);
 }
 /** Communication Desk operation: thresholdFor. */
 public double thresholdFor(String metric) {
  return thresholds().getOrDefault(metric,Double.NaN);
 }
 /** Communication Desk operation: actionFor. */
 public String actionFor(String metric) {
  return actions().getOrDefault(metric,"Review the reception worklist.");
 }
 /** Communication Desk operation: ruleCoverageCount. */
 public int ruleCoverageCount(ReceptionDashboardSignalSnapshot s) {
  return (int)metricNames().stream().filter(s::contains).count();
 }
 /** Communication Desk operation: rulesForMetricCount. */
 public int rulesForMetricCount(String metric) {
  return rulesByMetric(metric).size();
 }
 /** Communication Desk operation: priorityQueue. */
 public List<ReceptionDashboardInsight> priorityQueue(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s);
 }
 /** Communication Desk operation: routineQueue. */
 public List<ReceptionDashboardInsight> routineQueue(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(x->x.severity().weight()<3).toList();
 }
 /** Communication Desk operation: requiresAttention. */
 public boolean requiresAttention(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)||hasActionable(s);
 }
 /** Communication Desk operation: coverageLabel. */
 public String coverageLabel(ReceptionDashboardSignalSnapshot s) {
  return String.format(Locale.ROOT,"%.1f%% signals connected",coveragePercent(s));
 }
 /** Communication Desk operation: evaluation. */
 public ReceptionDashboardEvaluation evaluation(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s);
 }
}
