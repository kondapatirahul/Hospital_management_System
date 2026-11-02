package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.NursingObservationPolicy;

class NursingObservationPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", NursingObservationPolicy.normalizeStatus(null));
        assertEquals("NEW", NursingObservationPolicy.normalizeStatus(""));
        assertEquals("NEW", NursingObservationPolicy.normalizeStatus(" "));
        assertEquals("NEW", NursingObservationPolicy.normalizeStatus("new"));
        assertEquals("NEW", NursingObservationPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", NursingObservationPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", NursingObservationPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", NursingObservationPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", NursingObservationPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", NursingObservationPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", NursingObservationPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", NursingObservationPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", NursingObservationPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", NursingObservationPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", NursingObservationPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", NursingObservationPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", NursingObservationPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", NursingObservationPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", NursingObservationPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", NursingObservationPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", NursingObservationPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", NursingObservationPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", NursingObservationPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", NursingObservationPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", NursingObservationPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, NursingObservationPolicy.pageSize(-100));
        assertEquals(25, NursingObservationPolicy.pageSize(-1));
        assertEquals(25, NursingObservationPolicy.pageSize(0));
        assertEquals(1, NursingObservationPolicy.pageSize(1));
        assertEquals(2, NursingObservationPolicy.pageSize(2));
        assertEquals(5, NursingObservationPolicy.pageSize(5));
        assertEquals(10, NursingObservationPolicy.pageSize(10));
        assertEquals(20, NursingObservationPolicy.pageSize(20));
        assertEquals(24, NursingObservationPolicy.pageSize(24));
        assertEquals(25, NursingObservationPolicy.pageSize(25));
        assertEquals(26, NursingObservationPolicy.pageSize(26));
        assertEquals(49, NursingObservationPolicy.pageSize(49));
        assertEquals(50, NursingObservationPolicy.pageSize(50));
        assertEquals(75, NursingObservationPolicy.pageSize(75));
        assertEquals(99, NursingObservationPolicy.pageSize(99));
        assertEquals(100, NursingObservationPolicy.pageSize(100));
        assertEquals(101, NursingObservationPolicy.pageSize(101));
        assertEquals(150, NursingObservationPolicy.pageSize(150));
        assertEquals(199, NursingObservationPolicy.pageSize(199));
        assertEquals(200, NursingObservationPolicy.pageSize(200));
        assertEquals(200, NursingObservationPolicy.pageSize(201));
        assertEquals(200, NursingObservationPolicy.pageSize(250));
        assertEquals(200, NursingObservationPolicy.pageSize(500));
        assertEquals(200, NursingObservationPolicy.pageSize(1000));
        assertEquals(200, NursingObservationPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, NursingObservationPolicy.pageIndex(-100));
        assertEquals(0, NursingObservationPolicy.pageIndex(-10));
        assertEquals(0, NursingObservationPolicy.pageIndex(-2));
        assertEquals(0, NursingObservationPolicy.pageIndex(-1));
        assertEquals(0, NursingObservationPolicy.pageIndex(0));
        assertEquals(1, NursingObservationPolicy.pageIndex(1));
        assertEquals(2, NursingObservationPolicy.pageIndex(2));
        assertEquals(3, NursingObservationPolicy.pageIndex(3));
        assertEquals(4, NursingObservationPolicy.pageIndex(4));
        assertEquals(5, NursingObservationPolicy.pageIndex(5));
        assertEquals(6, NursingObservationPolicy.pageIndex(6));
        assertEquals(7, NursingObservationPolicy.pageIndex(7));
        assertEquals(8, NursingObservationPolicy.pageIndex(8));
        assertEquals(9, NursingObservationPolicy.pageIndex(9));
        assertEquals(10, NursingObservationPolicy.pageIndex(10));
        assertEquals(11, NursingObservationPolicy.pageIndex(11));
        assertEquals(12, NursingObservationPolicy.pageIndex(12));
        assertEquals(13, NursingObservationPolicy.pageIndex(13));
        assertEquals(14, NursingObservationPolicy.pageIndex(14));
        assertEquals(15, NursingObservationPolicy.pageIndex(15));
        assertEquals(20, NursingObservationPolicy.pageIndex(20));
        assertEquals(50, NursingObservationPolicy.pageIndex(50));
        assertEquals(100, NursingObservationPolicy.pageIndex(100));
        assertEquals(1000, NursingObservationPolicy.pageIndex(1000));
        assertEquals(99999, NursingObservationPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", NursingObservationPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", NursingObservationPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", NursingObservationPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", NursingObservationPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", NursingObservationPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", NursingObservationPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", NursingObservationPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", NursingObservationPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", NursingObservationPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", NursingObservationPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", NursingObservationPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", NursingObservationPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", NursingObservationPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", NursingObservationPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", NursingObservationPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", NursingObservationPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", NursingObservationPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", NursingObservationPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", NursingObservationPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", NursingObservationPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", NursingObservationPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", NursingObservationPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", NursingObservationPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", NursingObservationPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", NursingObservationPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", NursingObservationPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", NursingObservationPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", NursingObservationPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", NursingObservationPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", NursingObservationPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", NursingObservationPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", NursingObservationPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", NursingObservationPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", NursingObservationPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", NursingObservationPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", NursingObservationPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", NursingObservationPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", NursingObservationPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", NursingObservationPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", NursingObservationPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", NursingObservationPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", NursingObservationPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", NursingObservationPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", NursingObservationPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", NursingObservationPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", NursingObservationPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", NursingObservationPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", NursingObservationPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", NursingObservationPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", NursingObservationPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", NursingObservationPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", NursingObservationPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", NursingObservationPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", NursingObservationPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", NursingObservationPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", NursingObservationPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", NursingObservationPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", NursingObservationPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", NursingObservationPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", NursingObservationPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", NursingObservationPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", NursingObservationPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", NursingObservationPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", NursingObservationPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", NursingObservationPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", NursingObservationPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", NursingObservationPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", NursingObservationPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", NursingObservationPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", NursingObservationPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", NursingObservationPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", NursingObservationPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", NursingObservationPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", NursingObservationPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", NursingObservationPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), NursingObservationPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, NursingObservationPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, NursingObservationPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, NursingObservationPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", NursingObservationPolicy.normalizePriority("critical"));
        assertEquals("URGENT", NursingObservationPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", NursingObservationPolicy.normalizePriority("high"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("normal"));
        assertEquals("LOW", NursingObservationPolicy.normalizePriority("low"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority(null));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority(""));
        assertEquals("CRITICAL", NursingObservationPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", NursingObservationPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", NursingObservationPolicy.normalizePriority("low "));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", NursingObservationPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("none"));
        assertEquals("NORMAL", NursingObservationPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, NursingObservationPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", NursingObservationPolicy.redactForLog(""));
        assertEquals("****", NursingObservationPolicy.redactForLog("a"));
        assertEquals("****", NursingObservationPolicy.redactForLog("ab"));
        assertEquals("****", NursingObservationPolicy.redactForLog("abc"));
        assertEquals("****", NursingObservationPolicy.redactForLog("abcd"));
        assertEquals("ab***de", NursingObservationPolicy.redactForLog("abcde"));
        assertEquals("pa***23", NursingObservationPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", NursingObservationPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", NursingObservationPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", NursingObservationPolicy.redactForLog("Rahul"));
        assertEquals("do***01", NursingObservationPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", NursingObservationPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", NursingObservationPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", NursingObservationPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", NursingObservationPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", NursingObservationPolicy.redactForLog("test-value"));
        assertEquals("se***ef", NursingObservationPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", NursingObservationPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", NursingObservationPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", NursingObservationPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", NursingObservationPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", NursingObservationPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", NursingObservationPolicy.redactForLog("case-2026"));
        assertEquals("re***88", NursingObservationPolicy.redactForLog("record-88"));
        assertEquals("ad***77", NursingObservationPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", NursingObservationPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", NursingObservationPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", NursingObservationPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", NursingObservationPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", NursingObservationPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", NursingObservationPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", NursingObservationPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", NursingObservationPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", NursingObservationPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", NursingObservationPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", NursingObservationPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", NursingObservationPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", NursingObservationPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", NursingObservationPolicy.truncate("Access Review", 5));
        assertEquals("War…", NursingObservationPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", NursingObservationPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", NursingObservationPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", NursingObservationPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", NursingObservationPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", NursingObservationPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", NursingObservationPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", NursingObservationPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", NursingObservationPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", NursingObservationPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", NursingObservationPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(NursingObservationPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(NursingObservationPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(NursingObservationPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(NursingObservationPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(NursingObservationPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(NursingObservationPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(NursingObservationPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(NursingObservationPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(NursingObservationPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(NursingObservationPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(NursingObservationPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(NursingObservationPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(NursingObservationPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(NursingObservationPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(NursingObservationPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(NursingObservationPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(NursingObservationPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(NursingObservationPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, NursingObservationPolicy.requiresApproval(null, null));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, NursingObservationPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, NursingObservationPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
