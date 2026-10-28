package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LabReagentPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", LabReagentPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", LabReagentPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", LabReagentPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(LabReagentPolicy.canTransition("NEW", "OPEN"));
        assertTrue(LabReagentPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(LabReagentPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", LabReagentPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> LabReagentPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> LabReagentPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", LabReagentPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> LabReagentPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> LabReagentPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, LabReagentPolicy.pageSize(0));
        assertEquals(200, LabReagentPolicy.pageSize(999));
        assertEquals(0, LabReagentPolicy.pageIndex(-8));
        assertEquals("alpha", LabReagentPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> LabReagentPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), LabReagentPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), LabReagentPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> LabReagentPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(LabReagentPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(LabReagentPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(LabReagentPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, LabReagentPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(LabReagentPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(LabReagentPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), LabReagentPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(LabReagentPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = LabReagentPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("LabReagent", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", LabReagentPolicy.safeActor(" "));
        assertEquals("AB***YZ", LabReagentPolicy.redactForLog("AB12345YZ"));
        assertEquals("LabReagent", LabReagentPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", LabReagentPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", LabReagentPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("not-a-priority"));
        assertTrue(LabReagentPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(LabReagentPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(LabReagentPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(LabReagentPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(LabReagentPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", LabReagentPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", LabReagentPolicy.truncate("longer text", 5));
        assertTrue(LabReagentPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(LabReagentPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(LabReagentPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), LabReagentPolicy.terminalStatuses());
        assertEquals("LABREAGENT:REF-1:7", LabReagentPolicy.workflowKey("REF-1", 7L));
    }
}
