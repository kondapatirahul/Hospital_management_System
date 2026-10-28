package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LabInstrumentPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", LabInstrumentPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", LabInstrumentPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", LabInstrumentPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(LabInstrumentPolicy.canTransition("NEW", "OPEN"));
        assertTrue(LabInstrumentPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(LabInstrumentPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", LabInstrumentPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> LabInstrumentPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> LabInstrumentPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", LabInstrumentPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> LabInstrumentPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> LabInstrumentPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, LabInstrumentPolicy.pageSize(0));
        assertEquals(200, LabInstrumentPolicy.pageSize(999));
        assertEquals(0, LabInstrumentPolicy.pageIndex(-8));
        assertEquals("alpha", LabInstrumentPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> LabInstrumentPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), LabInstrumentPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), LabInstrumentPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(LabInstrumentPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(LabInstrumentPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(LabInstrumentPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, LabInstrumentPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(LabInstrumentPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(LabInstrumentPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), LabInstrumentPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(LabInstrumentPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = LabInstrumentPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("LabInstrument", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", LabInstrumentPolicy.safeActor(" "));
        assertEquals("AB***YZ", LabInstrumentPolicy.redactForLog("AB12345YZ"));
        assertEquals("LabInstrument", LabInstrumentPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", LabInstrumentPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", LabInstrumentPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("not-a-priority"));
        assertTrue(LabInstrumentPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(LabInstrumentPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(LabInstrumentPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(LabInstrumentPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(LabInstrumentPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", LabInstrumentPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", LabInstrumentPolicy.truncate("longer text", 5));
        assertTrue(LabInstrumentPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(LabInstrumentPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(LabInstrumentPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), LabInstrumentPolicy.terminalStatuses());
        assertEquals("LABINSTRUMENT:REF-1:7", LabInstrumentPolicy.workflowKey("REF-1", 7L));
    }
}
