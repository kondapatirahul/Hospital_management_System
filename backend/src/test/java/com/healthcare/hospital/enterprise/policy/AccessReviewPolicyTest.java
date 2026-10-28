package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AccessReviewPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", AccessReviewPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", AccessReviewPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", AccessReviewPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(AccessReviewPolicy.canTransition("NEW", "OPEN"));
        assertTrue(AccessReviewPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(AccessReviewPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", AccessReviewPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> AccessReviewPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> AccessReviewPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", AccessReviewPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> AccessReviewPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> AccessReviewPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, AccessReviewPolicy.pageSize(0));
        assertEquals(200, AccessReviewPolicy.pageSize(999));
        assertEquals(0, AccessReviewPolicy.pageIndex(-8));
        assertEquals("alpha", AccessReviewPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> AccessReviewPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), AccessReviewPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), AccessReviewPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> AccessReviewPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(AccessReviewPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(AccessReviewPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(AccessReviewPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, AccessReviewPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(AccessReviewPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(AccessReviewPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), AccessReviewPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(AccessReviewPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = AccessReviewPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("AccessReview", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", AccessReviewPolicy.safeActor(" "));
        assertEquals("AB***YZ", AccessReviewPolicy.redactForLog("AB12345YZ"));
        assertEquals("AccessReview", AccessReviewPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", AccessReviewPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", AccessReviewPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("not-a-priority"));
        assertTrue(AccessReviewPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(AccessReviewPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(AccessReviewPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(AccessReviewPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(AccessReviewPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", AccessReviewPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", AccessReviewPolicy.truncate("longer text", 5));
        assertTrue(AccessReviewPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(AccessReviewPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(AccessReviewPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), AccessReviewPolicy.terminalStatuses());
        assertEquals("ACCESSREVIEW:REF-1:7", AccessReviewPolicy.workflowKey("REF-1", 7L));
    }
}
