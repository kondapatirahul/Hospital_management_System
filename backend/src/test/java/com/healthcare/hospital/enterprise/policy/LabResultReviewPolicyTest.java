package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LabResultReviewPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", LabResultReviewPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", LabResultReviewPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", LabResultReviewPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(LabResultReviewPolicy.canTransition("NEW", "OPEN"));
        assertTrue(LabResultReviewPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(LabResultReviewPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", LabResultReviewPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> LabResultReviewPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> LabResultReviewPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", LabResultReviewPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> LabResultReviewPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> LabResultReviewPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, LabResultReviewPolicy.pageSize(0));
        assertEquals(200, LabResultReviewPolicy.pageSize(999));
        assertEquals(0, LabResultReviewPolicy.pageIndex(-8));
        assertEquals("alpha", LabResultReviewPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> LabResultReviewPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), LabResultReviewPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), LabResultReviewPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(LabResultReviewPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(LabResultReviewPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(LabResultReviewPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, LabResultReviewPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(LabResultReviewPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(LabResultReviewPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), LabResultReviewPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(LabResultReviewPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = LabResultReviewPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("LabResultReview", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", LabResultReviewPolicy.safeActor(" "));
        assertEquals("AB***YZ", LabResultReviewPolicy.redactForLog("AB12345YZ"));
        assertEquals("LabResultReview", LabResultReviewPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", LabResultReviewPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", LabResultReviewPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("not-a-priority"));
        assertTrue(LabResultReviewPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(LabResultReviewPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(LabResultReviewPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(LabResultReviewPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(LabResultReviewPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", LabResultReviewPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", LabResultReviewPolicy.truncate("longer text", 5));
        assertTrue(LabResultReviewPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(LabResultReviewPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(LabResultReviewPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), LabResultReviewPolicy.terminalStatuses());
        assertEquals("LABRESULTREVIEW:REF-1:7", LabResultReviewPolicy.workflowKey("REF-1", 7L));
    }
}
