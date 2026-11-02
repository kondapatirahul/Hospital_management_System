package com.healthcare.hospital.enterprise.workflows;

import com.healthcare.hospital.entity.Doctor;
import java.util.*;
import org.springframework.stereotype.Service;

/** Doctor directory, availability and assignment helpers using the existing Doctor entity. */
@Service
public class DoctorWorkflowService {
    public List<Doctor> search(Collection<Doctor> doctors,String query) {
        return WorkflowAnalytics.safe(doctors).stream().filter(d->WorkflowAnalytics.contains(d.getFullName(),query)||
            WorkflowAnalytics.contains(d.getQualification(),query)||WorkflowAnalytics.contains(departmentName(d),query)||
            WorkflowAnalytics.contains(d.getEmail(),query)).sorted(Comparator.comparing(Doctor::getFullName,Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))).toList();
    }
    public List<Doctor> byDepartment(Collection<Doctor> doctors,String department) {
        return WorkflowAnalytics.safe(doctors).stream().filter(d->WorkflowAnalytics.key(departmentName(d)).equals(WorkflowAnalytics.key(department))).toList();
    }
    public List<Doctor> availableToday(Collection<Doctor> doctors) {
        return WorkflowAnalytics.safe(doctors).stream().filter(Doctor::isAvailableToday).toList();
    }
    public Map<String,Long> departmentDistribution(Collection<Doctor> doctors) {
        return WorkflowAnalytics.count(WorkflowAnalytics.safe(doctors),DoctorWorkflowService::departmentName);
    }
    public List<String> validateDirectoryEntry(Doctor doctor) {
        if(doctor==null)return List.of("Doctor is required");
        List<String> errors=new ArrayList<>();
        if(doctor.getFullName()==null||doctor.getFullName().trim().length()<2)errors.add("Doctor name is required");
        if(doctor.getDepartment()==null||doctor.getDepartment().isBlank())errors.add("Department is required");
        if(doctor.getEmail()!=null&&!doctor.getEmail().isBlank()&&!doctor.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))errors.add("Email format is invalid");
        return List.copyOf(errors);
    }
    public List<Doctor> missingRoomAssignment(Collection<Doctor> doctors) {
        return WorkflowAnalytics.safe(doctors).stream().filter(d->d.getRoomNumber()==null||d.getRoomNumber().isBlank()).toList();
    }
    public List<Doctor> missingContact(Collection<Doctor> doctors) {
        return WorkflowAnalytics.safe(doctors).stream().filter(d->(d.getEmail()==null||d.getEmail().isBlank())&&(d.getPhone()==null||d.getPhone().isBlank())).toList();
    }
    public Map<String,Object> directorySummary(Collection<Doctor> doctors) {
        List<Doctor> list=WorkflowAnalytics.safe(doctors);
        long available=list.stream().filter(Doctor::isAvailableToday).count();
        return Map.of("total",list.size(),"availableToday",available,"unavailableToday",list.size()-available,
            "departments",departmentDistribution(list),"missingRooms",missingRoomAssignment(list).size());
    }
    public List<Doctor> byQualification(Collection<Doctor> doctors,String qualification) {
        return WorkflowAnalytics.safe(doctors).stream().filter(d->WorkflowAnalytics.contains(d.getQualification(),qualification)).toList();
    }
    public List<Doctor> byRoom(Collection<Doctor> doctors,String room) {
        return WorkflowAnalytics.safe(doctors).stream().filter(d->room!=null&&room.equalsIgnoreCase(WorkflowAnalytics.text(d.getRoomNumber()))).toList();
    }
    public WorkflowSnapshot snapshot(Collection<Doctor> doctors) {
        List<Doctor> list=WorkflowAnalytics.safe(doctors);
        long attention=missingRoomAssignment(list).size()+missingContact(list).size();
        return WorkflowAnalytics.snapshot("DOCTOR",list.stream().map(DoctorWorkflowService::departmentName).toList(),list.size(),
            list.stream().filter(Doctor::isAvailableToday).count(),attention,List.of());
    }
    private static String departmentName(Doctor doctor) {\n        return doctor.getDepartment() == null ? "" : doctor.getDepartment().getName();\n    }\n}
