package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.WardRoundPolicy;

class WardRoundPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", WardRoundPolicy.normalizeStatus(null));
        assertEquals("NEW", WardRoundPolicy.normalizeStatus(""));
        assertEquals("NEW", WardRoundPolicy.normalizeStatus(" "));
        assertEquals("NEW", WardRoundPolicy.normalizeStatus("new"));
        assertEquals("NEW", WardRoundPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", WardRoundPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", WardRoundPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", WardRoundPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", WardRoundPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", WardRoundPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", WardRoundPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", WardRoundPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", WardRoundPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", WardRoundPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", WardRoundPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", WardRoundPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", WardRoundPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", WardRoundPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", WardRoundPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", WardRoundPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", WardRoundPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", WardRoundPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", WardRoundPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", WardRoundPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", WardRoundPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, WardRoundPolicy.pageSize(-100));
        assertEquals(25, WardRoundPolicy.pageSize(-1));
        assertEquals(25, WardRoundPolicy.pageSize(0));
        assertEquals(1, WardRoundPolicy.pageSize(1));
        assertEquals(2, WardRoundPolicy.pageSize(2));
        assertEquals(5, WardRoundPolicy.pageSize(5));
        assertEquals(10, WardRoundPolicy.pageSize(10));
        assertEquals(20, WardRoundPolicy.pageSize(20));
        assertEquals(24, WardRoundPolicy.pageSize(24));
        assertEquals(25, WardRoundPolicy.pageSize(25));
        assertEquals(26, WardRoundPolicy.pageSize(26));
        assertEquals(49, WardRoundPolicy.pageSize(49));
        assertEquals(50, WardRoundPolicy.pageSize(50));
        assertEquals(75, WardRoundPolicy.pageSize(75));
        assertEquals(99, WardRoundPolicy.pageSize(99));
        assertEquals(100, WardRoundPolicy.pageSize(100));
        assertEquals(101, WardRoundPolicy.pageSize(101));
        assertEquals(150, WardRoundPolicy.pageSize(150));
        assertEquals(199, WardRoundPolicy.pageSize(199));
        assertEquals(200, WardRoundPolicy.pageSize(200));
        assertEquals(200, WardRoundPolicy.pageSize(201));
        assertEquals(200, WardRoundPolicy.pageSize(250));
        assertEquals(200, WardRoundPolicy.pageSize(500));
        assertEquals(200, WardRoundPolicy.pageSize(1000));
        assertEquals(200, WardRoundPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, WardRoundPolicy.pageIndex(-100));
        assertEquals(0, WardRoundPolicy.pageIndex(-10));
        assertEquals(0, WardRoundPolicy.pageIndex(-2));
        assertEquals(0, WardRoundPolicy.pageIndex(-1));
        assertEquals(0, WardRoundPolicy.pageIndex(0));
        assertEquals(1, WardRoundPolicy.pageIndex(1));
        assertEquals(2, WardRoundPolicy.pageIndex(2));
        assertEquals(3, WardRoundPolicy.pageIndex(3));
        assertEquals(4, WardRoundPolicy.pageIndex(4));
        assertEquals(5, WardRoundPolicy.pageIndex(5));
        assertEquals(6, WardRoundPolicy.pageIndex(6));
        assertEquals(7, WardRoundPolicy.pageIndex(7));
        assertEquals(8, WardRoundPolicy.pageIndex(8));
        assertEquals(9, WardRoundPolicy.pageIndex(9));
        assertEquals(10, WardRoundPolicy.pageIndex(10));
        assertEquals(11, WardRoundPolicy.pageIndex(11));
        assertEquals(12, WardRoundPolicy.pageIndex(12));
        assertEquals(13, WardRoundPolicy.pageIndex(13));
        assertEquals(14, WardRoundPolicy.pageIndex(14));
        assertEquals(15, WardRoundPolicy.pageIndex(15));
        assertEquals(20, WardRoundPolicy.pageIndex(20));
        assertEquals(50, WardRoundPolicy.pageIndex(50));
        assertEquals(100, WardRoundPolicy.pageIndex(100));
        assertEquals(1000, WardRoundPolicy.pageIndex(1000));
        assertEquals(99999, WardRoundPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", WardRoundPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", WardRoundPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", WardRoundPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", WardRoundPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", WardRoundPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", WardRoundPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", WardRoundPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", WardRoundPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", WardRoundPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", WardRoundPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", WardRoundPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", WardRoundPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", WardRoundPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", WardRoundPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", WardRoundPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", WardRoundPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", WardRoundPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", WardRoundPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", WardRoundPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", WardRoundPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", WardRoundPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", WardRoundPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", WardRoundPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", WardRoundPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", WardRoundPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", WardRoundPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", WardRoundPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", WardRoundPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", WardRoundPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", WardRoundPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", WardRoundPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", WardRoundPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", WardRoundPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", WardRoundPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", WardRoundPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", WardRoundPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", WardRoundPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", WardRoundPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", WardRoundPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", WardRoundPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", WardRoundPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", WardRoundPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", WardRoundPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", WardRoundPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", WardRoundPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", WardRoundPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", WardRoundPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", WardRoundPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", WardRoundPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", WardRoundPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", WardRoundPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", WardRoundPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", WardRoundPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", WardRoundPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", WardRoundPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", WardRoundPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", WardRoundPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", WardRoundPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", WardRoundPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", WardRoundPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", WardRoundPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", WardRoundPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", WardRoundPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", WardRoundPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", WardRoundPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", WardRoundPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", WardRoundPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", WardRoundPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", WardRoundPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", WardRoundPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", WardRoundPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", WardRoundPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", WardRoundPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", WardRoundPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", WardRoundPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), WardRoundPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, WardRoundPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, WardRoundPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, WardRoundPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", WardRoundPolicy.normalizePriority("critical"));
        assertEquals("URGENT", WardRoundPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", WardRoundPolicy.normalizePriority("high"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("normal"));
        assertEquals("LOW", WardRoundPolicy.normalizePriority("low"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority(null));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority(""));
        assertEquals("CRITICAL", WardRoundPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", WardRoundPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", WardRoundPolicy.normalizePriority("low "));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", WardRoundPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("none"));
        assertEquals("NORMAL", WardRoundPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, WardRoundPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, WardRoundPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, WardRoundPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, WardRoundPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, WardRoundPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, WardRoundPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, WardRoundPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, WardRoundPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, WardRoundPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, WardRoundPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, WardRoundPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, WardRoundPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, WardRoundPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, WardRoundPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, WardRoundPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, WardRoundPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, WardRoundPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, WardRoundPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, WardRoundPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, WardRoundPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, WardRoundPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, WardRoundPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, WardRoundPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, WardRoundPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, WardRoundPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", WardRoundPolicy.redactForLog(""));
        assertEquals("****", WardRoundPolicy.redactForLog("a"));
        assertEquals("****", WardRoundPolicy.redactForLog("ab"));
        assertEquals("****", WardRoundPolicy.redactForLog("abc"));
        assertEquals("****", WardRoundPolicy.redactForLog("abcd"));
        assertEquals("ab***de", WardRoundPolicy.redactForLog("abcde"));
        assertEquals("pa***23", WardRoundPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", WardRoundPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", WardRoundPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", WardRoundPolicy.redactForLog("Rahul"));
        assertEquals("do***01", WardRoundPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", WardRoundPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", WardRoundPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", WardRoundPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", WardRoundPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", WardRoundPolicy.redactForLog("test-value"));
        assertEquals("se***ef", WardRoundPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", WardRoundPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", WardRoundPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", WardRoundPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", WardRoundPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", WardRoundPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", WardRoundPolicy.redactForLog("case-2026"));
        assertEquals("re***88", WardRoundPolicy.redactForLog("record-88"));
        assertEquals("ad***77", WardRoundPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", WardRoundPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", WardRoundPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", WardRoundPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", WardRoundPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", WardRoundPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", WardRoundPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", WardRoundPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", WardRoundPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", WardRoundPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", WardRoundPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", WardRoundPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", WardRoundPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", WardRoundPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", WardRoundPolicy.truncate("Access Review", 5));
        assertEquals("War…", WardRoundPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", WardRoundPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", WardRoundPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", WardRoundPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", WardRoundPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", WardRoundPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", WardRoundPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", WardRoundPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", WardRoundPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", WardRoundPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", WardRoundPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(WardRoundPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(WardRoundPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(WardRoundPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(WardRoundPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(WardRoundPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(WardRoundPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(WardRoundPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(WardRoundPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(WardRoundPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(WardRoundPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(WardRoundPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(WardRoundPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(WardRoundPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(WardRoundPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(WardRoundPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(WardRoundPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(WardRoundPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(WardRoundPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(WardRoundPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(WardRoundPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(WardRoundPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(WardRoundPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(WardRoundPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(WardRoundPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(WardRoundPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, WardRoundPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, WardRoundPolicy.requiresApproval(null, null));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, WardRoundPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, WardRoundPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
