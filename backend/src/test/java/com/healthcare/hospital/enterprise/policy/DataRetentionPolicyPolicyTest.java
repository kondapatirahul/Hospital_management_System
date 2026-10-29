package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DataRetentionPolicyPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", DataRetentionPolicyPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", DataRetentionPolicyPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", DataRetentionPolicyPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(DataRetentionPolicyPolicy.canTransition("NEW", "OPEN"));
        assertTrue(DataRetentionPolicyPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(DataRetentionPolicyPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", DataRetentionPolicyPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> DataRetentionPolicyPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> DataRetentionPolicyPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", DataRetentionPolicyPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> DataRetentionPolicyPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> DataRetentionPolicyPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, DataRetentionPolicyPolicy.pageSize(0));
        assertEquals(200, DataRetentionPolicyPolicy.pageSize(999));
        assertEquals(0, DataRetentionPolicyPolicy.pageIndex(-8));
        assertEquals("alpha", DataRetentionPolicyPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> DataRetentionPolicyPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), DataRetentionPolicyPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), DataRetentionPolicyPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(DataRetentionPolicyPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(DataRetentionPolicyPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(DataRetentionPolicyPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, DataRetentionPolicyPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(DataRetentionPolicyPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(DataRetentionPolicyPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), DataRetentionPolicyPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(DataRetentionPolicyPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = DataRetentionPolicyPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("DataRetentionPolicy", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", DataRetentionPolicyPolicy.safeActor(" "));
        assertEquals("AB***YZ", DataRetentionPolicyPolicy.redactForLog("AB12345YZ"));
        assertEquals("DataRetentionPolicy", DataRetentionPolicyPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", DataRetentionPolicyPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", DataRetentionPolicyPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("not-a-priority"));
        assertTrue(DataRetentionPolicyPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(DataRetentionPolicyPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(DataRetentionPolicyPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(DataRetentionPolicyPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(DataRetentionPolicyPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", DataRetentionPolicyPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", DataRetentionPolicyPolicy.truncate("longer text", 5));
        assertTrue(DataRetentionPolicyPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(DataRetentionPolicyPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(DataRetentionPolicyPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), DataRetentionPolicyPolicy.terminalStatuses());
        assertEquals("DATARETENTIONPOLICY:REF-1:7", DataRetentionPolicyPolicy.workflowKey("REF-1", 7L));
    }
}
