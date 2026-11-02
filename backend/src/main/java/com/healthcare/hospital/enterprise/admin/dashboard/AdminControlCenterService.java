package com.healthcare.hospital.enterprise.admin.dashboard;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

/** Coordinates administrative domain policy engines for the executive admin dashboard. */
@Service
public class AdminControlCenterService {
    private final AccessGovernance accessGovernance = new AccessGovernance();
    private final DepartmentOperations departmentOperations = new DepartmentOperations();
    private final WorkforceAdministration workforceAdministration = new WorkforceAdministration();
    private final PatientExperienceOversight patientExperienceOversight = new PatientExperienceOversight();
    private final ClinicalGovernance clinicalGovernance = new ClinicalGovernance();
    private final FinancialControls financialControls = new FinancialControls();
    private final ProcurementOversight procurementOversight = new ProcurementOversight();
    private final FacilityManagement facilityManagement = new FacilityManagement();
    private final InformationSecurity informationSecurity = new InformationSecurity();
    private final ComplianceManagement complianceManagement = new ComplianceManagement();
    private final ServiceDeskManagement serviceDeskManagement = new ServiceDeskManagement();
    private final IntegrationOperations integrationOperations = new IntegrationOperations();
    private final DataQualityManagement dataQualityManagement = new DataQualityManagement();
    private final RiskManagement riskManagement = new RiskManagement();
    private final EmergencyPreparedness emergencyPreparedness = new EmergencyPreparedness();
    private final VendorManagement vendorManagement = new VendorManagement();
    private final DocumentControl documentControl = new DocumentControl();
    private final BackupContinuity backupContinuity = new BackupContinuity();
    private final CommunicationsGovernance communicationsGovernance = new CommunicationsGovernance();
    private final OperationalAnalytics operationalAnalytics = new OperationalAnalytics();
    private final CapacityPlanning capacityPlanning = new CapacityPlanning();
    private final PolicyLifecycle policyLifecycle = new PolicyLifecycle();
    private final TrainingCompliance trainingCompliance = new TrainingCompliance();
    private final IncidentCommand incidentCommand = new IncidentCommand();
    private final RecordsGovernance recordsGovernance = new RecordsGovernance();
    private final EnvironmentSafety environmentSafety = new EnvironmentSafety();
    private final ServiceAvailability serviceAvailability = new ServiceAvailability();
    private final BudgetPlanning budgetPlanning = new BudgetPlanning();
    private final ChangeManagement changeManagement = new ChangeManagement();
    private final ExecutiveReporting executiveReporting = new ExecutiveReporting();
    private final PatientSafetyOversight patientSafetyOversight = new PatientSafetyOversight();
    private final QueueAndAccessOversight queueAndAccessOversight = new QueueAndAccessOversight();
    private final ClinicalEquipmentGovernance clinicalEquipmentGovernance = new ClinicalEquipmentGovernance();
    private final RevenueIntegrity revenueIntegrity = new RevenueIntegrity();
    private final PrivacyOperations privacyOperations = new PrivacyOperations();
    private final WorkplaceRelations workplaceRelations = new WorkplaceRelations();

    public Map<String, Object> evaluateDomain(String domain, Collection<AdminDashboardSignal> signals, LocalDate today) {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(today, "today");
        String key = domain.trim().toLowerCase(Locale.ROOT).replace('-', ' ').replace('_', ' ');
        List<AdminAssessment> result = new ArrayList<>();
        if (key.equals("access governance")) result.addAll(accessGovernance.assessAll(signals, today));
        if (key.equals("department operations")) result.addAll(departmentOperations.assessAll(signals, today));
        if (key.equals("workforce administration")) result.addAll(workforceAdministration.assessAll(signals, today));
        if (key.equals("patient experience oversight")) result.addAll(patientExperienceOversight.assessAll(signals, today));
        if (key.equals("clinical governance")) result.addAll(clinicalGovernance.assessAll(signals, today));
        if (key.equals("financial controls")) result.addAll(financialControls.assessAll(signals, today));
        if (key.equals("procurement oversight")) result.addAll(procurementOversight.assessAll(signals, today));
        if (key.equals("facility management")) result.addAll(facilityManagement.assessAll(signals, today));
        if (key.equals("information security")) result.addAll(informationSecurity.assessAll(signals, today));
        if (key.equals("compliance management")) result.addAll(complianceManagement.assessAll(signals, today));
        if (key.equals("service desk management")) result.addAll(serviceDeskManagement.assessAll(signals, today));
        if (key.equals("integration operations")) result.addAll(integrationOperations.assessAll(signals, today));
        if (key.equals("data quality management")) result.addAll(dataQualityManagement.assessAll(signals, today));
        if (key.equals("risk management")) result.addAll(riskManagement.assessAll(signals, today));
        if (key.equals("emergency preparedness")) result.addAll(emergencyPreparedness.assessAll(signals, today));
        if (key.equals("vendor management")) result.addAll(vendorManagement.assessAll(signals, today));
        if (key.equals("document control")) result.addAll(documentControl.assessAll(signals, today));
        if (key.equals("backup and continuity")) result.addAll(backupContinuity.assessAll(signals, today));
        if (key.equals("communications governance")) result.addAll(communicationsGovernance.assessAll(signals, today));
        if (key.equals("operational analytics")) result.addAll(operationalAnalytics.assessAll(signals, today));
        if (key.equals("capacity planning")) result.addAll(capacityPlanning.assessAll(signals, today));
        if (key.equals("policy lifecycle")) result.addAll(policyLifecycle.assessAll(signals, today));
        if (key.equals("training compliance")) result.addAll(trainingCompliance.assessAll(signals, today));
        if (key.equals("incident command")) result.addAll(incidentCommand.assessAll(signals, today));
        if (key.equals("records governance")) result.addAll(recordsGovernance.assessAll(signals, today));
        if (key.equals("environmental safety")) result.addAll(environmentSafety.assessAll(signals, today));
        if (key.equals("service availability")) result.addAll(serviceAvailability.assessAll(signals, today));
        if (key.equals("budget planning")) result.addAll(budgetPlanning.assessAll(signals, today));
        if (key.equals("change management")) result.addAll(changeManagement.assessAll(signals, today));
        if (key.equals("executive reporting")) result.addAll(executiveReporting.assessAll(signals, today));
        if (key.equals("patient safety oversight")) result.addAll(patientSafetyOversight.assessAll(signals, today));
        if (key.equals("queue and access oversight")) result.addAll(queueAndAccessOversight.assessAll(signals, today));
        if (key.equals("clinical equipment governance")) result.addAll(clinicalEquipmentGovernance.assessAll(signals, today));
        if (key.equals("revenue integrity")) result.addAll(revenueIntegrity.assessAll(signals, today));
        if (key.equals("privacy operations")) result.addAll(privacyOperations.assessAll(signals, today));
        if (key.equals("workplace relations")) result.addAll(workplaceRelations.assessAll(signals, today));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("domain", domain);
        response.put("assessedAt", java.time.Instant.now().toString());
        response.put("total", result.size());
        response.put("attention", result.stream().filter(AdminAssessment::requiresAttention).count());
        response.put("healthIndex", result.stream().mapToInt(AdminAssessment::score).average().orElse(100.0));
        response.put("items", List.copyOf(result));
        return Map.copyOf(response);
    }

    public Map<String, Object> dashboardSnapshot(Collection<AdminDashboardSignal> signals, LocalDate today) {
        Objects.requireNonNull(today, "today");
        Collection<AdminDashboardSignal> safeSignals = signals == null ? List.of() : signals;
        Map<String, Object> result = new LinkedHashMap<>();
        List<AdminAssessment> all = new ArrayList<>();
        List<AdminAssessment> accessGovernanceItems = accessGovernance.assessAll(safeSignals, today);
        all.addAll(accessGovernanceItems);
        result.put("access-governance", accessGovernanceItems);
        List<AdminAssessment> departmentOperationsItems = departmentOperations.assessAll(safeSignals, today);
        all.addAll(departmentOperationsItems);
        result.put("department-operations", departmentOperationsItems);
        List<AdminAssessment> workforceAdministrationItems = workforceAdministration.assessAll(safeSignals, today);
        all.addAll(workforceAdministrationItems);
        result.put("workforce-administration", workforceAdministrationItems);
        List<AdminAssessment> patientExperienceOversightItems = patientExperienceOversight.assessAll(safeSignals, today);
        all.addAll(patientExperienceOversightItems);
        result.put("patient-experience-oversight", patientExperienceOversightItems);
        List<AdminAssessment> clinicalGovernanceItems = clinicalGovernance.assessAll(safeSignals, today);
        all.addAll(clinicalGovernanceItems);
        result.put("clinical-governance", clinicalGovernanceItems);
        List<AdminAssessment> financialControlsItems = financialControls.assessAll(safeSignals, today);
        all.addAll(financialControlsItems);
        result.put("financial-controls", financialControlsItems);
        List<AdminAssessment> procurementOversightItems = procurementOversight.assessAll(safeSignals, today);
        all.addAll(procurementOversightItems);
        result.put("procurement-oversight", procurementOversightItems);
        List<AdminAssessment> facilityManagementItems = facilityManagement.assessAll(safeSignals, today);
        all.addAll(facilityManagementItems);
        result.put("facility-management", facilityManagementItems);
        List<AdminAssessment> informationSecurityItems = informationSecurity.assessAll(safeSignals, today);
        all.addAll(informationSecurityItems);
        result.put("information-security", informationSecurityItems);
        List<AdminAssessment> complianceManagementItems = complianceManagement.assessAll(safeSignals, today);
        all.addAll(complianceManagementItems);
        result.put("compliance-management", complianceManagementItems);
        List<AdminAssessment> serviceDeskManagementItems = serviceDeskManagement.assessAll(safeSignals, today);
        all.addAll(serviceDeskManagementItems);
        result.put("service-desk-management", serviceDeskManagementItems);
        List<AdminAssessment> integrationOperationsItems = integrationOperations.assessAll(safeSignals, today);
        all.addAll(integrationOperationsItems);
        result.put("integration-operations", integrationOperationsItems);
        List<AdminAssessment> dataQualityManagementItems = dataQualityManagement.assessAll(safeSignals, today);
        all.addAll(dataQualityManagementItems);
        result.put("data-quality-management", dataQualityManagementItems);
        List<AdminAssessment> riskManagementItems = riskManagement.assessAll(safeSignals, today);
        all.addAll(riskManagementItems);
        result.put("risk-management", riskManagementItems);
        List<AdminAssessment> emergencyPreparednessItems = emergencyPreparedness.assessAll(safeSignals, today);
        all.addAll(emergencyPreparednessItems);
        result.put("emergency-preparedness", emergencyPreparednessItems);
        List<AdminAssessment> vendorManagementItems = vendorManagement.assessAll(safeSignals, today);
        all.addAll(vendorManagementItems);
        result.put("vendor-management", vendorManagementItems);
        List<AdminAssessment> documentControlItems = documentControl.assessAll(safeSignals, today);
        all.addAll(documentControlItems);
        result.put("document-control", documentControlItems);
        List<AdminAssessment> backupContinuityItems = backupContinuity.assessAll(safeSignals, today);
        all.addAll(backupContinuityItems);
        result.put("backup-and-continuity", backupContinuityItems);
        List<AdminAssessment> communicationsGovernanceItems = communicationsGovernance.assessAll(safeSignals, today);
        all.addAll(communicationsGovernanceItems);
        result.put("communications-governance", communicationsGovernanceItems);
        List<AdminAssessment> operationalAnalyticsItems = operationalAnalytics.assessAll(safeSignals, today);
        all.addAll(operationalAnalyticsItems);
        result.put("operational-analytics", operationalAnalyticsItems);
        List<AdminAssessment> capacityPlanningItems = capacityPlanning.assessAll(safeSignals, today);
        all.addAll(capacityPlanningItems);
        result.put("capacity-planning", capacityPlanningItems);
        List<AdminAssessment> policyLifecycleItems = policyLifecycle.assessAll(safeSignals, today);
        all.addAll(policyLifecycleItems);
        result.put("policy-lifecycle", policyLifecycleItems);
        List<AdminAssessment> trainingComplianceItems = trainingCompliance.assessAll(safeSignals, today);
        all.addAll(trainingComplianceItems);
        result.put("training-compliance", trainingComplianceItems);
        List<AdminAssessment> incidentCommandItems = incidentCommand.assessAll(safeSignals, today);
        all.addAll(incidentCommandItems);
        result.put("incident-command", incidentCommandItems);
        List<AdminAssessment> recordsGovernanceItems = recordsGovernance.assessAll(safeSignals, today);
        all.addAll(recordsGovernanceItems);
        result.put("records-governance", recordsGovernanceItems);
        List<AdminAssessment> environmentSafetyItems = environmentSafety.assessAll(safeSignals, today);
        all.addAll(environmentSafetyItems);
        result.put("environmental-safety", environmentSafetyItems);
        List<AdminAssessment> serviceAvailabilityItems = serviceAvailability.assessAll(safeSignals, today);
        all.addAll(serviceAvailabilityItems);
        result.put("service-availability", serviceAvailabilityItems);
        List<AdminAssessment> budgetPlanningItems = budgetPlanning.assessAll(safeSignals, today);
        all.addAll(budgetPlanningItems);
        result.put("budget-planning", budgetPlanningItems);
        List<AdminAssessment> changeManagementItems = changeManagement.assessAll(safeSignals, today);
        all.addAll(changeManagementItems);
        result.put("change-management", changeManagementItems);
        List<AdminAssessment> executiveReportingItems = executiveReporting.assessAll(safeSignals, today);
        all.addAll(executiveReportingItems);
        result.put("executive-reporting", executiveReportingItems);
        List<AdminAssessment> patientSafetyOversightItems = patientSafetyOversight.assessAll(safeSignals, today);
        all.addAll(patientSafetyOversightItems);
        result.put("patient-safety-oversight", patientSafetyOversightItems);
        List<AdminAssessment> queueAndAccessOversightItems = queueAndAccessOversight.assessAll(safeSignals, today);
        all.addAll(queueAndAccessOversightItems);
        result.put("queue-and-access-oversight", queueAndAccessOversightItems);
        List<AdminAssessment> clinicalEquipmentGovernanceItems = clinicalEquipmentGovernance.assessAll(safeSignals, today);
        all.addAll(clinicalEquipmentGovernanceItems);
        result.put("clinical-equipment-governance", clinicalEquipmentGovernanceItems);
        List<AdminAssessment> revenueIntegrityItems = revenueIntegrity.assessAll(safeSignals, today);
        all.addAll(revenueIntegrityItems);
        result.put("revenue-integrity", revenueIntegrityItems);
        List<AdminAssessment> privacyOperationsItems = privacyOperations.assessAll(safeSignals, today);
        all.addAll(privacyOperationsItems);
        result.put("privacy-operations", privacyOperationsItems);
        List<AdminAssessment> workplaceRelationsItems = workplaceRelations.assessAll(safeSignals, today);
        all.addAll(workplaceRelationsItems);
        result.put("workplace-relations", workplaceRelationsItems);
        result.put("totalChecks", all.size());
        result.put("attentionRequired", all.stream().filter(AdminAssessment::requiresAttention).count());
        result.put("critical", all.stream().filter(item -> "CRITICAL".equals(item.status())).count());
        result.put("overdue", all.stream().filter(item -> "OVERDUE".equals(item.status())).count());
        result.put("healthIndex", all.stream().mapToInt(AdminAssessment::score).average().orElse(100.0));
        result.put("priorityActions", all.stream().filter(AdminAssessment::requiresAttention)
                .sorted(Comparator.comparingInt(AdminAssessment::score))
                .flatMap(item -> item.recommendedActions().stream()).distinct().limit(30).toList());
        result.put("assessedAt", java.time.Instant.now().toString());
        return Collections.unmodifiableMap(result);
    }

    public List<String> supportedDomains() {
        return List.of(
            "access governance",
            "department operations",
            "workforce administration",
            "patient experience oversight",
            "clinical governance",
            "financial controls",
            "procurement oversight",
            "facility management",
            "information security",
            "compliance management",
            "service desk management",
            "integration operations",
            "data quality management",
            "risk management",
            "emergency preparedness",
            "vendor management",
            "document control",
            "backup and continuity",
            "communications governance",
            "operational analytics",
            "capacity planning",
            "policy lifecycle",
            "training compliance",
            "incident command",
            "records governance",
            "environmental safety",
            "service availability",
            "budget planning",
            "change management",
            "executive reporting",
            "patient safety oversight",
            "queue and access oversight",
            "clinical equipment governance",
            "revenue integrity",
            "privacy operations",
            "workplace relations"
        );
    }
}
