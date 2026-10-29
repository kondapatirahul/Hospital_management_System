package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NursingObservationPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", NursingObservationPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", NursingObservationPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", NursingObservationPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(NursingObservationPolicy.canTransition("NEW", "OPEN"));
        assertTrue(NursingObservationPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(NursingObservationPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", NursingObservationPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> NursingObservationPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> NursingObservationPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", NursingObservationPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> NursingObservationPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> NursingObservationPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, NursingObservationPolicy.pageSize(0));
        assertEquals(200, NursingObservationPolicy.pageSize(999));
        assertEquals(0, NursingObservationPolicy.pageIndex(-8));
        assertEquals("alpha", NursingObservationPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> NursingObservationPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), NursingObservationPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), NursingObservationPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> NursingObservationPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(NursingObservationPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(NursingObservationPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(NursingObservationPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, NursingObservationPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(NursingObservationPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(NursingObservationPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), NursingObservationPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(NursingObservationPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = NursingObservationPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("NursingObservation", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", NursingObservationPolicy.safeActor(" "));
        assertEquals("AB***YZ", NursingObservationPolicy.redactForLog("AB12345YZ"));
        assertEquals("NursingObservation", NursingObservationPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", NursingObservationPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", NursingObservationPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("not-a-priority"));
        assertTrue(NursingObservationPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(NursingObservationPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(NursingObservationPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(NursingObservationPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(NursingObservationPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", NursingObservationPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", NursingObservationPolicy.truncate("longer text", 5));
        assertTrue(NursingObservationPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(NursingObservationPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(NursingObservationPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), NursingObservationPolicy.terminalStatuses());
        assertEquals("NURSINGOBSERVATION:REF-1:7", NursingObservationPolicy.workflowKey("REF-1", 7L));
    }
}
