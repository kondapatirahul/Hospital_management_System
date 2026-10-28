package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ReferralPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", ReferralPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", ReferralPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", ReferralPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(ReferralPolicy.canTransition("NEW", "OPEN"));
        assertTrue(ReferralPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(ReferralPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", ReferralPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> ReferralPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> ReferralPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", ReferralPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> ReferralPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> ReferralPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, ReferralPolicy.pageSize(0));
        assertEquals(200, ReferralPolicy.pageSize(999));
        assertEquals(0, ReferralPolicy.pageIndex(-8));
        assertEquals("alpha", ReferralPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> ReferralPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), ReferralPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), ReferralPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), ReferralPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> ReferralPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(ReferralPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(ReferralPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(ReferralPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, ReferralPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(ReferralPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(ReferralPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), ReferralPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(ReferralPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = ReferralPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("Referral", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", ReferralPolicy.safeActor(" "));
        assertEquals("AB***YZ", ReferralPolicy.redactForLog("AB12345YZ"));
        assertEquals("Referral", ReferralPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", ReferralPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", ReferralPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("not-a-priority"));
        assertTrue(ReferralPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(ReferralPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(ReferralPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(ReferralPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(ReferralPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", ReferralPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", ReferralPolicy.truncate("longer text", 5));
        assertTrue(ReferralPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(ReferralPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(ReferralPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), ReferralPolicy.terminalStatuses());
        assertEquals("REFERRAL:REF-1:7", ReferralPolicy.workflowKey("REF-1", 7L));
    }
}
