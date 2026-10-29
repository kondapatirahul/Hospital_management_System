package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WardRoundPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", WardRoundPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", WardRoundPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", WardRoundPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(WardRoundPolicy.canTransition("NEW", "OPEN"));
        assertTrue(WardRoundPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(WardRoundPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", WardRoundPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> WardRoundPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> WardRoundPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", WardRoundPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> WardRoundPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> WardRoundPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, WardRoundPolicy.pageSize(0));
        assertEquals(200, WardRoundPolicy.pageSize(999));
        assertEquals(0, WardRoundPolicy.pageIndex(-8));
        assertEquals("alpha", WardRoundPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> WardRoundPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), WardRoundPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), WardRoundPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> WardRoundPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(WardRoundPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(WardRoundPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(WardRoundPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, WardRoundPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(WardRoundPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(WardRoundPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), WardRoundPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(WardRoundPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = WardRoundPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("WardRound", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", WardRoundPolicy.safeActor(" "));
        assertEquals("AB***YZ", WardRoundPolicy.redactForLog("AB12345YZ"));
        assertEquals("WardRound", WardRoundPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", WardRoundPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", WardRoundPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("not-a-priority"));
        assertTrue(WardRoundPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(WardRoundPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(WardRoundPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(WardRoundPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(WardRoundPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", WardRoundPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", WardRoundPolicy.truncate("longer text", 5));
        assertTrue(WardRoundPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(WardRoundPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(WardRoundPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), WardRoundPolicy.terminalStatuses());
        assertEquals("WARDROUND:REF-1:7", WardRoundPolicy.workflowKey("REF-1", 7L));
    }
}
