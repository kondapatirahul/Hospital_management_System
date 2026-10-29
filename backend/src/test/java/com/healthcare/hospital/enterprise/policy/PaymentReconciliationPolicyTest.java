package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PaymentReconciliationPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", PaymentReconciliationPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", PaymentReconciliationPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", PaymentReconciliationPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(PaymentReconciliationPolicy.canTransition("NEW", "OPEN"));
        assertTrue(PaymentReconciliationPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(PaymentReconciliationPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", PaymentReconciliationPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> PaymentReconciliationPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> PaymentReconciliationPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", PaymentReconciliationPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> PaymentReconciliationPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> PaymentReconciliationPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, PaymentReconciliationPolicy.pageSize(0));
        assertEquals(200, PaymentReconciliationPolicy.pageSize(999));
        assertEquals(0, PaymentReconciliationPolicy.pageIndex(-8));
        assertEquals("alpha", PaymentReconciliationPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> PaymentReconciliationPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), PaymentReconciliationPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), PaymentReconciliationPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(PaymentReconciliationPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(PaymentReconciliationPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(PaymentReconciliationPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, PaymentReconciliationPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(PaymentReconciliationPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(PaymentReconciliationPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), PaymentReconciliationPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(PaymentReconciliationPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = PaymentReconciliationPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("PaymentReconciliation", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", PaymentReconciliationPolicy.safeActor(" "));
        assertEquals("AB***YZ", PaymentReconciliationPolicy.redactForLog("AB12345YZ"));
        assertEquals("PaymentReconciliation", PaymentReconciliationPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", PaymentReconciliationPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", PaymentReconciliationPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("not-a-priority"));
        assertTrue(PaymentReconciliationPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(PaymentReconciliationPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(PaymentReconciliationPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(PaymentReconciliationPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(PaymentReconciliationPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", PaymentReconciliationPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", PaymentReconciliationPolicy.truncate("longer text", 5));
        assertTrue(PaymentReconciliationPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(PaymentReconciliationPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(PaymentReconciliationPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), PaymentReconciliationPolicy.terminalStatuses());
        assertEquals("PAYMENTRECONCILIATION:REF-1:7", PaymentReconciliationPolicy.workflowKey("REF-1", 7L));
    }
}
