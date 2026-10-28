package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PatientTransferPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", PatientTransferPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", PatientTransferPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", PatientTransferPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(PatientTransferPolicy.canTransition("NEW", "OPEN"));
        assertTrue(PatientTransferPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(PatientTransferPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", PatientTransferPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> PatientTransferPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> PatientTransferPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", PatientTransferPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> PatientTransferPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> PatientTransferPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, PatientTransferPolicy.pageSize(0));
        assertEquals(200, PatientTransferPolicy.pageSize(999));
        assertEquals(0, PatientTransferPolicy.pageIndex(-8));
        assertEquals("alpha", PatientTransferPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> PatientTransferPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), PatientTransferPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), PatientTransferPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> PatientTransferPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(PatientTransferPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(PatientTransferPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(PatientTransferPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, PatientTransferPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(PatientTransferPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(PatientTransferPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), PatientTransferPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(PatientTransferPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = PatientTransferPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("PatientTransfer", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", PatientTransferPolicy.safeActor(" "));
        assertEquals("AB***YZ", PatientTransferPolicy.redactForLog("AB12345YZ"));
        assertEquals("PatientTransfer", PatientTransferPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", PatientTransferPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", PatientTransferPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("not-a-priority"));
        assertTrue(PatientTransferPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(PatientTransferPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(PatientTransferPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(PatientTransferPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(PatientTransferPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", PatientTransferPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", PatientTransferPolicy.truncate("longer text", 5));
        assertTrue(PatientTransferPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(PatientTransferPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(PatientTransferPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), PatientTransferPolicy.terminalStatuses());
        assertEquals("PATIENTTRANSFER:REF-1:7", PatientTransferPolicy.workflowKey("REF-1", 7L));
    }
}
