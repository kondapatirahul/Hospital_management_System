package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PharmacyStocktakePolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", PharmacyStocktakePolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", PharmacyStocktakePolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", PharmacyStocktakePolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(PharmacyStocktakePolicy.canTransition("NEW", "OPEN"));
        assertTrue(PharmacyStocktakePolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(PharmacyStocktakePolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", PharmacyStocktakePolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> PharmacyStocktakePolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> PharmacyStocktakePolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", PharmacyStocktakePolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> PharmacyStocktakePolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> PharmacyStocktakePolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, PharmacyStocktakePolicy.pageSize(0));
        assertEquals(200, PharmacyStocktakePolicy.pageSize(999));
        assertEquals(0, PharmacyStocktakePolicy.pageIndex(-8));
        assertEquals("alpha", PharmacyStocktakePolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> PharmacyStocktakePolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), PharmacyStocktakePolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), PharmacyStocktakePolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> PharmacyStocktakePolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(PharmacyStocktakePolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(PharmacyStocktakePolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(PharmacyStocktakePolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, PharmacyStocktakePolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(PharmacyStocktakePolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(PharmacyStocktakePolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), PharmacyStocktakePolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(PharmacyStocktakePolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = PharmacyStocktakePolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("PharmacyStocktake", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", PharmacyStocktakePolicy.safeActor(" "));
        assertEquals("AB***YZ", PharmacyStocktakePolicy.redactForLog("AB12345YZ"));
        assertEquals("PharmacyStocktake", PharmacyStocktakePolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", PharmacyStocktakePolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", PharmacyStocktakePolicy.normalizePriority("critical"));
        assertEquals("NORMAL", PharmacyStocktakePolicy.normalizePriority("not-a-priority"));
        assertTrue(PharmacyStocktakePolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(PharmacyStocktakePolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(PharmacyStocktakePolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(PharmacyStocktakePolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(PharmacyStocktakePolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", PharmacyStocktakePolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", PharmacyStocktakePolicy.truncate("longer text", 5));
        assertTrue(PharmacyStocktakePolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(PharmacyStocktakePolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(PharmacyStocktakePolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), PharmacyStocktakePolicy.terminalStatuses());
        assertEquals("PHARMACYSTOCKTAKE:REF-1:7", PharmacyStocktakePolicy.workflowKey("REF-1", 7L));
    }
}
