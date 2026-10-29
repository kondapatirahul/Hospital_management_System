package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AppointmentWaitlistPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", AppointmentWaitlistPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", AppointmentWaitlistPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", AppointmentWaitlistPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(AppointmentWaitlistPolicy.canTransition("NEW", "OPEN"));
        assertTrue(AppointmentWaitlistPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(AppointmentWaitlistPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", AppointmentWaitlistPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentWaitlistPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> AppointmentWaitlistPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", AppointmentWaitlistPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> AppointmentWaitlistPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> AppointmentWaitlistPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, AppointmentWaitlistPolicy.pageSize(0));
        assertEquals(200, AppointmentWaitlistPolicy.pageSize(999));
        assertEquals(0, AppointmentWaitlistPolicy.pageIndex(-8));
        assertEquals("alpha", AppointmentWaitlistPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> AppointmentWaitlistPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), AppointmentWaitlistPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), AppointmentWaitlistPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> AppointmentWaitlistPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(AppointmentWaitlistPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(AppointmentWaitlistPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(AppointmentWaitlistPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, AppointmentWaitlistPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(AppointmentWaitlistPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(AppointmentWaitlistPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), AppointmentWaitlistPolicy.distinctSorted(java.util.Arrays.asList("beta", "Alpha", "alpha", null)));
        assertTrue(AppointmentWaitlistPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = AppointmentWaitlistPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("AppointmentWaitlist", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", AppointmentWaitlistPolicy.safeActor(" "));
        assertEquals("AB***YZ", AppointmentWaitlistPolicy.redactForLog("AB12345YZ"));
        assertEquals("AppointmentWaitlist", AppointmentWaitlistPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", AppointmentWaitlistPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", AppointmentWaitlistPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", AppointmentWaitlistPolicy.normalizePriority("not-a-priority"));
        assertTrue(AppointmentWaitlistPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(AppointmentWaitlistPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(AppointmentWaitlistPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(AppointmentWaitlistPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(AppointmentWaitlistPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", AppointmentWaitlistPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", AppointmentWaitlistPolicy.truncate("longer text", 5));
        assertTrue(AppointmentWaitlistPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(AppointmentWaitlistPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(AppointmentWaitlistPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), AppointmentWaitlistPolicy.terminalStatuses());
        assertEquals("APPOINTMENTWAITLIST:REF-1:7", AppointmentWaitlistPolicy.workflowKey("REF-1", 7L));
    }
}
