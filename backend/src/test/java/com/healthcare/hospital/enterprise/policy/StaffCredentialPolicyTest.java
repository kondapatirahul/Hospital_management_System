package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StaffCredentialPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", StaffCredentialPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", StaffCredentialPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", StaffCredentialPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(StaffCredentialPolicy.canTransition("NEW", "OPEN"));
        assertTrue(StaffCredentialPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(StaffCredentialPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", StaffCredentialPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> StaffCredentialPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> StaffCredentialPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", StaffCredentialPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> StaffCredentialPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> StaffCredentialPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, StaffCredentialPolicy.pageSize(0));
        assertEquals(200, StaffCredentialPolicy.pageSize(999));
        assertEquals(0, StaffCredentialPolicy.pageIndex(-8));
        assertEquals("alpha", StaffCredentialPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> StaffCredentialPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), StaffCredentialPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), StaffCredentialPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(StaffCredentialPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(StaffCredentialPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(StaffCredentialPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, StaffCredentialPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(StaffCredentialPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(StaffCredentialPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), StaffCredentialPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(StaffCredentialPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = StaffCredentialPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("StaffCredential", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", StaffCredentialPolicy.safeActor(" "));
        assertEquals("AB***YZ", StaffCredentialPolicy.redactForLog("AB12345YZ"));
        assertEquals("StaffCredential", StaffCredentialPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", StaffCredentialPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", StaffCredentialPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("not-a-priority"));
        assertTrue(StaffCredentialPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(StaffCredentialPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(StaffCredentialPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(StaffCredentialPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(StaffCredentialPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", StaffCredentialPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", StaffCredentialPolicy.truncate("longer text", 5));
        assertTrue(StaffCredentialPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(StaffCredentialPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(StaffCredentialPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), StaffCredentialPolicy.terminalStatuses());
        assertEquals("STAFFCREDENTIAL:REF-1:7", StaffCredentialPolicy.workflowKey("REF-1", 7L));
    }
}
