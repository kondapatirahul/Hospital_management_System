package com.healthcare.hospital.modules.workflows;
import static org.junit.jupiter.api.Assertions.*;
import com.healthcare.hospital.modules.legacy.entity.LabSample;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
class LaboratoryWorkflowServiceTest {
    private final LaboratoryWorkflowService service=new LaboratoryWorkflowService();
    private LabSample sample(String code,String type,String status) {
        LabSample s=new LabSample(); s.setSampleCode(code); s.setSampleType(type); s.setStatus(status); s.setCreatedAt(LocalDateTime.now().minusHours(30)); return s;
    }
    @Test void pendingReceiptIncludesOrderedSamples() { assertEquals(1,service.pendingReceipt(List.of(sample("S1","BLOOD","ORDERED"))).size()); }
    @Test void rejectedSamplesAreSeparated() { assertEquals(1,service.rejected(List.of(sample("S1","BLOOD","REJECTED"))).size()); }
    @Test void agingSamplesExcludeFinalStates() { assertTrue(service.agingSamples(List.of(sample("S1","BLOOD","REJECTED")),LocalDateTime.now(),24).isEmpty()); }
    @Test void agingSamplesIncludeOldActiveSamples() { assertEquals(1,service.agingSamples(List.of(sample("S1","BLOOD","COLLECTED")),LocalDateTime.now(),24).size()); }
    @Test void duplicateSampleCodesAreCaseInsensitive() { assertEquals(List.of("s1"),service.duplicateSampleCodes(List.of(sample("S1","BLOOD","ORDERED"),sample("s1","URINE","ORDERED")))); }
    @Test void sampleCodeIsRequired() { assertTrue(service.validateSample(sample("","BLOOD","ORDERED")).contains("Sample code is required")); }
    @Test void rejectionRequiresReason() { assertTrue(service.validateSample(sample("S1","BLOOD","REJECTED")).contains("Rejection reason is required")); }
    @Test void sampleTypeCountsAreGrouped() { assertEquals(2L,service.sampleTypeCounts(List.of(sample("S1","BLOOD","ORDERED"),sample("S2","BLOOD","RECEIVED"))).get("BLOOD")); }
}
