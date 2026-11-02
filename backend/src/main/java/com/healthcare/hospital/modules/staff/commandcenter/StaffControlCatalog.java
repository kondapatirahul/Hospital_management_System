package com.healthcare.hospital.modules.staff.commandcenter;

import java.util.*;

/** Central registry for staff and nursing operational controls. */
public final class StaffControlCatalog {
    private StaffControlCatalog() { }
    public static Map<String,List<StaffControlRule>> allDomains() {
        Map<String,List<StaffControlRule>> result = new LinkedHashMap<>();
        result.put(CareTaskGovernance.DOMAIN, CareTaskGovernance.rules());
        result.put(MedicationAdministration.DOMAIN, MedicationAdministration.rules());
        result.put(NursingObservation.DOMAIN, NursingObservation.rules());
        result.put(WardReadiness.DOMAIN, WardReadiness.rules());
        result.put(PatientHandover.DOMAIN, PatientHandover.rules());
        result.put(PatientTransfer.DOMAIN, PatientTransfer.rules());
        result.put(DischargeCoordination.DOMAIN, DischargeCoordination.rules());
        result.put(IncidentSafety.DOMAIN, IncidentSafety.rules());
        result.put(StaffingCoverage.DOMAIN, StaffingCoverage.rules());
        result.put(AttendanceCompliance.DOMAIN, AttendanceCompliance.rules());
        result.put(InfectionPrevention.DOMAIN, InfectionPrevention.rules());
        result.put(PatientOutreach.DOMAIN, PatientOutreach.rules());
        result.put(EquipmentLifecycle.DOMAIN, EquipmentLifecycle.rules());
        result.put(ClinicalDocumentation.DOMAIN, ClinicalDocumentation.rules());
        result.put(PatientExperience.DOMAIN, PatientExperience.rules());
        result.put(WorkforceWellbeing.DOMAIN, WorkforceWellbeing.rules());
        return Collections.unmodifiableMap(result);
    }
    public static List<StaffControlRule> allRules() {
        return allDomains().values().stream().flatMap(Collection::stream).toList();
    }
    public static Optional<StaffControlRule> find(String id) {
        return allRules().stream().filter(rule -> rule.id().equals(id)).findFirst();
    }
    public static int domainCount() { return allDomains().size(); }
}
