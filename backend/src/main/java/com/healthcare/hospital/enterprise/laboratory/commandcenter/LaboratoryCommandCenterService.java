package com.healthcare.hospital.enterprise.laboratory.commandcenter;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LaboratoryCommandCenterService {
 private final List<Object> services=List.of(
  new SpecimenIntakeDashboardService(),new AccessioningDashboardService(),
  new AnalyticalQualityDashboardService(),new ResultVerificationDashboardService(),
  new TurnaroundTimeDashboardService(),new EquipmentReadinessDashboardService(),
  new ReagentInventoryDashboardService(),new BloodBankSafetyDashboardService(),
  new MicrobiologyWorkflowDashboardService(),new HistopathologyWorkflowDashboardService(),
  new SampleRejectionDashboardService(),new ReferralTestingDashboardService(),
  new ComplianceGovernanceDashboardService(),new WorkforceCoverageDashboardService(),
  new PatientCommunicationDashboardService(),new DataInteroperabilityDashboardService());
 public Map<String,Object> summary(LabDashboardSignalSnapshot snapshot){
  List<LabDashboardEvaluation> evaluations=new ArrayList<>();
  evaluations.add(new SpecimenIntakeDashboardService().evaluate(snapshot));
  evaluations.add(new AccessioningDashboardService().evaluate(snapshot));
  evaluations.add(new AnalyticalQualityDashboardService().evaluate(snapshot));
  evaluations.add(new ResultVerificationDashboardService().evaluate(snapshot));
  evaluations.add(new TurnaroundTimeDashboardService().evaluate(snapshot));
  evaluations.add(new EquipmentReadinessDashboardService().evaluate(snapshot));
  evaluations.add(new ReagentInventoryDashboardService().evaluate(snapshot));
  evaluations.add(new BloodBankSafetyDashboardService().evaluate(snapshot));
  evaluations.add(new MicrobiologyWorkflowDashboardService().evaluate(snapshot));
  evaluations.add(new HistopathologyWorkflowDashboardService().evaluate(snapshot));
  evaluations.add(new SampleRejectionDashboardService().evaluate(snapshot));
  evaluations.add(new ReferralTestingDashboardService().evaluate(snapshot));
  evaluations.add(new ComplianceGovernanceDashboardService().evaluate(snapshot));
  evaluations.add(new WorkforceCoverageDashboardService().evaluate(snapshot));
  evaluations.add(new PatientCommunicationDashboardService().evaluate(snapshot));
  evaluations.add(new DataInteroperabilityDashboardService().evaluate(snapshot));
  List<LabDashboardInsight> insights=LabDashboardRuleEvaluator.merge(evaluations);
  Map<String,Object> result=new LinkedHashMap<>();
  result.put("domains",evaluations); result.put("insights",insights);
  result.put("domainCount",evaluations.size());
  result.put("rulesEvaluated",evaluations.stream().mapToInt(LabDashboardEvaluation::rulesEvaluated).sum());
  result.put("metricsAvailable",evaluations.stream().mapToInt(LabDashboardEvaluation::metricsAvailable).sum());
  result.put("criticalCount",insights.stream().filter(LabDashboardInsight::critical).count());
  result.put("highPriorityCount",insights.stream().filter(LabDashboardInsight::highPriority).count());
  result.put("capturedAt",snapshot.capturedAt());
  return Map.copyOf(result);
 }
}
