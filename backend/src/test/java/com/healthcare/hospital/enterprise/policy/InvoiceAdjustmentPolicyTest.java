package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InvoiceAdjustmentPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", InvoiceAdjustmentPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", InvoiceAdjustmentPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", InvoiceAdjustmentPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(InvoiceAdjustmentPolicy.canTransition("NEW", "OPEN"));
        assertTrue(InvoiceAdjustmentPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(InvoiceAdjustmentPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", InvoiceAdjustmentPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> InvoiceAdjustmentPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> InvoiceAdjustmentPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", InvoiceAdjustmentPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> InvoiceAdjustmentPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> InvoiceAdjustmentPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, InvoiceAdjustmentPolicy.pageSize(0));
        assertEquals(200, InvoiceAdjustmentPolicy.pageSize(999));
        assertEquals(0, InvoiceAdjustmentPolicy.pageIndex(-8));
        assertEquals("alpha", InvoiceAdjustmentPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> InvoiceAdjustmentPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), InvoiceAdjustmentPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), InvoiceAdjustmentPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> InvoiceAdjustmentPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(InvoiceAdjustmentPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(InvoiceAdjustmentPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(InvoiceAdjustmentPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, InvoiceAdjustmentPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(InvoiceAdjustmentPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(InvoiceAdjustmentPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), InvoiceAdjustmentPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(InvoiceAdjustmentPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = InvoiceAdjustmentPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("InvoiceAdjustment", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", InvoiceAdjustmentPolicy.safeActor(" "));
        assertEquals("AB***YZ", InvoiceAdjustmentPolicy.redactForLog("AB12345YZ"));
        assertEquals("InvoiceAdjustment", InvoiceAdjustmentPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", InvoiceAdjustmentPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", InvoiceAdjustmentPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", InvoiceAdjustmentPolicy.normalizePriority("not-a-priority"));
        assertTrue(InvoiceAdjustmentPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(InvoiceAdjustmentPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(InvoiceAdjustmentPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(InvoiceAdjustmentPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(InvoiceAdjustmentPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", InvoiceAdjustmentPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", InvoiceAdjustmentPolicy.truncate("longer text", 5));
        assertTrue(InvoiceAdjustmentPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(InvoiceAdjustmentPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(InvoiceAdjustmentPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), InvoiceAdjustmentPolicy.terminalStatuses());
        assertEquals("INVOICEADJUSTMENT:REF-1:7", InvoiceAdjustmentPolicy.workflowKey("REF-1", 7L));
    }
}
