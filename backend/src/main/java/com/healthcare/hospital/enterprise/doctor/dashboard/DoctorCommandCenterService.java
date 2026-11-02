package com.healthcare.hospital.enterprise.doctor.dashboard;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Coordinates the clinical workstream evaluators for the physician dashboard. */
@Service
public class DoctorCommandCenterService {
    private final AppointmentFlowDashboardService appointments = new AppointmentFlowDashboardService();
    private final ClinicalDocumentationDashboardService documentation = new ClinicalDocumentationDashboardService();
    private final MedicationSafetyDashboardService medication = new MedicationSafetyDashboardService();
    private final DiagnosticFollowupDashboardService diagnostics = new DiagnosticFollowupDashboardService();
    private final CareContinuityDashboardService continuity = new CareContinuityDashboardService();
    private final PatientSafetyDashboardService safety = new PatientSafetyDashboardService();
    private final ChronicCareDashboardService chronic = new ChronicCareDashboardService();
    private final InpatientRoundsDashboardService inpatient = new InpatientRoundsDashboardService();
    private final TheatreCoordinationDashboardService theatre = new TheatreCoordinationDashboardService();
    private final ReferralManagementDashboardService referrals = new ReferralManagementDashboardService();
    private final PatientCommunicationDashboardService communication = new PatientCommunicationDashboardService();
    private final QualityImprovementDashboardService quality = new QualityImprovementDashboardService();
    private final CapacityPlanningDashboardService capacity = new CapacityPlanningDashboardService();
    private final WorkloadBalanceDashboardService workload = new WorkloadBalanceDashboardService();
    private final DataGovernanceDashboardService governance = new DataGovernanceDashboardService();
    private final ProfessionalDevelopmentDashboardService development = new ProfessionalDevelopmentDashboardService();

    public List<DoctorDashboardEvaluation> evaluate(DoctorDashboardSignalSnapshot snapshot) {
        List<DoctorDashboardEvaluation> results = new ArrayList<>();
        results.add(appointments.evaluate(snapshot));
        results.add(documentation.evaluate(snapshot));
        results.add(medication.evaluate(snapshot));
        results.add(diagnostics.evaluate(snapshot));
        results.add(continuity.evaluate(snapshot));
        results.add(safety.evaluate(snapshot));
        results.add(chronic.evaluate(snapshot));
        results.add(inpatient.evaluate(snapshot));
        results.add(theatre.evaluate(snapshot));
        results.add(referrals.evaluate(snapshot));
        results.add(communication.evaluate(snapshot));
        results.add(quality.evaluate(snapshot));
        results.add(capacity.evaluate(snapshot));
        results.add(workload.evaluate(snapshot));
        results.add(governance.evaluate(snapshot));
        results.add(development.evaluate(snapshot));
        return List.copyOf(results);
    }

    public Map<String,Object> summary(DoctorDashboardSignalSnapshot snapshot) {
        List<DoctorDashboardEvaluation> evaluations = evaluate(snapshot);
        List<DoctorDashboardInsight> insights = DoctorDashboardRuleEvaluator.merge(evaluations);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("domains", evaluations);
        result.put("insights", insights);
        result.put("domainCount", evaluations.size());
        result.put("rulesEvaluated", evaluations.stream().mapToInt(DoctorDashboardEvaluation::rulesEvaluated).sum());
        result.put("metricsAvailable", evaluations.stream().mapToInt(DoctorDashboardEvaluation::metricsAvailable).sum());
        result.put("criticalCount", insights.stream().filter(DoctorDashboardInsight::isCritical).count());
        result.put("highPriorityCount", insights.stream().filter(DoctorDashboardInsight::isHighPriority).count());
        result.put("capturedAt", snapshot.capturedAt());
        return Map.copyOf(result);
    }
}
