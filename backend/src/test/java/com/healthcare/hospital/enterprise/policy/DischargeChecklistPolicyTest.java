package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DischargeChecklistPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", DischargeChecklistPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", DischargeChecklistPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", DischargeChecklistPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(DischargeChecklistPolicy.canTransition("NEW", "OPEN"));
        assertTrue(DischargeChecklistPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(DischargeChecklistPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", DischargeChecklistPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> DischargeChecklistPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> DischargeChecklistPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", DischargeChecklistPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> DischargeChecklistPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> DischargeChecklistPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, DischargeChecklistPolicy.pageSize(0));
        assertEquals(200, DischargeChecklistPolicy.pageSize(999));
        assertEquals(0, DischargeChecklistPolicy.pageIndex(-8));
        assertEquals("alpha", DischargeChecklistPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> DischargeChecklistPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), DischargeChecklistPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), DischargeChecklistPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(DischargeChecklistPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(DischargeChecklistPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(DischargeChecklistPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, DischargeChecklistPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(DischargeChecklistPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(DischargeChecklistPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), DischargeChecklistPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(DischargeChecklistPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = DischargeChecklistPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("DischargeChecklist", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", DischargeChecklistPolicy.safeActor(" "));
        assertEquals("AB***YZ", DischargeChecklistPolicy.redactForLog("AB12345YZ"));
        assertEquals("DischargeChecklist", DischargeChecklistPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", DischargeChecklistPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", DischargeChecklistPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("not-a-priority"));
        assertTrue(DischargeChecklistPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(DischargeChecklistPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(DischargeChecklistPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(DischargeChecklistPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(DischargeChecklistPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", DischargeChecklistPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", DischargeChecklistPolicy.truncate("longer text", 5));
        assertTrue(DischargeChecklistPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(DischargeChecklistPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(DischargeChecklistPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), DischargeChecklistPolicy.terminalStatuses());
        assertEquals("DISCHARGECHECKLIST:REF-1:7", DischargeChecklistPolicy.workflowKey("REF-1", 7L));
    }
}
