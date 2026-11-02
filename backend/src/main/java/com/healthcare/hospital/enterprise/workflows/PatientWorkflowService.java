package com.healthcare.hospital.enterprise.workflows;

import com.healthcare.hospital.entity.Patient;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** Patient registration, identity hygiene, panel search and demographic workflow helpers. */
@Service
public class PatientWorkflowService {
    public List<Patient> search(Collection<Patient> patients, String query) {
        String q=WorkflowAnalytics.text(query).toLowerCase(Locale.ROOT);
        return WorkflowAnalytics.safe(patients).stream().filter(p ->
            q.isEmpty() || WorkflowAnalytics.contains(p.getPatientId(),q) ||
            WorkflowAnalytics.contains(p.getFullName(),q) || WorkflowAnalytics.contains(p.getPhone(),q) ||
            WorkflowAnalytics.contains(p.getEmail(),q)).sorted(Comparator.comparing(Patient::getId,Comparator.nullsLast(Long::compareTo)).reversed()).toList();
    }
    public List<Patient> byGender(Collection<Patient> patients,String gender) {
        return WorkflowAnalytics.safe(patients).stream().filter(p -> WorkflowAnalytics.key(p.getGender()).equals(WorkflowAnalytics.key(gender))).toList();
    }
    public List<Patient> byBloodGroup(Collection<Patient> patients,String bloodGroup) {
        return WorkflowAnalytics.safe(patients).stream().filter(p -> WorkflowAnalytics.key(p.getBloodGroup()).equals(WorkflowAnalytics.key(bloodGroup))).toList();
    }
    public Map<String,Long> genderDistribution(Collection<Patient> patients) {
        return WorkflowAnalytics.count(WorkflowAnalytics.safe(patients),Patient::getGender);
    }
    public Map<String,Long> bloodGroupDistribution(Collection<Patient> patients) {
        return WorkflowAnalytics.count(WorkflowAnalytics.safe(patients),Patient::getBloodGroup);
    }
    public List<String> validateIdentity(Patient patient) {
        if(patient==null)return List.of("Patient is required");
        List<String> errors=new ArrayList<>();
        if(WorkflowAnalytics.text(patient.getPatientId()).isEmpty())errors.add("Patient identifier is missing");
        if(WorkflowAnalytics.text(patient.getFullName()).length()<2)errors.add("Patient name is incomplete");
        if(patient.getPhone()!=null && !patient.getPhone().isBlank() && !patient.getPhone().matches("[+0-9() .-]{7,20}"))errors.add("Phone format is invalid");
        if(patient.getEmail()!=null && !patient.getEmail().isBlank() && !patient.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))errors.add("Email format is invalid");
        return List.copyOf(errors);
    }
    public List<String> duplicateIdentifiers(Collection<Patient> patients) {
        Map<String,Long> counts=WorkflowAnalytics.safe(patients).stream().map(Patient::getPatientId).filter(Objects::nonNull)
            .collect(Collectors.groupingBy(s->s.trim().toLowerCase(Locale.ROOT),Collectors.counting()));
        return counts.entrySet().stream().filter(e->e.getValue()>1).map(Map.Entry::getKey).sorted().toList();
    }
    public Map<String,Object> profileCompleteness(Patient p) {
        if(p==null)throw new IllegalArgumentException("Patient is required");
        Map<String,Boolean> fields=new LinkedHashMap<>();
        fields.put("patientId",present(p.getPatientId())); fields.put("name",present(p.getFullName()));
        fields.put("email",present(p.getEmail())); fields.put("phone",present(p.getPhone()));
        fields.put("gender",present(p.getGender())); fields.put("bloodGroup",present(p.getBloodGroup()));
        fields.put("address",present(p.getAddress())); fields.put("emergencyContact",present(p.getEmergencyContactPhone()));
        long complete=fields.values().stream().filter(Boolean::booleanValue).count();
        return Map.of("fields",Map.copyOf(fields),"completed",complete,"total",fields.size(),"percentage",Math.round(complete*100.0/fields.size()));
    }
    private boolean present(String s){return s!=null&&!s.isBlank();}
    public WorkflowSnapshot snapshot(Collection<Patient> patients) {
        List<Patient> list=WorkflowAnalytics.safe(patients);
        long incomplete=list.stream().filter(p->validateIdentity(p).size()>0).count();
        return WorkflowAnalytics.snapshot("PATIENT",list.stream().map(Patient::getGender).toList(),list.size(),list.size(),incomplete,
            incomplete==0?List.of():List.of("Some patient profiles require identity review"));
    }
    public List<Patient> recentlyRegistered(Collection<Patient> patients,int limit) {
        return WorkflowAnalytics.safe(patients).stream().sorted(Comparator.comparing(Patient::getRegisteredAt,Comparator.nullsLast(Comparator.reverseOrder())))
            .limit(Math.max(0,Math.min(limit,500))).toList();
    }
    public Map<String,Long> registrationByMonth(Collection<Patient> patients) {
        Map<String,Long> result=new TreeMap<>();
        WorkflowAnalytics.safe(patients).forEach(p->{if(p.getRegisteredAt()!=null)result.merge(p.getRegisteredAt().toLocalDate().withDayOfMonth(1).toString(),1L,Long::sum);});
        return Map.copyOf(result);
    }
    public List<Patient> missingEmergencyContact(Collection<Patient> patients) {
        return WorkflowAnalytics.safe(patients).stream().filter(p->!present(p.getEmergencyContactPhone())||!present(p.getEmergencyContactName())).toList();
    }
    public List<Patient> missingContactDetails(Collection<Patient> patients) {
        return WorkflowAnalytics.safe(patients).stream().filter(p->!present(p.getPhone())&&!present(p.getEmail())).toList();
    }
    public Optional<Patient> findByIdentifier(Collection<Patient> patients,String id) {
        if(id==null)return Optional.empty();
        return WorkflowAnalytics.safe(patients).stream().filter(p->id.trim().equalsIgnoreCase(WorkflowAnalytics.text(p.getPatientId()))).findFirst();
    }
    public List<Patient> filter(Collection<Patient> patients,String gender,String bloodGroup,String query) {
        return search(patients,query).stream().filter(p->gender==null||gender.isBlank()||WorkflowAnalytics.key(gender).equals(WorkflowAnalytics.key(p.getGender())))
            .filter(p->bloodGroup==null||bloodGroup.isBlank()||WorkflowAnalytics.key(bloodGroup).equals(WorkflowAnalytics.key(p.getBloodGroup()))).toList();
    }
    public Map<String,Object> cohortSummary(Collection<Patient> patients) {
        List<Patient> list=WorkflowAnalytics.safe(patients);
        return Map.of("total",list.size(),"gender",genderDistribution(list),"bloodGroups",bloodGroupDistribution(list),
            "missingEmergencyContacts",missingEmergencyContact(list).size(),"incompleteIdentity",list.stream().filter(p->!validateIdentity(p).isEmpty()).count());
    }
}
