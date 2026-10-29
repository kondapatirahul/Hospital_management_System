package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FinancialPeriodPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", FinancialPeriodPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", FinancialPeriodPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", FinancialPeriodPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(FinancialPeriodPolicy.canTransition("NEW", "OPEN"));
        assertTrue(FinancialPeriodPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(FinancialPeriodPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", FinancialPeriodPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> FinancialPeriodPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> FinancialPeriodPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", FinancialPeriodPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> FinancialPeriodPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> FinancialPeriodPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, FinancialPeriodPolicy.pageSize(0));
        assertEquals(200, FinancialPeriodPolicy.pageSize(999));
        assertEquals(0, FinancialPeriodPolicy.pageIndex(-8));
        assertEquals("alpha", FinancialPeriodPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> FinancialPeriodPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), FinancialPeriodPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), FinancialPeriodPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(FinancialPeriodPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(FinancialPeriodPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(FinancialPeriodPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, FinancialPeriodPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(FinancialPeriodPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(FinancialPeriodPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), FinancialPeriodPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(FinancialPeriodPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = FinancialPeriodPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("FinancialPeriod", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", FinancialPeriodPolicy.safeActor(" "));
        assertEquals("AB***YZ", FinancialPeriodPolicy.redactForLog("AB12345YZ"));
        assertEquals("FinancialPeriod", FinancialPeriodPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", FinancialPeriodPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", FinancialPeriodPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("not-a-priority"));
        assertTrue(FinancialPeriodPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(FinancialPeriodPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(FinancialPeriodPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(FinancialPeriodPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(FinancialPeriodPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", FinancialPeriodPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", FinancialPeriodPolicy.truncate("longer text", 5));
        assertTrue(FinancialPeriodPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(FinancialPeriodPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(FinancialPeriodPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), FinancialPeriodPolicy.terminalStatuses());
        assertEquals("FINANCIALPERIOD:REF-1:7", FinancialPeriodPolicy.workflowKey("REF-1", 7L));
    }
}
