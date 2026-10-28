package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServiceRequestPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", ServiceRequestPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", ServiceRequestPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", ServiceRequestPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(ServiceRequestPolicy.canTransition("NEW", "OPEN"));
        assertTrue(ServiceRequestPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(ServiceRequestPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", ServiceRequestPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> ServiceRequestPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> ServiceRequestPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", ServiceRequestPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> ServiceRequestPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> ServiceRequestPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, ServiceRequestPolicy.pageSize(0));
        assertEquals(200, ServiceRequestPolicy.pageSize(999));
        assertEquals(0, ServiceRequestPolicy.pageIndex(-8));
        assertEquals("alpha", ServiceRequestPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> ServiceRequestPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), ServiceRequestPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), ServiceRequestPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(ServiceRequestPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(ServiceRequestPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(ServiceRequestPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, ServiceRequestPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(ServiceRequestPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(ServiceRequestPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), ServiceRequestPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(ServiceRequestPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = ServiceRequestPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("ServiceRequest", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", ServiceRequestPolicy.safeActor(" "));
        assertEquals("AB***YZ", ServiceRequestPolicy.redactForLog("AB12345YZ"));
        assertEquals("ServiceRequest", ServiceRequestPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", ServiceRequestPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", ServiceRequestPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("not-a-priority"));
        assertTrue(ServiceRequestPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(ServiceRequestPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(ServiceRequestPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(ServiceRequestPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(ServiceRequestPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", ServiceRequestPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", ServiceRequestPolicy.truncate("longer text", 5));
        assertTrue(ServiceRequestPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(ServiceRequestPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(ServiceRequestPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), ServiceRequestPolicy.terminalStatuses());
        assertEquals("SERVICEREQUEST:REF-1:7", ServiceRequestPolicy.workflowKey("REF-1", 7L));
    }
}
