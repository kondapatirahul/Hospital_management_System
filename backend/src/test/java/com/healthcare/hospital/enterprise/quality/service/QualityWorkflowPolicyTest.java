package com.healthcare.hospital.enterprise.quality.service;

import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.entity.QualityAction;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/** Pure policy tests; no database or Spring context is required. */
class QualityWorkflowPolicyTest {
    private final QualityWorkflowPolicy policy=new QualityWorkflowPolicy();

    @Test
    void allowsOnlyDocumentedTransitionsFromDraft() {
        QualityReview review=new QualityReview();
        review.setStatus("DRAFT");
        assertDoesNotThrow(()->policy.requireTransition(review,"SUBMITTED"));
        assertDoesNotThrow(()->policy.requireTransition(review,"CANCELLED"));
        assertThrows(IllegalStateException.class,()->policy.requireTransition(review,"CLOSED"));
    }

    @Test
    void sentinelEventMustHavePatientSafetyImpact() {
        QualityReview review=new QualityReview();
        review.setTitle("Medication near miss");
        review.setEventDescription("A wrong dose was prepared but intercepted.");
        review.setCategory("MEDICATION");
        review.setSeverity("HIGH");
        review.setSentinelEvent(true);
        assertThrows(IllegalArgumentException.class,()->policy.validateCreate(review));
        review.setPatientSafetyImpact(true);
        assertDoesNotThrow(()->policy.validateCreate(review));
    }

    @Test
    void closureRequiresNoOpenActionsAndEvidence() {
        QualityReview review=new QualityReview();
        review.setRootCause("The label was not checked.");
        review.setCorrectivePlan("Add a second-person check.");
        review.setClosureSummary("Action verified and learning shared.");
        review.setEvidencePreserved(true);
        assertDoesNotThrow(()->policy.requireReadyForClosure(review,0));
        assertThrows(IllegalStateException.class,()->policy.requireReadyForClosure(review,1));
    }

    @Test
    void actionStateCannotJumpFromOpenToVerified() {
        QualityAction action=new QualityAction();
        action.setStatus("OPEN");
        assertDoesNotThrow(()->policy.requireActionTransition(action,"IN_PROGRESS"));
        assertThrows(IllegalStateException.class,()->policy.requireActionTransition(action,"VERIFIED"));
    }

    @Test
    void overdueUsesLocalCalendarDateAndExcludesClosedCases() {
        QualityReview review=new QualityReview();
        review.setDueDate(LocalDate.now().minusDays(1));
        review.setStatus("INVESTIGATING");
        assertTrue(policy.overdue(review));
        review.setStatus("CLOSED");
        assertFalse(policy.overdue(review));
    }

    @Test
    void validatesSupportedCategoryAndSeverity() {
        assertDoesNotThrow(()->policy.validateCategory("MEDICATION"));
        assertDoesNotThrow(()->policy.validateSeverity("CRITICAL"));
        assertThrows(IllegalArgumentException.class,()->policy.validateCategory("MADE_UP"));
        assertThrows(IllegalArgumentException.class,()->policy.validateSeverity("EXTREME"));
    }
}
