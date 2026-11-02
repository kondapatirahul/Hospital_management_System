package com.healthcare.hospital.enterprise.reception.commandcenter;

import java.util.*;
import java.util.stream.Collectors;

/** Domain service for Patient Records; exposes front-desk worklists, explainable indicators and coverage. */
public final class PatientRecordsDashboardService {
 public static final String DOMAIN=PatientRecordsPolicyCatalog.DOMAIN;
 private static final List<ReceptionDashboardRule> RULES=PatientRecordsPolicyCatalog.rules();
 public PatientRecordsDashboardService() { }
 /** Patient Records operation: rules. */
 public List<ReceptionDashboardRule> rules() {
  return RULES;
 }
 /** Patient Records operation: ruleCount. */
 public int ruleCount() {
  return RULES.size();
 }
 /** Patient Records operation: metricNames. */
 public List<String> metricNames() {
  return RULES.stream().map(ReceptionDashboardRule::metric).distinct().toList();
 }
 /** Patient Records operation: evaluate. */
 public ReceptionDashboardEvaluation evaluate(ReceptionDashboardSignalSnapshot s) {
  return ReceptionDashboardRuleEvaluator.evaluate(DOMAIN,RULES,s);
 }
 /** Patient Records operation: actionable. */
 public List<ReceptionDashboardInsight> actionable(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s).insights();
 }
 /** Patient Records operation: critical. */
 public List<ReceptionDashboardInsight> critical(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::critical).toList();
 }
 /** Patient Records operation: highPriority. */
 public List<ReceptionDashboardInsight> highPriority(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(ReceptionDashboardInsight::highPriority).toList();
 }
 /** Patient Records operation: coveragePercent. */
 public double coveragePercent(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).count()*100.0/Math.max(1,metricNames().size());
 }
 /** Patient Records operation: missingMetrics. */
 public List<String> missingMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(m->!s.contains(m)).toList();
 }
 /** Patient Records operation: observedMetrics. */
 public Map<String,Double> observedMetrics(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().filter(s::contains).collect(Collectors.toMap(m->m,s::value,(a,b)->a,LinkedHashMap::new));
 }
 /** Patient Records operation: thresholds. */
 public Map<String,Double> thresholds() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::threshold,(a,b)->a,LinkedHashMap::new));
 }
 /** Patient Records operation: actions. */
 public Map<String,String> actions() {
  return RULES.stream().collect(Collectors.toMap(ReceptionDashboardRule::metric,ReceptionDashboardRule::recommendedAction,(a,b)->a,LinkedHashMap::new));
 }
 /** Patient Records operation: severityBreakdown. */
 public Map<String,Long> severityBreakdown(ReceptionDashboardSignalSnapshot s) {
  return Arrays.stream(ReceptionDashboardSeverity.values()).collect(Collectors.toMap(Enum::name,v->actionable(s).stream().filter(x->x.severity()==v).count(),(a,b)->a,LinkedHashMap::new));
 }
 /** Patient Records operation: criticalCount. */
 public long criticalCount(ReceptionDashboardSignalSnapshot s) {
  return critical(s).size();
 }
 /** Patient Records operation: highPriorityCount. */
 public long highPriorityCount(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s).size();
 }
 /** Patient Records operation: breachedMetricCount. */
 public long breachedMetricCount(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::metric).distinct().count();
 }
 /** Patient Records operation: riskScore. */
 public double riskScore(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().mapToDouble(x->x.severity().weight()*Math.max(1,x.excess())).sum();
 }
 /** Patient Records operation: normalizedRiskScore. */
 public double normalizedRiskScore(ReceptionDashboardSignalSnapshot s) {
  return Math.min(100,riskScore(s)/Math.max(1,RULES.size()*400.0)*100.0);
 }
 /** Patient Records operation: topItems. */
 public List<ReceptionDashboardInsight> topItems(ReceptionDashboardSignalSnapshot s,int limit) {
  return limit<=0?List.of():actionable(s).stream().limit(limit).toList();
 }
 /** Patient Records operation: bySeverity. */
 public List<ReceptionDashboardInsight> bySeverity(ReceptionDashboardSignalSnapshot s,ReceptionDashboardSeverity severity) {
  return severity==null?List.of():actionable(s).stream().filter(x->x.severity()==severity).toList();
 }
 /** Patient Records operation: byMetric. */
 public List<ReceptionDashboardInsight> byMetric(ReceptionDashboardSignalSnapshot s,String metric) {
  return metric==null?List.of():actionable(s).stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Patient Records operation: rulesByMetric. */
 public List<ReceptionDashboardRule> rulesByMetric(String metric) {
  return metric==null?List.of():RULES.stream().filter(x->x.metric().equalsIgnoreCase(metric)).toList();
 }
 /** Patient Records operation: rulesBySeverity. */
 public List<ReceptionDashboardRule> rulesBySeverity(ReceptionDashboardSeverity severity) {
  return severity==null?List.of():RULES.stream().filter(x->x.severity()==severity).toList();
 }
 /** Patient Records operation: rulesAboveThreshold. */
 public List<ReceptionDashboardRule> rulesAboveThreshold(double minimum) {
  return RULES.stream().filter(x->x.threshold()>=minimum).toList();
 }
 /** Patient Records operation: rulesBelowThreshold. */
 public List<ReceptionDashboardRule> rulesBelowThreshold(double maximum) {
  return RULES.stream().filter(x->x.threshold()<maximum).toList();
 }
 /** Patient Records operation: metricCoverage. */
 public Map<String,Double> metricCoverage(ReceptionDashboardSignalSnapshot s) {
  return metricNames().stream().collect(Collectors.toMap(m->m,m->s.contains(m)?1.0:0.0,(a,b)->a,LinkedHashMap::new));
 }
 /** Patient Records operation: coverageComplete. */
 public boolean coverageComplete(ReceptionDashboardSignalSnapshot s) {
  return coveragePercent(s)>=100;
 }
 /** Patient Records operation: hasCritical. */
 public boolean hasCritical(ReceptionDashboardSignalSnapshot s) {
  return criticalCount(s)>0;
 }
 /** Patient Records operation: hasActionable. */
 public boolean hasActionable(ReceptionDashboardSignalSnapshot s) {
  return !actionable(s).isEmpty();
 }
 /** Patient Records operation: status. */
 public String status(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)?"CRITICAL_REVIEW":hasActionable(s)?"ACTION_REQUIRED":coverageComplete(s)?"NO_CURRENT_BREACH":"LIMITED_SIGNAL_COVERAGE";
 }
 /** Patient Records operation: domainName. */
 public String domainName() {
  return "Patient Records";
 }
 /** Patient Records operation: domainDescription. */
 public String domainDescription() {
  return "Operational indicators for patient records.";
 }
 /** Patient Records operation: capturedAt. */
 public java.time.Instant capturedAt(ReceptionDashboardSignalSnapshot s) {
  return s.capturedAt();
 }
 /** Patient Records operation: maximumObserved. */
 public double maximumObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
 }
 /** Patient Records operation: totalObserved. */
 public double totalObserved(ReceptionDashboardSignalSnapshot s) {
  return observedMetrics(s).values().stream().mapToDouble(Double::doubleValue).sum();
 }
 /** Patient Records operation: ruleKeys. */
 public List<String> ruleKeys() {
  return RULES.stream().map(ReceptionDashboardRule::key).toList();
 }
 /** Patient Records operation: uniqueMetricCount. */
 public int uniqueMetricCount() {
  return metricNames().size();
 }
 /** Patient Records operation: rulesByKey. */
 public List<ReceptionDashboardRule> rulesByKey(String key) {
  return key==null?List.of():RULES.stream().filter(x->x.key().equalsIgnoreCase(key)).toList();
 }
 /** Patient Records operation: excessItems. */
 public List<ReceptionDashboardInsight> excessItems(ReceptionDashboardSignalSnapshot s,double minimum) {
  return actionable(s).stream().filter(x->x.excess()>=minimum).toList();
 }
 /** Patient Records operation: groupByMetric. */
 public Map<String,List<ReceptionDashboardInsight>> groupByMetric(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(ReceptionDashboardInsight::metric,LinkedHashMap::new,Collectors.toList()));
 }
 /** Patient Records operation: groupBySeverity. */
 public Map<String,List<ReceptionDashboardInsight>> groupBySeverity(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().collect(Collectors.groupingBy(x->x.severity().name(),LinkedHashMap::new,Collectors.toList()));
 }
 /** Patient Records operation: triggeredKeys. */
 public Set<String> triggeredKeys(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().map(ReceptionDashboardInsight::ruleKey).collect(Collectors.toUnmodifiableSet());
 }
 /** Patient Records operation: signalValue. */
 public double signalValue(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.value(metric);
 }
 /** Patient Records operation: hasSignal. */
 public boolean hasSignal(ReceptionDashboardSignalSnapshot s,String metric) {
  return s.contains(metric);
 }
 /** Patient Records operation: thresholdFor. */
 public double thresholdFor(String metric) {
  return thresholds().getOrDefault(metric,Double.NaN);
 }
 /** Patient Records operation: actionFor. */
 public String actionFor(String metric) {
  return actions().getOrDefault(metric,"Review the reception worklist.");
 }
 /** Patient Records operation: ruleCoverageCount. */
 public int ruleCoverageCount(ReceptionDashboardSignalSnapshot s) {
  return (int)metricNames().stream().filter(s::contains).count();
 }
 /** Patient Records operation: rulesForMetricCount. */
 public int rulesForMetricCount(String metric) {
  return rulesByMetric(metric).size();
 }
 /** Patient Records operation: priorityQueue. */
 public List<ReceptionDashboardInsight> priorityQueue(ReceptionDashboardSignalSnapshot s) {
  return highPriority(s);
 }
 /** Patient Records operation: routineQueue. */
 public List<ReceptionDashboardInsight> routineQueue(ReceptionDashboardSignalSnapshot s) {
  return actionable(s).stream().filter(x->x.severity().weight()<3).toList();
 }
 /** Patient Records operation: requiresAttention. */
 public boolean requiresAttention(ReceptionDashboardSignalSnapshot s) {
  return hasCritical(s)||hasActionable(s);
 }
 /** Patient Records operation: coverageLabel. */
 public String coverageLabel(ReceptionDashboardSignalSnapshot s) {
  return String.format(Locale.ROOT,"%.1f%% signals connected",coveragePercent(s));
 }
 /** Patient Records operation: evaluation. */
 public ReceptionDashboardEvaluation evaluation(ReceptionDashboardSignalSnapshot s) {
  return evaluate(s);
 }
}
