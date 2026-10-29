package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LabQualityControlPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", LabQualityControlPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", LabQualityControlPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", LabQualityControlPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(LabQualityControlPolicy.canTransition("NEW", "OPEN"));
        assertTrue(LabQualityControlPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(LabQualityControlPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", LabQualityControlPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> LabQualityControlPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> LabQualityControlPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", LabQualityControlPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> LabQualityControlPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> LabQualityControlPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, LabQualityControlPolicy.pageSize(0));
        assertEquals(200, LabQualityControlPolicy.pageSize(999));
        assertEquals(0, LabQualityControlPolicy.pageIndex(-8));
        assertEquals("alpha", LabQualityControlPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> LabQualityControlPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), LabQualityControlPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), LabQualityControlPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(LabQualityControlPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(LabQualityControlPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(LabQualityControlPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, LabQualityControlPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(LabQualityControlPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(LabQualityControlPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), LabQualityControlPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(LabQualityControlPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = LabQualityControlPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("LabQualityControl", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", LabQualityControlPolicy.safeActor(" "));
        assertEquals("AB***YZ", LabQualityControlPolicy.redactForLog("AB12345YZ"));
        assertEquals("LabQualityControl", LabQualityControlPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", LabQualityControlPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", LabQualityControlPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("not-a-priority"));
        assertTrue(LabQualityControlPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(LabQualityControlPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(LabQualityControlPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(LabQualityControlPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(LabQualityControlPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", LabQualityControlPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", LabQualityControlPolicy.truncate("longer text", 5));
        assertTrue(LabQualityControlPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(LabQualityControlPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(LabQualityControlPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), LabQualityControlPolicy.terminalStatuses());
        assertEquals("LABQUALITYCONTROL:REF-1:7", LabQualityControlPolicy.workflowKey("REF-1", 7L));
    }
}
