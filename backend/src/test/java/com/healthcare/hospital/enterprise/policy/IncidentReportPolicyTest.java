package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class IncidentReportPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", IncidentReportPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", IncidentReportPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", IncidentReportPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(IncidentReportPolicy.canTransition("NEW", "OPEN"));
        assertTrue(IncidentReportPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(IncidentReportPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", IncidentReportPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> IncidentReportPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> IncidentReportPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", IncidentReportPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> IncidentReportPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> IncidentReportPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, IncidentReportPolicy.pageSize(0));
        assertEquals(200, IncidentReportPolicy.pageSize(999));
        assertEquals(0, IncidentReportPolicy.pageIndex(-8));
        assertEquals("alpha", IncidentReportPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> IncidentReportPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), IncidentReportPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), IncidentReportPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> IncidentReportPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(IncidentReportPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(IncidentReportPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(IncidentReportPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, IncidentReportPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(IncidentReportPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(IncidentReportPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), IncidentReportPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(IncidentReportPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = IncidentReportPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("IncidentReport", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", IncidentReportPolicy.safeActor(" "));
        assertEquals("AB***YZ", IncidentReportPolicy.redactForLog("AB12345YZ"));
        assertEquals("IncidentReport", IncidentReportPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", IncidentReportPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", IncidentReportPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("not-a-priority"));
        assertTrue(IncidentReportPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(IncidentReportPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(IncidentReportPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(IncidentReportPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(IncidentReportPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", IncidentReportPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", IncidentReportPolicy.truncate("longer text", 5));
        assertTrue(IncidentReportPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(IncidentReportPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(IncidentReportPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), IncidentReportPolicy.terminalStatuses());
        assertEquals("INCIDENTREPORT:REF-1:7", IncidentReportPolicy.workflowKey("REF-1", 7L));
    }
}
