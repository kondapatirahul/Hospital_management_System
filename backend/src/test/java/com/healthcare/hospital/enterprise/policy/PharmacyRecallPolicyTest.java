package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PharmacyRecallPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", PharmacyRecallPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", PharmacyRecallPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", PharmacyRecallPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(PharmacyRecallPolicy.canTransition("NEW", "OPEN"));
        assertTrue(PharmacyRecallPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(PharmacyRecallPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", PharmacyRecallPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> PharmacyRecallPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> PharmacyRecallPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", PharmacyRecallPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> PharmacyRecallPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> PharmacyRecallPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, PharmacyRecallPolicy.pageSize(0));
        assertEquals(200, PharmacyRecallPolicy.pageSize(999));
        assertEquals(0, PharmacyRecallPolicy.pageIndex(-8));
        assertEquals("alpha", PharmacyRecallPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> PharmacyRecallPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), PharmacyRecallPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), PharmacyRecallPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(PharmacyRecallPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(PharmacyRecallPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(PharmacyRecallPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, PharmacyRecallPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(PharmacyRecallPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(PharmacyRecallPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), PharmacyRecallPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(PharmacyRecallPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = PharmacyRecallPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("PharmacyRecall", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", PharmacyRecallPolicy.safeActor(" "));
        assertEquals("AB***YZ", PharmacyRecallPolicy.redactForLog("AB12345YZ"));
        assertEquals("PharmacyRecall", PharmacyRecallPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", PharmacyRecallPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", PharmacyRecallPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("not-a-priority"));
        assertTrue(PharmacyRecallPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(PharmacyRecallPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(PharmacyRecallPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(PharmacyRecallPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(PharmacyRecallPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", PharmacyRecallPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", PharmacyRecallPolicy.truncate("longer text", 5));
        assertTrue(PharmacyRecallPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(PharmacyRecallPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(PharmacyRecallPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), PharmacyRecallPolicy.terminalStatuses());
        assertEquals("PHARMACYRECALL:REF-1:7", PharmacyRecallPolicy.workflowKey("REF-1", 7L));
    }
}
