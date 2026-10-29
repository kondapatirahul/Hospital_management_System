package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SupplierContractPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", SupplierContractPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", SupplierContractPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", SupplierContractPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(SupplierContractPolicy.canTransition("NEW", "OPEN"));
        assertTrue(SupplierContractPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(SupplierContractPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", SupplierContractPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> SupplierContractPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> SupplierContractPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", SupplierContractPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> SupplierContractPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> SupplierContractPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, SupplierContractPolicy.pageSize(0));
        assertEquals(200, SupplierContractPolicy.pageSize(999));
        assertEquals(0, SupplierContractPolicy.pageIndex(-8));
        assertEquals("alpha", SupplierContractPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> SupplierContractPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), SupplierContractPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), SupplierContractPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), SupplierContractPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> SupplierContractPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(SupplierContractPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(SupplierContractPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(SupplierContractPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, SupplierContractPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(SupplierContractPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(SupplierContractPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), SupplierContractPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(SupplierContractPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = SupplierContractPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("SupplierContract", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", SupplierContractPolicy.safeActor(" "));
        assertEquals("AB***YZ", SupplierContractPolicy.redactForLog("AB12345YZ"));
        assertEquals("SupplierContract", SupplierContractPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", SupplierContractPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", SupplierContractPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", SupplierContractPolicy.normalizePriority("not-a-priority"));
        assertTrue(SupplierContractPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(SupplierContractPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(SupplierContractPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(SupplierContractPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(SupplierContractPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", SupplierContractPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", SupplierContractPolicy.truncate("longer text", 5));
        assertTrue(SupplierContractPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(SupplierContractPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(SupplierContractPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), SupplierContractPolicy.terminalStatuses());
        assertEquals("SUPPLIERCONTRACT:REF-1:7", SupplierContractPolicy.workflowKey("REF-1", 7L));
    }
}
