package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LabOrderPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", LabOrderPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", LabOrderPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", LabOrderPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(LabOrderPolicy.canTransition("NEW", "OPEN"));
        assertTrue(LabOrderPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(LabOrderPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", LabOrderPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> LabOrderPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> LabOrderPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", LabOrderPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> LabOrderPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> LabOrderPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, LabOrderPolicy.pageSize(0));
        assertEquals(200, LabOrderPolicy.pageSize(999));
        assertEquals(0, LabOrderPolicy.pageIndex(-8));
        assertEquals("alpha", LabOrderPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> LabOrderPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), LabOrderPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), LabOrderPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> LabOrderPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(LabOrderPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(LabOrderPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(LabOrderPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, LabOrderPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(LabOrderPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(LabOrderPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), LabOrderPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(LabOrderPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = LabOrderPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("LabOrder", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", LabOrderPolicy.safeActor(" "));
        assertEquals("AB***YZ", LabOrderPolicy.redactForLog("AB12345YZ"));
        assertEquals("LabOrder", LabOrderPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", LabOrderPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", LabOrderPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("not-a-priority"));
        assertTrue(LabOrderPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(LabOrderPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(LabOrderPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(LabOrderPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(LabOrderPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", LabOrderPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", LabOrderPolicy.truncate("longer text", 5));
        assertTrue(LabOrderPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(LabOrderPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(LabOrderPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), LabOrderPolicy.terminalStatuses());
        assertEquals("LABORDER:REF-1:7", LabOrderPolicy.workflowKey("REF-1", 7L));
    }
}
