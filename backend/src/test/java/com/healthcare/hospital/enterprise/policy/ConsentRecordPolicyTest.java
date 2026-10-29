package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConsentRecordPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", ConsentRecordPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", ConsentRecordPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", ConsentRecordPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(ConsentRecordPolicy.canTransition("NEW", "OPEN"));
        assertTrue(ConsentRecordPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(ConsentRecordPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", ConsentRecordPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> ConsentRecordPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> ConsentRecordPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", ConsentRecordPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> ConsentRecordPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> ConsentRecordPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, ConsentRecordPolicy.pageSize(0));
        assertEquals(200, ConsentRecordPolicy.pageSize(999));
        assertEquals(0, ConsentRecordPolicy.pageIndex(-8));
        assertEquals("alpha", ConsentRecordPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> ConsentRecordPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), ConsentRecordPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), ConsentRecordPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(ConsentRecordPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(ConsentRecordPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(ConsentRecordPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, ConsentRecordPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(ConsentRecordPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(ConsentRecordPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), ConsentRecordPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(ConsentRecordPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = ConsentRecordPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("ConsentRecord", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", ConsentRecordPolicy.safeActor(" "));
        assertEquals("AB***YZ", ConsentRecordPolicy.redactForLog("AB12345YZ"));
        assertEquals("ConsentRecord", ConsentRecordPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", ConsentRecordPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", ConsentRecordPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("not-a-priority"));
        assertTrue(ConsentRecordPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(ConsentRecordPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(ConsentRecordPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(ConsentRecordPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(ConsentRecordPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", ConsentRecordPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", ConsentRecordPolicy.truncate("longer text", 5));
        assertTrue(ConsentRecordPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(ConsentRecordPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(ConsentRecordPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), ConsentRecordPolicy.terminalStatuses());
        assertEquals("CONSENTRECORD:REF-1:7", ConsentRecordPolicy.workflowKey("REF-1", 7L));
    }
}
