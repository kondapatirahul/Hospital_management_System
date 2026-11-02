package com.healthcare.hospital.modules.workflows;
import static org.junit.jupiter.api.Assertions.*;
import com.healthcare.hospital.modules.legacy.entity.Department;
import com.healthcare.hospital.modules.legacy.entity.Doctor;
import java.util.List;
import org.junit.jupiter.api.Test;
class DoctorWorkflowServiceTest {
    private final DoctorWorkflowService service=new DoctorWorkflowService();
    private Doctor doctor(String name,String department,boolean available) {
        Doctor d=new Doctor(); d.setFullName(name); d.setDepartment(new Department(department,"","")); d.setAvailableToday(available); d.setQualification("MBBS"); return d;
    }
    @Test void searchesDepartmentNameFromDepartmentEntity() {
        assertEquals(1,service.search(List.of(doctor("Asha","Cardiology",true)),"cardio").size());
    }
    @Test void filtersByDepartmentName() {
        assertEquals(1,service.byDepartment(List.of(doctor("Asha","Cardiology",true)),"CARDIOLOGY").size());
    }
    @Test void listsAvailableDoctors() {
        assertEquals(1,service.availableToday(List.of(doctor("Asha","Cardiology",true),doctor("Ravi","Surgery",false))).size());
    }
    @Test void validatesMissingDoctorName() {
        assertTrue(service.validateDirectoryEntry(doctor(" ","Cardiology",true)).contains("Doctor name is required"));
    }
    @Test void reportsMissingRoomAssignment() {
        assertEquals(1,service.missingRoomAssignment(List.of(doctor("Asha","Cardiology",true))).size());
    }
}
