package com.healthcare.hospital.modules.operations;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Spring-managed application facade for the CareFlow operational policy engines.
 * This layer exposes policy metadata and transition decisions to controllers and
 * other services without allowing web code to depend on implementation details.
 */
@Service
public class OperationsPolicyFacade {
    private static final Map<String, java.util.function.Supplier<Map<String, Object>>> CATALOG = catalogSuppliers();

    private static Map<String, java.util.function.Supplier<Map<String, Object>>> catalogSuppliers() {
        Map<String, java.util.function.Supplier<Map<String, Object>>> map = new LinkedHashMap<>();
        map.put("APPOINTMENT", AppointmentCapacityService::policyDescriptor);
        map.put("PATIENT_FLOW", PatientFlowService::policyDescriptor);
        map.put("SPECIMEN", SpecimenTrackingService::policyDescriptor);
        map.put("PHARMACY_STOCK", PharmacyStockControlService::policyDescriptor);
        map.put("BILLING_RECON", BillingReconciliationService::policyDescriptor);
        map.put("STAFF_ROSTER", StaffRosterService::policyDescriptor);
        map.put("DISCHARGE_PLAN", DischargePlanningService::policyDescriptor);
        map.put("REFERRAL", ReferralCoordinationService::policyDescriptor);
        map.put("INCIDENT", IncidentManagementService::policyDescriptor);
        map.put("CONSENT", ConsentLifecycleService::policyDescriptor);
        map.put("DOCUMENT_RETENTION", DocumentRetentionService::policyDescriptor);
        map.put("QUALITY_MEASURE", QualityMeasurementService::policyDescriptor);
        map.put("ASSET_MAINTENANCE", AssetMaintenanceService::policyDescriptor);
        map.put("CLAIM", ClaimsProcessingService::policyDescriptor);
        map.put("NOTIFICATION", NotificationDeliveryService::policyDescriptor);
        map.put("ACCESS_REVIEW", AccessGovernanceService::policyDescriptor);
        map.put("DATA_EXPORT", DataExportService::policyDescriptor);
        map.put("INTEGRATION_SYNC", IntegrationSyncService::policyDescriptor);
        map.put("WARD_COORDINATION", WardCoordinationService::policyDescriptor);
        map.put("PROCUREMENT", ProcurementPlanningService::policyDescriptor);
        return Map.copyOf(map);
    }

    public List<Map<String, Object>> catalog() {
        return CATALOG.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getValue().get())
                .toList();
    }

    public Map<String, Object> describe(String domain) {
        String key = OperationSupport.required(domain, "domain", 60).toUpperCase();
        var supplier = CATALOG.get(key);
        if (supplier == null) throw new IllegalArgumentException("Unknown operations domain: " + key);
        return supplier.get();
    }

    public TransitionDecision checkTransition(String domain, String from, String to) {
        String key = OperationSupport.required(domain, "domain", 60).toUpperCase();
        String source = OperationSupport.required(from, "from", 40);
        String target = OperationSupport.required(to, "to", 40);
        boolean allowed = switch (key) {
            case "APPOINTMENT" -> AppointmentCapacityService.canTransition(source, target);
            case "PATIENT_FLOW" -> PatientFlowService.canTransition(source, target);
            case "SPECIMEN" -> SpecimenTrackingService.canTransition(source, target);
            case "PHARMACY_STOCK" -> PharmacyStockControlService.canTransition(source, target);
            case "BILLING_RECON" -> BillingReconciliationService.canTransition(source, target);
            case "STAFF_ROSTER" -> StaffRosterService.canTransition(source, target);
            case "DISCHARGE_PLAN" -> DischargePlanningService.canTransition(source, target);
            case "REFERRAL" -> ReferralCoordinationService.canTransition(source, target);
            case "INCIDENT" -> IncidentManagementService.canTransition(source, target);
            case "CONSENT" -> ConsentLifecycleService.canTransition(source, target);
            case "DOCUMENT_RETENTION" -> DocumentRetentionService.canTransition(source, target);
            case "QUALITY_MEASURE" -> QualityMeasurementService.canTransition(source, target);
            case "ASSET_MAINTENANCE" -> AssetMaintenanceService.canTransition(source, target);
            case "CLAIM" -> ClaimsProcessingService.canTransition(source, target);
            case "NOTIFICATION" -> NotificationDeliveryService.canTransition(source, target);
            case "ACCESS_REVIEW" -> AccessGovernanceService.canTransition(source, target);
            case "DATA_EXPORT" -> DataExportService.canTransition(source, target);
            case "INTEGRATION_SYNC" -> IntegrationSyncService.canTransition(source, target);
            case "WARD_COORDINATION" -> WardCoordinationService.canTransition(source, target);
            case "PROCUREMENT" -> ProcurementPlanningService.canTransition(source, target);
            default -> throw new IllegalArgumentException("Unknown operations domain: " + key);
        };
        return new TransitionDecision(key, source, target, allowed,
                allowed ? "Transition is allowed by the domain policy" : "Transition is not allowed by the domain policy");
    }

    public record TransitionDecision(String domain, String from, String to, boolean allowed, String message) {}
}
