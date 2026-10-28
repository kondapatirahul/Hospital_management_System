package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CommunicationLogPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", CommunicationLogPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", CommunicationLogPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", CommunicationLogPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(CommunicationLogPolicy.canTransition("NEW", "OPEN"));
        assertTrue(CommunicationLogPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(CommunicationLogPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", CommunicationLogPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> CommunicationLogPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> CommunicationLogPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", CommunicationLogPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> CommunicationLogPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> CommunicationLogPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, CommunicationLogPolicy.pageSize(0));
        assertEquals(200, CommunicationLogPolicy.pageSize(999));
        assertEquals(0, CommunicationLogPolicy.pageIndex(-8));
        assertEquals("alpha", CommunicationLogPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> CommunicationLogPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), CommunicationLogPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), CommunicationLogPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(CommunicationLogPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(CommunicationLogPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(CommunicationLogPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, CommunicationLogPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(CommunicationLogPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(CommunicationLogPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), CommunicationLogPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(CommunicationLogPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = CommunicationLogPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("CommunicationLog", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", CommunicationLogPolicy.safeActor(" "));
        assertEquals("AB***YZ", CommunicationLogPolicy.redactForLog("AB12345YZ"));
        assertEquals("CommunicationLog", CommunicationLogPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", CommunicationLogPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", CommunicationLogPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("not-a-priority"));
        assertTrue(CommunicationLogPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(CommunicationLogPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(CommunicationLogPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(CommunicationLogPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(CommunicationLogPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", CommunicationLogPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", CommunicationLogPolicy.truncate("longer text", 5));
        assertTrue(CommunicationLogPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(CommunicationLogPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(CommunicationLogPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), CommunicationLogPolicy.terminalStatuses());
        assertEquals("COMMUNICATIONLOG:REF-1:7", CommunicationLogPolicy.workflowKey("REF-1", 7L));
    }
}
