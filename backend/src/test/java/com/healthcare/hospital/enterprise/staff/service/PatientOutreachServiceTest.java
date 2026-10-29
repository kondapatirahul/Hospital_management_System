package com.healthcare.hospital.enterprise.staff.service;

import com.healthcare.hospital.enterprise.staff.entity.PatientOutreach;
import com.healthcare.hospital.enterprise.staff.repository.DischargeChecklistRepository;
import com.healthcare.hospital.enterprise.staff.repository.PatientOutreachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientOutreachServiceTest {
    private PatientOutreachRepository repository;
    private DischargeChecklistRepository checklists;
    private PatientOutreachService service;
    private PatientOutreach item;

    @BeforeEach
    void setUp() {
        repository = mock(PatientOutreachRepository.class);
        checklists = mock(DischargeChecklistRepository.class);
        service = new PatientOutreachService(repository, checklists);
        item = new PatientOutreach();
        item.setReferenceCode("OUT-20261029-0001");
        item.setPatientReference("PT-001");
        item.setPatientName("Example Patient");
        item.setPurpose("Post-discharge check-in");
        item.setCreatedBy("nurse@example.test");
        item.setAssignedTeam("Nursing");
        item.setConsentConfirmed(true);
        item.setStatus("OPEN");
        item.setOutcome("NOT_ATTEMPTED");
        item.setEscalationLevel("NONE");
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(repository.save(any(PatientOutreach.class))).thenAnswer(call -> call.getArgument(0));
    }

    @Test
    void redFlagRaisesEmergencyAndRequiresHandoff() {
        PatientOutreach saved = service.recordAttempt(1L, "REACHED", "Reached patient", "Breathlessness reported",
            null, null, null, null, null, "Follow local emergency procedure", null,
            true, false, false, false, false, true, false, false, false, false, "doctor@example.test");
        assertTrue(saved.isRedFlagSymptoms());
        assertEquals("EMERGENCY", saved.getEscalationLevel());
        assertEquals("URGENT", saved.getPriority());
        assertTrue(saved.isClinicianHandoffRequired());
        assertTrue(saved.isSupervisorReviewRequired());
        assertEquals("WAITING_FOR_CLINICIAN", saved.getStatus());
        assertEquals(1, saved.getAttemptCount());
    }

    @Test
    void activeRedFlagPreventsClosure() {
        item.setAttemptCount(1);
        item.setOutcome("REACHED");
        item.setRedFlagSymptoms(true);
        IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> service.close(1L, "Contact complete", "doctor@example.test"));
        assertTrue(error.getMessage().contains("red-flag"));
        verify(repository, never()).save(any(PatientOutreach.class));
    }

    @Test
    void safetyResolutionRequiresHandoffAcknowledgement() {
        item.setRedFlagSymptoms(true);
        item.setClinicianHandoffRequired(true);
        IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> service.resolveSafety(1L, "Reviewed", "doctor@example.test"));
        assertTrue(error.getMessage().contains("handoff"));
    }

    @Test
    void resolutionClearsActiveFlagsButKeepsAuditNote() {
        item.setRedFlagSymptoms(true);
        item.setBreathingConcern(true);
        item.setEscalationNotes("Initial escalation");
        PatientOutreach saved = service.resolveSafety(1L,
            "Clinician reviewed; approved follow-up pathway arranged.", "doctor@example.test");
        assertFalse(saved.isRedFlagSymptoms());
        assertFalse(saved.isBreathingConcern());
        assertEquals("ROUTINE", saved.getEscalationLevel());
        assertTrue(saved.getEscalationNotes().contains("safety review resolved by doctor@example.test"));
    }

    @Test
    void invalidOutcomeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.recordAttempt(1L, "INVALID", "Summary", null,
            null, null, null, null, null, null, null, true, false, false, false,
            false, false, false, false, false, false, "nurse@example.test"));
    }

    @Test
    void substantiveContactRequiresConsent() {
        item.setConsentConfirmed(false);
        IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> service.recordAttempt(1L, "REACHED", "Conversation", null, null, null, null,
                null, null, null, null, true, false, false, false, false,
                false, false, false, false, false, "nurse@example.test"));
        assertTrue(error.getMessage().contains("consent"));
    }
}
