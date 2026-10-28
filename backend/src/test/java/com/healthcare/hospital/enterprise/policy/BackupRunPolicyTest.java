package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BackupRunPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", BackupRunPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", BackupRunPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", BackupRunPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(BackupRunPolicy.canTransition("NEW", "OPEN"));
        assertTrue(BackupRunPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(BackupRunPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", BackupRunPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> BackupRunPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> BackupRunPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", BackupRunPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> BackupRunPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> BackupRunPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, BackupRunPolicy.pageSize(0));
        assertEquals(200, BackupRunPolicy.pageSize(999));
        assertEquals(0, BackupRunPolicy.pageIndex(-8));
        assertEquals("alpha", BackupRunPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> BackupRunPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), BackupRunPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), BackupRunPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> BackupRunPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(BackupRunPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(BackupRunPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(BackupRunPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, BackupRunPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(BackupRunPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(BackupRunPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), BackupRunPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(BackupRunPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = BackupRunPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("BackupRun", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", BackupRunPolicy.safeActor(" "));
        assertEquals("AB***YZ", BackupRunPolicy.redactForLog("AB12345YZ"));
        assertEquals("BackupRun", BackupRunPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", BackupRunPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", BackupRunPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("not-a-priority"));
        assertTrue(BackupRunPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(BackupRunPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(BackupRunPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(BackupRunPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(BackupRunPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", BackupRunPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", BackupRunPolicy.truncate("longer text", 5));
        assertTrue(BackupRunPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(BackupRunPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(BackupRunPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), BackupRunPolicy.terminalStatuses());
        assertEquals("BACKUPRUN:REF-1:7", BackupRunPolicy.workflowKey("REF-1", 7L));
    }
}
