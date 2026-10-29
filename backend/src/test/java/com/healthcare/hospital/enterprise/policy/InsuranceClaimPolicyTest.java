package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InsuranceClaimPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", InsuranceClaimPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", InsuranceClaimPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", InsuranceClaimPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(InsuranceClaimPolicy.canTransition("NEW", "OPEN"));
        assertTrue(InsuranceClaimPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(InsuranceClaimPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", InsuranceClaimPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> InsuranceClaimPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> InsuranceClaimPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", InsuranceClaimPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> InsuranceClaimPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> InsuranceClaimPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, InsuranceClaimPolicy.pageSize(0));
        assertEquals(200, InsuranceClaimPolicy.pageSize(999));
        assertEquals(0, InsuranceClaimPolicy.pageIndex(-8));
        assertEquals("alpha", InsuranceClaimPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> InsuranceClaimPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), InsuranceClaimPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), InsuranceClaimPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(InsuranceClaimPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(InsuranceClaimPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(InsuranceClaimPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, InsuranceClaimPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(InsuranceClaimPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(InsuranceClaimPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), InsuranceClaimPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(InsuranceClaimPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = InsuranceClaimPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("InsuranceClaim", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", InsuranceClaimPolicy.safeActor(" "));
        assertEquals("AB***YZ", InsuranceClaimPolicy.redactForLog("AB12345YZ"));
        assertEquals("InsuranceClaim", InsuranceClaimPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", InsuranceClaimPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", InsuranceClaimPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("not-a-priority"));
        assertTrue(InsuranceClaimPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(InsuranceClaimPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(InsuranceClaimPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(InsuranceClaimPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(InsuranceClaimPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", InsuranceClaimPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", InsuranceClaimPolicy.truncate("longer text", 5));
        assertTrue(InsuranceClaimPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(InsuranceClaimPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(InsuranceClaimPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), InsuranceClaimPolicy.terminalStatuses());
        assertEquals("INSURANCECLAIM:REF-1:7", InsuranceClaimPolicy.workflowKey("REF-1", 7L));
    }
}
