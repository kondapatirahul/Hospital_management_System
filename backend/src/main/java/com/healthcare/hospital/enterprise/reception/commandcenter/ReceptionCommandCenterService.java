package com.healthcare.hospital.enterprise.reception.commandcenter;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Coordinates all front-desk domains into one prioritized receptionist workspace. */
@Service
public class ReceptionCommandCenterService {
 public Map<String,Object> summarize(ReceptionDashboardSignalSnapshot snapshot){
  List<ReceptionDashboardEvaluation> evaluations=new ArrayList<>();
  evaluations.add(new PatientRegistrationDashboardService().evaluate(snapshot));
  evaluations.add(new AppointmentSchedulingDashboardService().evaluate(snapshot));
  evaluations.add(new ArrivalCheckInDashboardService().evaluate(snapshot));
  evaluations.add(new WaitingRoomFlowDashboardService().evaluate(snapshot));
  evaluations.add(new QueueCoordinationDashboardService().evaluate(snapshot));
  evaluations.add(new PatientRecordsDashboardService().evaluate(snapshot));
  evaluations.add(new DoctorAvailabilityDashboardService().evaluate(snapshot));
  evaluations.add(new BillingHandoffDashboardService().evaluate(snapshot));
  evaluations.add(new InsuranceEligibilityDashboardService().evaluate(snapshot));
  evaluations.add(new CommunicationDeskDashboardService().evaluate(snapshot));
  evaluations.add(new ReferralCoordinationDashboardService().evaluate(snapshot));
  evaluations.add(new DischargeDeskDashboardService().evaluate(snapshot));
  evaluations.add(new FrontDeskCapacityDashboardService().evaluate(snapshot));
  evaluations.add(new CashDeskReconciliationDashboardService().evaluate(snapshot));
  evaluations.add(new PrivacyAndConsentDashboardService().evaluate(snapshot));
  evaluations.add(new ServiceRecoveryDashboardService().evaluate(snapshot));
  List<ReceptionDashboardInsight> insights=ReceptionDashboardRuleEvaluator.merge(evaluations);
  Map<String,Object> result=new LinkedHashMap<>();
  result.put("domains",evaluations);result.put("insights",insights);
  result.put("domainCount",evaluations.size());
  result.put("rulesEvaluated",evaluations.stream().mapToInt(ReceptionDashboardEvaluation::rulesEvaluated).sum());
  result.put("metricsAvailable",evaluations.stream().mapToInt(ReceptionDashboardEvaluation::metricsAvailable).sum());
  result.put("criticalCount",insights.stream().filter(ReceptionDashboardInsight::critical).count());
  result.put("highPriorityCount",insights.stream().filter(ReceptionDashboardInsight::highPriority).count());
  result.put("capturedAt",snapshot.capturedAt());
  return Map.copyOf(result);
 }
}
