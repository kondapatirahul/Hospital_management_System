package com.healthcare.hospital.enterprise.reception.commandcenter;

import java.util.*;
import java.util.stream.Collectors;

/** Domain service for Cash Desk Reconciliation; exposes front-desk worklists, explainable indicators and coverage. */
public final class CashDeskReconciliationDashboardService {
 public static final String DOMAIN=CashDeskReconciliationPolicyCatalog.DOMAIN;
 private static final List<ReceptionDashboardRule> RULES=CashDeskReconciliationPolicyCatalog.rules();
 public CashDeskReconciliationDashboardService() { }
 /** Cash Desk Reconciliation operation: rules. */
 public List<ReceptionDashboardRule> rules() {
  return RULES;
 }
 /** Cash Desk Reconciliation operation: ruleCount. */
 public int ruleCount() {
  return RULES.size();
 }
 /** Cash Desk Reconciliation operation: metricNames. */
 public List<String> metricNames() {
  return RULES.stream().map(ReceptionDashboardRule::metric).distinct().toList();
 }
 /** Cash Desk Reconciliation operation: evaluate. */
 public ReceptionDashboardEvaluation evaluate(ReceptionDashboardSignalSnapshot s) {
  return ReceptionDashboardRuleEvaluator.evaluate(DOMAIN,RULES,s);
 }
 /** Cash Desk Reconciliation operation: actionable. */
 public List<ReceptionDashboardInsight> actionable(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s).insights();
 }
 /** Cash Desk Reconciliation operation: critical. */
 public List<ReceptionDashboardInsight> critical(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::critical).toList();
 }
 /** Cash Desk Reconciliation operation: highPriority. */
 public List<ReceptionDashboardInsight> highPriority(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::highPriority).toList();
 }
 /** Cash Desk Reconciliation operation: coveragePercent. */
 public double coveragePercent(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).count()*100.0/Math.max(1,metricNames().size());
 }
 /** Cash Desk Reconciliation operation: missingMetrics. */
 public List<String> missingMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(m->!s.contains(m)).toList();
 }
 /** Cash Desk Reconciliation operation: observedMetrics. */
 public Map<String,Double> observedMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).collect(Collectors.toMap(m->m,s::value,(a,b)->a,LinkedHashMap::new));
 }
 /** Cash Desk Reconciliation operation: thresholds. */
 public Map<String,Double> thresholds() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::threshold,(a,b)->a,LinkedHashMap::new));
 }
 /** Cash Desk Reconciliation operation: actions. */
 public Map<String,String> actions() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::recommendedAction,(a,b)->a,LinkedHashMap::new));
 }
 /** Cash Desk Reconciliation operation: severityBreakdown. */
 public Map<String,Long> severityBreakdown(ReceptionDashboardSignalSnapshot s) {
  return Arrays.stream(ReceptionDashboardSeverity.values()).collect(Collectors.toMap(Enum::name,v->actionable(s).stream().filter(x->x.severity()==v).count(),(a,b)->a,LinkedHashMap::new));
 }
 /** Cash Desk Reconciliation operation: criticalCount. */
 public long criticalCount(ReceptionDashboardSignalSnapshot s) {
  return critical(s).size();
 }
 /** Cash Desk Reconciliation operation: highPriorityCount. */
 public long highPriorityCount(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s).size();
 }
 /** Cash Desk Reconciliation operation: breachedMetricCount. */
 public long breachedMetricCount(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::metric).distinct().count();
 }
 /** Cash Desk Reconciliation operation: riskScore. */
 public double riskScore(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().mapToDouble(x->x.severity().weight()*Math.max(1,x.excess())).sum();
 }
 /** Cash Desk Reconciliation operation: normalizedRiskScore. */
 public double normalizedRiskScore(ReceptionDashboardSignalSnapshot s) {
  return Math.min(100,riskScore(s)/Math.max(1,RULES.size()*400.0)*100.0);
 }
 /** Cash Desk Reconciliation operation: topItems. */
 public List<ReceptionDashboardInsight> topItems(ReceptionDashboardSignalSnapshot s,int limit) {
  return limit<=0?List.of():actionable(s).stream().limit(limit).toList();
 }
 /** Cash Desk Reconciliation operation: bySeverity. */
 public List<ReceptionDashboardInsight> bySeverity(ReceptionDashboardSignalSnapshot s,ReceptionDashboardSeverity severity) {
  return severity==null?List.of():actionable(s).stream().filter(x->x.severity()==severity).toList();
 }
 /** Cash Desk Reconciliation operation: byMetric. */
 public List<ReceptionDashboardInsight> byMetric(ReceptionDashboardSignalSnapshot s,String metric) {
  return metric==null?List.of():actionable(s).stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Cash Desk Reconciliation operation: rulesByMetric. */
 public List<ReceptionDashboardRule> rulesByMetric(String metric) {
  return metric==null?List.of():RULES.stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Cash Desk Reconciliation operation: rulesBySeverity. */
 public List<ReceptionDashboardRule> rulesBySeverity(ReceptionDashboardSeverity severity) {
  return severity==null?List.of():RULES.stream().filter(x->x.severity()==severity).toList();
 }
 /** Cash Desk Reconciliation operation: rulesAboveThreshold. */
 public List<ReceptionDashboardRule> rulesAboveThreshold(double minimum) {
  return RULES.stream().filter(x->x.threshold()>=minimum).toList();
 }
 /** Cash Desk Reconciliation operation: rulesBelowThreshold. */
 public List<ReceptionDashboardRule> rulesBelowThreshold(double maximum) {
  return RULES.stream().filter(x->x.threshold()<maximum).toList();
 }
 /** Cash Desk Reconciliation operation: metricCoverage. */
 public Map<String,Double> metricCoverage(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().collect(Collectors.toMap(m->m,m->s.contains(m)?1.0:0.0,(a,b)->a,LinkedHashMap::new));
 }
 /** Cash Desk Reconciliation operation: coverageComplete. */
 public boolean coverageComplete(ReceptionDashboardSignalSnapshot s) {
  return coveragePercent(s)>=100;
 }
 /** Cash Desk Reconciliation operation: hasCritical. */
 public boolean hasCritical(ReceptionDashboardSignalSnapshot s) {
  return criticalCount(s)>0;
 }
 /** Cash Desk Reconciliation operation: hasActionable. */
 public boolean hasActionable(ReceptionDashboardSignalSnapshot s) {
  return !actionable(s).isEmpty();
 }
 /** Cash Desk Reconciliation operation: status. */
 public String status(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)?"CRITICAL_REVIEW":hasActionable(s)?"ACTION_REQUIRED":coverageComplete(s)?"NO_CURRENT_BREACH":"LIMITED_SIGNAL_COVERAGE";
 }
 /** Cash Desk Reconciliation operation: domainName. */
 public String domainName() {
  return "Cash Desk Reconciliation";
 }
 /** Cash Desk Reconciliation operation: domainDescription. */
 public String domainDescription() {
  return "Operational indicators for cash desk reconciliation.";
 }
 /** Cash Desk Reconciliation operation: capturedAt. */
 public java.time.Instant capturedAt(ReceptionDashboardSignalSnapshot s) {
  return s.capturedAt();
 }
 /** Cash Desk Reconciliation operation: maximumObserved. */
 public double maximumObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
 }
 /** Cash Desk Reconciliation operation: totalObserved. */
 public double totalObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).sum();
 }
 /** Cash Desk Reconciliation operation: ruleKeys. */
 public List<String> ruleKeys() {
  return RULES.stream().map(ReceptionDashboardRule::key).toList();
 }
 /** Cash Desk Reconciliation operation: uniqueMetricCount. */
 public int uniqueMetricCount() {
  return metricNames().size();
 }
 /** Cash Desk Reconciliation operation: rulesByKey. */
 public List<ReceptionDashboardRule> rulesByKey(String key) {
  return key==null?List.of():RULES.stream().filter(x->x.key().equalsIgnoreCase(key)).toList();
 }
 /** Cash Desk Reconciliation operation: excessItems. */
 public List<ReceptionDashboardInsight> excessItems(ReceptionDashboardSignalSnapshot s,double minimum) {
  return actionable(s).stream().filter(x->x.excess()>=minimum).toList();
 }
 /** Cash Desk Reconciliation operation: groupByMetric. */
 public Map<String,List<ReceptionDashboardInsight>> groupByMetric(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(ReceptionDashboardInsight::metric,LinkedHashMap::new,Collectors.toList()));
 }
 /** Cash Desk Reconciliation operation: groupBySeverity. */
 public Map<String,List<ReceptionDashboardInsight>> groupBySeverity(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(x->x.severity().name(),LinkedHashMap::new,Collectors.toList()));
 }
 /** Cash Desk Reconciliation operation: triggeredKeys. */
 public Set<String> triggeredKeys(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
 }
 /** Cash Desk Reconciliation operation: signalValue. */
 public double signalValue(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.value(metric);
 }
 /** Cash Desk Reconciliation operation: hasSignal. */
 public boolean hasSignal(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.contains(metric);
 }
 /** Cash Desk Reconciliation operation: thresholdFor. */
 public double thresholdFor(String metric) {
  return thresholds().getOrDefault(metric,Double.NaN);
 }
 /** Cash Desk Reconciliation operation: actionFor. */
 public String actionFor(String metric) {
  return actions().getOrDefault(metric,"Review the reception worklist.");
 }
 /** Cash Desk Reconciliation operation: ruleCoverageCount. */
 public int ruleCoverageCount(ReceptionDashboardSignalSnapshot s) {
  return (int)metricNames().stream().filter(s::contains).count();
 }
 /** Cash Desk Reconciliation operation: rulesForMetricCount. */
 public int rulesForMetricCount(String metric) {
  return rulesByMetric(metric).size();
 }
 /** Cash Desk Reconciliation operation: priorityQueue. */
 public List<ReceptionDashboardInsight> priorityQueue(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s);
 }
 /** Cash Desk Reconciliation operation: routineQueue. */
 public List<ReceptionDashboardInsight> routineQueue(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(x->x.severity().weight()<3).toList();
 }
 /** Cash Desk Reconciliation operation: requiresAttention. */
 public boolean requiresAttention(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)||hasActionable(s);
 }
 /** Cash Desk Reconciliation operation: coverageLabel. */
 public String coverageLabel(ReceptionDashboardSignalSnapshot s) {
  return String.format(Locale.ROOT,"%.1f%% signals connected",coveragePercent(s));
 }
 /** Cash Desk Reconciliation operation: evaluation. */
 public ReceptionDashboardEvaluation evaluation(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s);
 }
}
