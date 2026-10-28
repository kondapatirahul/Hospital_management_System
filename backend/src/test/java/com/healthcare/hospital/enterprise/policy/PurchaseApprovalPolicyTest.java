package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PurchaseApprovalPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", PurchaseApprovalPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", PurchaseApprovalPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", PurchaseApprovalPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(PurchaseApprovalPolicy.canTransition("NEW", "OPEN"));
        assertTrue(PurchaseApprovalPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(PurchaseApprovalPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", PurchaseApprovalPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> PurchaseApprovalPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> PurchaseApprovalPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", PurchaseApprovalPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> PurchaseApprovalPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> PurchaseApprovalPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, PurchaseApprovalPolicy.pageSize(0));
        assertEquals(200, PurchaseApprovalPolicy.pageSize(999));
        assertEquals(0, PurchaseApprovalPolicy.pageIndex(-8));
        assertEquals("alpha", PurchaseApprovalPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> PurchaseApprovalPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), PurchaseApprovalPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), PurchaseApprovalPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> PurchaseApprovalPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(PurchaseApprovalPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(PurchaseApprovalPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(PurchaseApprovalPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, PurchaseApprovalPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(PurchaseApprovalPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(PurchaseApprovalPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), PurchaseApprovalPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(PurchaseApprovalPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = PurchaseApprovalPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("PurchaseApproval", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", PurchaseApprovalPolicy.safeActor(" "));
        assertEquals("AB***YZ", PurchaseApprovalPolicy.redactForLog("AB12345YZ"));
        assertEquals("PurchaseApproval", PurchaseApprovalPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", PurchaseApprovalPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", PurchaseApprovalPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", PurchaseApprovalPolicy.normalizePriority("not-a-priority"));
        assertTrue(PurchaseApprovalPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(PurchaseApprovalPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(PurchaseApprovalPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(PurchaseApprovalPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(PurchaseApprovalPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", PurchaseApprovalPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", PurchaseApprovalPolicy.truncate("longer text", 5));
        assertTrue(PurchaseApprovalPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(PurchaseApprovalPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(PurchaseApprovalPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), PurchaseApprovalPolicy.terminalStatuses());
        assertEquals("PURCHASEAPPROVAL:REF-1:7", PurchaseApprovalPolicy.workflowKey("REF-1", 7L));
    }
}
