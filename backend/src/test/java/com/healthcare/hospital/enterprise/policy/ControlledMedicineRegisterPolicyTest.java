package com.healthcare.hospital.enterprise.policy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ControlledMedicineRegisterPolicyTest {
    @Test void normalizesMissingAndMixedStatuses() {
        assertEquals("NEW", ControlledMedicineRegisterPolicy.normalizeStatus(null));
        assertEquals("IN_PROGRESS", ControlledMedicineRegisterPolicy.normalizeStatus(" in progress "));
        assertEquals("ON_HOLD", ControlledMedicineRegisterPolicy.normalizeStatus("on-hold"));
    }
    @Test void acceptsOnlyDeclaredTransitions() {
        assertTrue(ControlledMedicineRegisterPolicy.canTransition("NEW", "OPEN"));
        assertTrue(ControlledMedicineRegisterPolicy.canTransition("IN_PROGRESS", "COMPLETED"));
        assertFalse(ControlledMedicineRegisterPolicy.canTransition("ARCHIVED", "NEW"));
        assertEquals("COMPLETED", ControlledMedicineRegisterPolicy.requireTransition("in progress", "completed"));
    }
    @Test void rejectsInvalidTransition() {
        assertThrows(IllegalArgumentException.class, () -> ControlledMedicineRegisterPolicy.requireTransition("ARCHIVED", "IN_PROGRESS"));
        assertThrows(IllegalArgumentException.class, () -> ControlledMedicineRegisterPolicy.validateStatus("MAGIC"));
    }
    @Test void validatesReferencesAndRequiredText() {
        assertEquals("LAB/2026-0001", ControlledMedicineRegisterPolicy.validateReference(" LAB/2026-0001 "));
        assertThrows(IllegalArgumentException.class, () -> ControlledMedicineRegisterPolicy.validateReference("bad reference"));
        assertThrows(IllegalArgumentException.class, () -> ControlledMedicineRegisterPolicy.requireText(" ", "title", 100));
    }
    @Test void clampsPagingAndCleansText() {
        assertEquals(25, ControlledMedicineRegisterPolicy.pageSize(0));
        assertEquals(200, ControlledMedicineRegisterPolicy.pageSize(999));
        assertEquals(0, ControlledMedicineRegisterPolicy.pageIndex(-8));
        assertEquals("alpha", ControlledMedicineRegisterPolicy.cleanText(" alpha ", "title", 100));
        assertThrows(IllegalArgumentException.class, () -> ControlledMedicineRegisterPolicy.cleanText("12345", "title", 4));
    }
    @Test void validatesMoneyAndCalculatesTotals() {
        assertEquals(new BigDecimal("12.50"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("12.5"), "amount"));
        assertEquals(new BigDecimal("2.00"), ControlledMedicineRegisterPolicy.calculateTax(new BigDecimal("20"), new BigDecimal("10")));
        assertEquals(new BigDecimal("21.00"), ControlledMedicineRegisterPolicy.calculateTotal(new BigDecimal("20"), new BigDecimal("2"), new BigDecimal("1")));
        assertThrows(IllegalArgumentException.class, () -> ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("-1"), "amount"));
    }
    @Test void evaluatesDueDatesAndAge() {
        LocalDate today = LocalDate.of(2026, 10, 28);
        assertTrue(ControlledMedicineRegisterPolicy.isOverdue(today.minusDays(1), "IN_PROGRESS", today));
        assertFalse(ControlledMedicineRegisterPolicy.isOverdue(today.minusDays(1), "COMPLETED", today));
        assertTrue(ControlledMedicineRegisterPolicy.isDueSoon(today.plusDays(2), today, 3));
        assertEquals(24, ControlledMedicineRegisterPolicy.ageInHours(Instant.parse("2026-10-27T00:00:00Z"), Instant.parse("2026-10-28T00:00:00Z")));
    }
    @Test void searchesAndDeduplicatesCaseInsensitively() {
        assertTrue(ControlledMedicineRegisterPolicy.matchesSearch(List.of("Patient Record", "Ward"), "patient"));
        assertFalse(ControlledMedicineRegisterPolicy.matchesSearch(List.of("Ward"), "patient"));
        assertEquals(List.of("Alpha", "beta"), ControlledMedicineRegisterPolicy.distinctSorted(List.of("beta", "Alpha", "alpha", null)));
        assertTrue(ControlledMedicineRegisterPolicy.isReferenceDuplicate(List.of("ABC-1"), "abc-1"));
    }
    @Test void createsUsefulSummariesAndAuditMetadata() {
        Map<String, Object> summary = ControlledMedicineRegisterPolicy.workflowSummary(List.of("NEW", "IN_PROGRESS", "COMPLETED"), List.of(LocalDate.now().minusDays(1)), LocalDate.now());
        assertEquals("ControlledMedicineRegister", summary.get("workflow"));
        assertEquals(3, summary.get("total"));
        assertEquals("system", ControlledMedicineRegisterPolicy.safeActor(" "));
        assertEquals("AB***YZ", ControlledMedicineRegisterPolicy.redactForLog("AB12345YZ"));
        assertEquals("ControlledMedicineRegister", ControlledMedicineRegisterPolicy.auditSnapshot("nurse", "OPEN", Instant.EPOCH).get("workflow"));
    }
    @Test void handlesLabelsPrioritiesAndPermissions() {
        assertEquals("In progress", ControlledMedicineRegisterPolicy.summaryLabel("IN_PROGRESS"));
        assertEquals("CRITICAL", ControlledMedicineRegisterPolicy.normalizePriority("critical"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("not-a-priority"));
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("URGENT", "LOW") < 0);
        assertFalse(ControlledMedicineRegisterPolicy.canEdit("UNDER_REVIEW", false));
        assertTrue(ControlledMedicineRegisterPolicy.canEdit("UNDER_REVIEW", true));
    }
    @Test void handlesDateRangesAndTextUtilities() {
        assertTrue(ControlledMedicineRegisterPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(ControlledMedicineRegisterPolicy.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals("hello world", ControlledMedicineRegisterPolicy.normalizeSearchTerm(" HELLO   WORLD "));
        assertEquals("long…", ControlledMedicineRegisterPolicy.truncate("longer text", 5));
        assertTrue(ControlledMedicineRegisterPolicy.shouldNotify("OPEN", "COMPLETED"));
        assertFalse(ControlledMedicineRegisterPolicy.shouldNotify("OPEN", "open"));
    }
    @Test void reportsValidationErrorsWithoutThrowing() {
        assertTrue(ControlledMedicineRegisterPolicy.validationMessages("?", "", "UNKNOWN").size() >= 2);
        assertEquals(Set.of("COMPLETED", "CANCELLED", "REJECTED", "ARCHIVED"), ControlledMedicineRegisterPolicy.terminalStatuses());
        assertEquals("CONTROLLEDMEDICINEREGISTER:REF-1:7", ControlledMedicineRegisterPolicy.workflowKey("REF-1", 7L));
    }
}
