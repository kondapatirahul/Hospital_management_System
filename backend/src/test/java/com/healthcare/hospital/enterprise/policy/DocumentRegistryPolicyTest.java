package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DocumentRegistryPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", DocumentRegistryPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", DocumentRegistryPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", DocumentRegistryPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(DocumentRegistryPolicy.canTransition("NEW", "OPEN"));
        assertTrue(DocumentRegistryPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(DocumentRegistryPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", DocumentRegistryPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> DocumentRegistryPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> DocumentRegistryPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", DocumentRegistryPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> DocumentRegistryPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> DocumentRegistryPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, DocumentRegistryPolicy.pageSize(0));
        assertEquals(200, DocumentRegistryPolicy.pageSize(999));
        assertEquals(0, DocumentRegistryPolicy.pageIndex(-8));
        assertEquals("alpha", DocumentRegistryPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> DocumentRegistryPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), DocumentRegistryPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), DocumentRegistryPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(DocumentRegistryPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(DocumentRegistryPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(DocumentRegistryPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, DocumentRegistryPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(DocumentRegistryPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(DocumentRegistryPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), DocumentRegistryPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(DocumentRegistryPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = DocumentRegistryPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("DocumentRegistry", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", DocumentRegistryPolicy.safeActor(" "));
        assertEquals("AB***YZ", DocumentRegistryPolicy.redactForLog("AB12345YZ"));
        assertEquals("DocumentRegistry", DocumentRegistryPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", DocumentRegistryPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", DocumentRegistryPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("not-a-priority"));
        assertTrue(DocumentRegistryPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(DocumentRegistryPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(DocumentRegistryPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(DocumentRegistryPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(DocumentRegistryPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", DocumentRegistryPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", DocumentRegistryPolicy.truncate("longer text", 5));
        assertTrue(DocumentRegistryPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(DocumentRegistryPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(DocumentRegistryPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), DocumentRegistryPolicy.terminalStatuses());
        assertEquals("DOCUMENTREGISTRY:REF-1:7", DocumentRegistryPolicy.workflowKey("REF-1", 7L));
    }
}
