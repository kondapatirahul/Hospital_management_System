package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RefundRequestPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", RefundRequestPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", RefundRequestPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", RefundRequestPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(RefundRequestPolicy.canTransition("NEW", "OPEN"));
        assertTrue(RefundRequestPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(RefundRequestPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", RefundRequestPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> RefundRequestPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> RefundRequestPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", RefundRequestPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> RefundRequestPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> RefundRequestPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, RefundRequestPolicy.pageSize(0));
        assertEquals(200, RefundRequestPolicy.pageSize(999));
        assertEquals(0, RefundRequestPolicy.pageIndex(-8));
        assertEquals("alpha", RefundRequestPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> RefundRequestPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), RefundRequestPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), RefundRequestPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> RefundRequestPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(RefundRequestPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(RefundRequestPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(RefundRequestPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, RefundRequestPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(RefundRequestPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(RefundRequestPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), RefundRequestPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(RefundRequestPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = RefundRequestPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("RefundRequest", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", RefundRequestPolicy.safeActor(" "));
        assertEquals("AB***YZ", RefundRequestPolicy.redactForLog("AB12345YZ"));
        assertEquals("RefundRequest", RefundRequestPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", RefundRequestPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", RefundRequestPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("not-a-priority"));
        assertTrue(RefundRequestPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(RefundRequestPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(RefundRequestPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(RefundRequestPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(RefundRequestPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", RefundRequestPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", RefundRequestPolicy.truncate("longer text", 5));
        assertTrue(RefundRequestPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(RefundRequestPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(RefundRequestPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), RefundRequestPolicy.terminalStatuses());
        assertEquals("REFUNDREQUEST:REF-1:7", RefundRequestPolicy.workflowKey("REF-1", 7L));
    }
}
