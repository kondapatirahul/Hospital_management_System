package com.healthcare.hospital.enterprise.reception.commandcenter;

import java.util.*;
import java.util.stream.Collectors;

/** Domain service for Insurance Eligibility; exposes front-desk worklists, explainable indicators and coverage. */
public final class InsuranceEligibilityDashboardService {
 public static final String DOMAIN=InsuranceEligibilityPolicyCatalog.DOMAIN;
 private static final List<ReceptionDashboardRule> RULES=InsuranceEligibilityPolicyCatalog.rules();
 public InsuranceEligibilityDashboardService() { }
 /** Insurance Eligibility operation: rules. */
 public List<ReceptionDashboardRule> rules() {
  return RULES;
 }
 /** Insurance Eligibility operation: ruleCount. */
 public int ruleCount() {
  return RULES.size();
 }
 /** Insurance Eligibility operation: metricNames. */
 public List<String> metricNames() {
  return RULES.stream().map(ReceptionDashboardRule::metric).distinct().toList();
 }
 /** Insurance Eligibility operation: evaluate. */
 public ReceptionDashboardEvaluation evaluate(ReceptionDashboardSignalSnapshot s) {
  return ReceptionDashboardRuleEvaluator.evaluate(DOMAIN,RULES,s);
 }
 /** Insurance Eligibility operation: actionable. */
 public List<ReceptionDashboardInsight> actionable(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s).insights();
 }
 /** Insurance Eligibility operation: critical. */
 public List<ReceptionDashboardInsight> critical(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::critical).toList();
 }
 /** Insurance Eligibility operation: highPriority. */
 public List<ReceptionDashboardInsight> highPriority(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::highPriority).toList();
 }
 /** Insurance Eligibility operation: coveragePercent. */
 public double coveragePercent(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).count()*100.0/Math.max(1,metricNames().size());
 }
 /** Insurance Eligibility operation: missingMetrics. */
 public List<String> missingMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(m->!s.contains(m)).toList();
 }
 /** Insurance Eligibility operation: observedMetrics. */
 public Map<String,Double> observedMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).collect(Collectors.toMap(m->m,s::value,(a,b)->a,LinkedHashMap::new));
 }
 /** Insurance Eligibility operation: thresholds. */
 public Map<String,Double> thresholds() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::threshold,(a,b)->a,LinkedHashMap::new));
 }
 /** Insurance Eligibility operation: actions. */
 public Map<String,String> actions() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::recommendedAction,(a,b)->a,LinkedHashMap::new));
 }
 /** Insurance Eligibility operation: severityBreakdown. */
 public Map<String,Long> severityBreakdown(ReceptionDashboardSignalSnapshot s) {
  return Arrays.stream(ReceptionDashboardSeverity.values()).collect(Collectors.toMap(Enum::name,v->actionable(s).stream().filter(x->x.severity()==v).count(),(a,b)->a,LinkedHashMap::new));
 }
 /** Insurance Eligibility operation: criticalCount. */
 public long criticalCount(ReceptionDashboardSignalSnapshot s) {
  return critical(s).size();
 }
 /** Insurance Eligibility operation: highPriorityCount. */
 public long highPriorityCount(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s).size();
 }
 /** Insurance Eligibility operation: breachedMetricCount. */
 public long breachedMetricCount(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::metric).distinct().count();
 }
 /** Insurance Eligibility operation: riskScore. */
 public double riskScore(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().mapToDouble(x->x.severity().weight()*Math.max(1,x.excess())).sum();
 }
 /** Insurance Eligibility operation: normalizedRiskScore. */
 public double normalizedRiskScore(ReceptionDashboardSignalSnapshot s) {
  return Math.min(100,riskScore(s)/Math.max(1,RULES.size()*400.0)*100.0);
 }
 /** Insurance Eligibility operation: topItems. */
 public List<ReceptionDashboardInsight> topItems(ReceptionDashboardSignalSnapshot s,int limit) {
  return limit<=0?List.of():actionable(s).stream().limit(limit).toList();
 }
 /** Insurance Eligibility operation: bySeverity. */
 public List<ReceptionDashboardInsight> bySeverity(ReceptionDashboardSignalSnapshot s,ReceptionDashboardSeverity severity) {
  return severity==null?List.of():actionable(s).stream().filter(x->x.severity()==severity).toList();
 }
 /** Insurance Eligibility operation: byMetric. */
 public List<ReceptionDashboardInsight> byMetric(ReceptionDashboardSignalSnapshot s,String metric) {
  return metric==null?List.of():actionable(s).stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Insurance Eligibility operation: rulesByMetric. */
 public List<ReceptionDashboardRule> rulesByMetric(String metric) {
  return metric==null?List.of():RULES.stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Insurance Eligibility operation: rulesBySeverity. */
 public List<ReceptionDashboardRule> rulesBySeverity(ReceptionDashboardSeverity severity) {
  return severity==null?List.of():RULES.stream().filter(x->x.severity()==severity).toList();
 }
 /** Insurance Eligibility operation: rulesAboveThreshold. */
 public List<ReceptionDashboardRule> rulesAboveThreshold(double minimum) {
  return RULES.stream().filter(x->x.threshold()>=minimum).toList();
 }
 /** Insurance Eligibility operation: rulesBelowThreshold. */
 public List<ReceptionDashboardRule> rulesBelowThreshold(double maximum) {
  return RULES.stream().filter(x->x.threshold()<maximum).toList();
 }
 /** Insurance Eligibility operation: metricCoverage. */
 public Map<String,Double> metricCoverage(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().collect(Collectors.toMap(m->m,m->s.contains(m)?1.0:0.0,(a,b)->a,LinkedHashMap::new));
 }
 /** Insurance Eligibility operation: coverageComplete. */
 public boolean coverageComplete(ReceptionDashboardSignalSnapshot s) {
  return coveragePercent(s)>=100;
 }
 /** Insurance Eligibility operation: hasCritical. */
 public boolean hasCritical(ReceptionDashboardSignalSnapshot s) {
  return criticalCount(s)>0;
 }
 /** Insurance Eligibility operation: hasActionable. */
 public boolean hasActionable(ReceptionDashboardSignalSnapshot s) {
  return !actionable(s).isEmpty();
 }
 /** Insurance Eligibility operation: status. */
 public String status(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)?"CRITICAL_REVIEW":hasActionable(s)?"ACTION_REQUIRED":coverageComplete(s)?"NO_CURRENT_BREACH":"LIMITED_SIGNAL_COVERAGE";
 }
 /** Insurance Eligibility operation: domainName. */
 public String domainName() {
  return "Insurance Eligibility";
 }
 /** Insurance Eligibility operation: domainDescription. */
 public String domainDescription() {
  return "Operational indicators for insurance eligibility.";
 }
 /** Insurance Eligibility operation: capturedAt. */
 public java.time.Instant capturedAt(ReceptionDashboardSignalSnapshot s) {
  return s.capturedAt();
 }
 /** Insurance Eligibility operation: maximumObserved. */
 public double maximumObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
 }
 /** Insurance Eligibility operation: totalObserved. */
 public double totalObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).sum();
 }
 /** Insurance Eligibility operation: ruleKeys. */
 public List<String> ruleKeys() {
  return RULES.stream().map(ReceptionDashboardRule::key).toList();
 }
 /** Insurance Eligibility operation: uniqueMetricCount. */
 public int uniqueMetricCount() {
  return metricNames().size();
 }
 /** Insurance Eligibility operation: rulesByKey. */
 public List<ReceptionDashboardRule> rulesByKey(String key) {
  return key==null?List.of():RULES.stream().filter(x->x.key().equalsIgnoreCase(key)).toList();
 }
 /** Insurance Eligibility operation: excessItems. */
 public List<ReceptionDashboardInsight> excessItems(ReceptionDashboardSignalSnapshot s,double minimum) {
  return actionable(s).stream().filter(x->x.excess()>=minimum).toList();
 }
 /** Insurance Eligibility operation: groupByMetric. */
 public Map<String,List<ReceptionDashboardInsight>> groupByMetric(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(ReceptionDashboardInsight::metric,LinkedHashMap::new,Collectors.toList()));
 }
 /** Insurance Eligibility operation: groupBySeverity. */
 public Map<String,List<ReceptionDashboardInsight>> groupBySeverity(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(x->x.severity().name(),LinkedHashMap::new,Collectors.toList()));
 }
 /** Insurance Eligibility operation: triggeredKeys. */
 public Set<String> triggeredKeys(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
 }
 /** Insurance Eligibility operation: signalValue. */
 public double signalValue(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.value(metric);
 }
 /** Insurance Eligibility operation: hasSignal. */
 public boolean hasSignal(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.contains(metric);
 }
 /** Insurance Eligibility operation: thresholdFor. */
 public double thresholdFor(String metric) {
  return thresholds().getOrDefault(metric,Double.NaN);
 }
 /** Insurance Eligibility operation: actionFor. */
 public String actionFor(String metric) {
  return actions().getOrDefault(metric,"Review the reception worklist.");
 }
 /** Insurance Eligibility operation: ruleCoverageCount. */
 public int ruleCoverageCount(ReceptionDashboardSignalSnapshot s) {
  return (int)metricNames().stream().filter(s::contains).count();
 }
 /** Insurance Eligibility operation: rulesForMetricCount. */
 public int rulesForMetricCount(String metric) {
  return rulesByMetric(metric).size();
 }
 /** Insurance Eligibility operation: priorityQueue. */
 public List<ReceptionDashboardInsight> priorityQueue(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s);
 }
 /** Insurance Eligibility operation: routineQueue. */
 public List<ReceptionDashboardInsight> routineQueue(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(x->x.severity().weight()<3).toList();
 }
 /** Insurance Eligibility operation: requiresAttention. */
 public boolean requiresAttention(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)||hasActionable(s);
 }
 /** Insurance Eligibility operation: coverageLabel. */
 public String coverageLabel(ReceptionDashboardSignalSnapshot s) {
  return String.format(Locale.ROOT,"%.1f%% signals connected",coveragePercent(s));
 }
 /** Insurance Eligibility operation: evaluation. */
 public ReceptionDashboardEvaluation evaluation(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s);
 }
}
