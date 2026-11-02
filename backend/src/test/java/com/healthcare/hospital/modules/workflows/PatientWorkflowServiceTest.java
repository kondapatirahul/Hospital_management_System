package com.healthcare.hospital.modules.workflows;
import static org.junit.jupiter.api.Assertions.*;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import java.util.List;
import org.junit.jupiter.api.Test;
class PatientWorkflowServiceTest {
    private final PatientWorkflowService service=new PatientWorkflowService();
    private Patient patient(String id,String name,String phone,String email) {
        Patient p=new Patient(); p.setPatientId(id); p.setFullName(name); p.setPhone(phone); p.setEmail(email); p.setGender("FEMALE"); p.setBloodGroup("O+"); return p;
    }
    @Test void acceptsValidPatientIdentity() { assertTrue(service.validateIdentity(patient("P-1","Asha Rao","9876543210","asha@example.com")).isEmpty()); }
    @Test void rejectsMissingPatientIdentifier() { assertTrue(service.validateIdentity(patient("","Asha Rao","9876543210","asha@example.com")).contains("Patient identifier is missing")); }
    @Test void rejectsShortPatientName() { assertTrue(service.validateIdentity(patient("P-1","A","9876543210","asha@example.com")).contains("Patient name is incomplete")); }
    @Test void rejectsMalformedEmail() { assertTrue(service.validateIdentity(patient("P-1","Asha Rao","9876543210","bad-email")).contains("Email format is invalid")); }
    @Test void rejectsMalformedPhone() { assertTrue(service.validateIdentity(patient("P-1","Asha Rao","phone","asha@example.com")).contains("Phone format is invalid")); }
    @Test void searchesPatientIdentifierCaseInsensitively() { assertEquals(1,service.search(List.of(patient("P-1","Asha Rao","9","a@b.com")),"p-1").size()); }
    @Test void filtersByGender() { assertEquals(1,service.byGender(List.of(patient("P-1","Asha","9","a@b.com")),"female").size()); }
    @Test void filtersByBloodGroup() { assertEquals(1,service.byBloodGroup(List.of(patient("P-1","Asha","9","a@b.com")),"o+").size()); }
    @Test void findsDuplicateIdentifiersIgnoringCase() {
        assertEquals(List.of("p-1"),service.duplicateIdentifiers(List.of(patient("P-1","Asha","9","a@b.com"),patient("p-1","Riya","8","r@b.com"))));
    }
    @Test void nullPatientReturnsValidationMessage() { assertFalse(service.validateIdentity(null).isEmpty()); }
    @Test void profileCompletenessHasPercentage() {
        var result=service.profileCompleteness(patient("P-1","Asha","9","a@b.com"));
        assertEquals(75L,result.get("percentage"));
    }
}
