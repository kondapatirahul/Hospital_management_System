package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.ControlledMedicineRegisterPolicy;

class ControlledMedicineRegisterPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", ControlledMedicineRegisterPolicy.normalizeStatus(null));
        assertEquals("NEW", ControlledMedicineRegisterPolicy.normalizeStatus(""));
        assertEquals("NEW", ControlledMedicineRegisterPolicy.normalizeStatus(" "));
        assertEquals("NEW", ControlledMedicineRegisterPolicy.normalizeStatus("new"));
        assertEquals("NEW", ControlledMedicineRegisterPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", ControlledMedicineRegisterPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", ControlledMedicineRegisterPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", ControlledMedicineRegisterPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", ControlledMedicineRegisterPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", ControlledMedicineRegisterPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", ControlledMedicineRegisterPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", ControlledMedicineRegisterPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", ControlledMedicineRegisterPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", ControlledMedicineRegisterPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", ControlledMedicineRegisterPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", ControlledMedicineRegisterPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", ControlledMedicineRegisterPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", ControlledMedicineRegisterPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", ControlledMedicineRegisterPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", ControlledMedicineRegisterPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", ControlledMedicineRegisterPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", ControlledMedicineRegisterPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", ControlledMedicineRegisterPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", ControlledMedicineRegisterPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", ControlledMedicineRegisterPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, ControlledMedicineRegisterPolicy.pageSize(-100));
        assertEquals(25, ControlledMedicineRegisterPolicy.pageSize(-1));
        assertEquals(25, ControlledMedicineRegisterPolicy.pageSize(0));
        assertEquals(1, ControlledMedicineRegisterPolicy.pageSize(1));
        assertEquals(2, ControlledMedicineRegisterPolicy.pageSize(2));
        assertEquals(5, ControlledMedicineRegisterPolicy.pageSize(5));
        assertEquals(10, ControlledMedicineRegisterPolicy.pageSize(10));
        assertEquals(20, ControlledMedicineRegisterPolicy.pageSize(20));
        assertEquals(24, ControlledMedicineRegisterPolicy.pageSize(24));
        assertEquals(25, ControlledMedicineRegisterPolicy.pageSize(25));
        assertEquals(26, ControlledMedicineRegisterPolicy.pageSize(26));
        assertEquals(49, ControlledMedicineRegisterPolicy.pageSize(49));
        assertEquals(50, ControlledMedicineRegisterPolicy.pageSize(50));
        assertEquals(75, ControlledMedicineRegisterPolicy.pageSize(75));
        assertEquals(99, ControlledMedicineRegisterPolicy.pageSize(99));
        assertEquals(100, ControlledMedicineRegisterPolicy.pageSize(100));
        assertEquals(101, ControlledMedicineRegisterPolicy.pageSize(101));
        assertEquals(150, ControlledMedicineRegisterPolicy.pageSize(150));
        assertEquals(199, ControlledMedicineRegisterPolicy.pageSize(199));
        assertEquals(200, ControlledMedicineRegisterPolicy.pageSize(200));
        assertEquals(200, ControlledMedicineRegisterPolicy.pageSize(201));
        assertEquals(200, ControlledMedicineRegisterPolicy.pageSize(250));
        assertEquals(200, ControlledMedicineRegisterPolicy.pageSize(500));
        assertEquals(200, ControlledMedicineRegisterPolicy.pageSize(1000));
        assertEquals(200, ControlledMedicineRegisterPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, ControlledMedicineRegisterPolicy.pageIndex(-100));
        assertEquals(0, ControlledMedicineRegisterPolicy.pageIndex(-10));
        assertEquals(0, ControlledMedicineRegisterPolicy.pageIndex(-2));
        assertEquals(0, ControlledMedicineRegisterPolicy.pageIndex(-1));
        assertEquals(0, ControlledMedicineRegisterPolicy.pageIndex(0));
        assertEquals(1, ControlledMedicineRegisterPolicy.pageIndex(1));
        assertEquals(2, ControlledMedicineRegisterPolicy.pageIndex(2));
        assertEquals(3, ControlledMedicineRegisterPolicy.pageIndex(3));
        assertEquals(4, ControlledMedicineRegisterPolicy.pageIndex(4));
        assertEquals(5, ControlledMedicineRegisterPolicy.pageIndex(5));
        assertEquals(6, ControlledMedicineRegisterPolicy.pageIndex(6));
        assertEquals(7, ControlledMedicineRegisterPolicy.pageIndex(7));
        assertEquals(8, ControlledMedicineRegisterPolicy.pageIndex(8));
        assertEquals(9, ControlledMedicineRegisterPolicy.pageIndex(9));
        assertEquals(10, ControlledMedicineRegisterPolicy.pageIndex(10));
        assertEquals(11, ControlledMedicineRegisterPolicy.pageIndex(11));
        assertEquals(12, ControlledMedicineRegisterPolicy.pageIndex(12));
        assertEquals(13, ControlledMedicineRegisterPolicy.pageIndex(13));
        assertEquals(14, ControlledMedicineRegisterPolicy.pageIndex(14));
        assertEquals(15, ControlledMedicineRegisterPolicy.pageIndex(15));
        assertEquals(20, ControlledMedicineRegisterPolicy.pageIndex(20));
        assertEquals(50, ControlledMedicineRegisterPolicy.pageIndex(50));
        assertEquals(100, ControlledMedicineRegisterPolicy.pageIndex(100));
        assertEquals(1000, ControlledMedicineRegisterPolicy.pageIndex(1000));
        assertEquals(99999, ControlledMedicineRegisterPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", ControlledMedicineRegisterPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ControlledMedicineRegisterPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", ControlledMedicineRegisterPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", ControlledMedicineRegisterPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", ControlledMedicineRegisterPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", ControlledMedicineRegisterPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ControlledMedicineRegisterPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ControlledMedicineRegisterPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", ControlledMedicineRegisterPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", ControlledMedicineRegisterPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ControlledMedicineRegisterPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", ControlledMedicineRegisterPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", ControlledMedicineRegisterPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ControlledMedicineRegisterPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ControlledMedicineRegisterPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ControlledMedicineRegisterPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", ControlledMedicineRegisterPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ControlledMedicineRegisterPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", ControlledMedicineRegisterPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", ControlledMedicineRegisterPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", ControlledMedicineRegisterPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ControlledMedicineRegisterPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ControlledMedicineRegisterPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ControlledMedicineRegisterPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", ControlledMedicineRegisterPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", ControlledMedicineRegisterPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ControlledMedicineRegisterPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", ControlledMedicineRegisterPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", ControlledMedicineRegisterPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", ControlledMedicineRegisterPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", ControlledMedicineRegisterPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ControlledMedicineRegisterPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ControlledMedicineRegisterPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", ControlledMedicineRegisterPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", ControlledMedicineRegisterPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ControlledMedicineRegisterPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", ControlledMedicineRegisterPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", ControlledMedicineRegisterPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ControlledMedicineRegisterPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ControlledMedicineRegisterPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ControlledMedicineRegisterPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", ControlledMedicineRegisterPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ControlledMedicineRegisterPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", ControlledMedicineRegisterPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", ControlledMedicineRegisterPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", ControlledMedicineRegisterPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ControlledMedicineRegisterPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ControlledMedicineRegisterPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ControlledMedicineRegisterPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", ControlledMedicineRegisterPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", ControlledMedicineRegisterPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", ControlledMedicineRegisterPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", ControlledMedicineRegisterPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", ControlledMedicineRegisterPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", ControlledMedicineRegisterPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", ControlledMedicineRegisterPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", ControlledMedicineRegisterPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", ControlledMedicineRegisterPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", ControlledMedicineRegisterPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", ControlledMedicineRegisterPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", ControlledMedicineRegisterPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", ControlledMedicineRegisterPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", ControlledMedicineRegisterPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", ControlledMedicineRegisterPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", ControlledMedicineRegisterPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", ControlledMedicineRegisterPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", ControlledMedicineRegisterPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", ControlledMedicineRegisterPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", ControlledMedicineRegisterPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", ControlledMedicineRegisterPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", ControlledMedicineRegisterPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", ControlledMedicineRegisterPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", ControlledMedicineRegisterPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", ControlledMedicineRegisterPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", ControlledMedicineRegisterPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), ControlledMedicineRegisterPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, ControlledMedicineRegisterPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, ControlledMedicineRegisterPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", ControlledMedicineRegisterPolicy.normalizePriority("critical"));
        assertEquals("URGENT", ControlledMedicineRegisterPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", ControlledMedicineRegisterPolicy.normalizePriority("high"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("normal"));
        assertEquals("LOW", ControlledMedicineRegisterPolicy.normalizePriority("low"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority(null));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority(""));
        assertEquals("CRITICAL", ControlledMedicineRegisterPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", ControlledMedicineRegisterPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", ControlledMedicineRegisterPolicy.normalizePriority("low "));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", ControlledMedicineRegisterPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("none"));
        assertEquals("NORMAL", ControlledMedicineRegisterPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, ControlledMedicineRegisterPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", ControlledMedicineRegisterPolicy.redactForLog(""));
        assertEquals("****", ControlledMedicineRegisterPolicy.redactForLog("a"));
        assertEquals("****", ControlledMedicineRegisterPolicy.redactForLog("ab"));
        assertEquals("****", ControlledMedicineRegisterPolicy.redactForLog("abc"));
        assertEquals("****", ControlledMedicineRegisterPolicy.redactForLog("abcd"));
        assertEquals("ab***de", ControlledMedicineRegisterPolicy.redactForLog("abcde"));
        assertEquals("pa***23", ControlledMedicineRegisterPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", ControlledMedicineRegisterPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", ControlledMedicineRegisterPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", ControlledMedicineRegisterPolicy.redactForLog("Rahul"));
        assertEquals("do***01", ControlledMedicineRegisterPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", ControlledMedicineRegisterPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", ControlledMedicineRegisterPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", ControlledMedicineRegisterPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", ControlledMedicineRegisterPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", ControlledMedicineRegisterPolicy.redactForLog("test-value"));
        assertEquals("se***ef", ControlledMedicineRegisterPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", ControlledMedicineRegisterPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", ControlledMedicineRegisterPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", ControlledMedicineRegisterPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", ControlledMedicineRegisterPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", ControlledMedicineRegisterPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", ControlledMedicineRegisterPolicy.redactForLog("case-2026"));
        assertEquals("re***88", ControlledMedicineRegisterPolicy.redactForLog("record-88"));
        assertEquals("ad***77", ControlledMedicineRegisterPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", ControlledMedicineRegisterPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", ControlledMedicineRegisterPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", ControlledMedicineRegisterPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", ControlledMedicineRegisterPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", ControlledMedicineRegisterPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", ControlledMedicineRegisterPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", ControlledMedicineRegisterPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", ControlledMedicineRegisterPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", ControlledMedicineRegisterPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", ControlledMedicineRegisterPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", ControlledMedicineRegisterPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", ControlledMedicineRegisterPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", ControlledMedicineRegisterPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", ControlledMedicineRegisterPolicy.truncate("Access Review", 5));
        assertEquals("War…", ControlledMedicineRegisterPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", ControlledMedicineRegisterPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", ControlledMedicineRegisterPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", ControlledMedicineRegisterPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", ControlledMedicineRegisterPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", ControlledMedicineRegisterPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", ControlledMedicineRegisterPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", ControlledMedicineRegisterPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", ControlledMedicineRegisterPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", ControlledMedicineRegisterPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", ControlledMedicineRegisterPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(ControlledMedicineRegisterPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(null, null));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, ControlledMedicineRegisterPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
