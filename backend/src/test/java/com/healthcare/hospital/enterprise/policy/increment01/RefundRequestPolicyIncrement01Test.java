package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.RefundRequestPolicy;

class RefundRequestPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", RefundRequestPolicy.normalizeStatus(null));
        assertEquals("NEW", RefundRequestPolicy.normalizeStatus(""));
        assertEquals("NEW", RefundRequestPolicy.normalizeStatus(" "));
        assertEquals("NEW", RefundRequestPolicy.normalizeStatus("new"));
        assertEquals("NEW", RefundRequestPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", RefundRequestPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", RefundRequestPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", RefundRequestPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", RefundRequestPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", RefundRequestPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", RefundRequestPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", RefundRequestPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", RefundRequestPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", RefundRequestPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", RefundRequestPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", RefundRequestPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", RefundRequestPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", RefundRequestPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", RefundRequestPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", RefundRequestPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", RefundRequestPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", RefundRequestPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", RefundRequestPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", RefundRequestPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", RefundRequestPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, RefundRequestPolicy.pageSize(-100));
        assertEquals(25, RefundRequestPolicy.pageSize(-1));
        assertEquals(25, RefundRequestPolicy.pageSize(0));
        assertEquals(1, RefundRequestPolicy.pageSize(1));
        assertEquals(2, RefundRequestPolicy.pageSize(2));
        assertEquals(5, RefundRequestPolicy.pageSize(5));
        assertEquals(10, RefundRequestPolicy.pageSize(10));
        assertEquals(20, RefundRequestPolicy.pageSize(20));
        assertEquals(24, RefundRequestPolicy.pageSize(24));
        assertEquals(25, RefundRequestPolicy.pageSize(25));
        assertEquals(26, RefundRequestPolicy.pageSize(26));
        assertEquals(49, RefundRequestPolicy.pageSize(49));
        assertEquals(50, RefundRequestPolicy.pageSize(50));
        assertEquals(75, RefundRequestPolicy.pageSize(75));
        assertEquals(99, RefundRequestPolicy.pageSize(99));
        assertEquals(100, RefundRequestPolicy.pageSize(100));
        assertEquals(101, RefundRequestPolicy.pageSize(101));
        assertEquals(150, RefundRequestPolicy.pageSize(150));
        assertEquals(199, RefundRequestPolicy.pageSize(199));
        assertEquals(200, RefundRequestPolicy.pageSize(200));
        assertEquals(200, RefundRequestPolicy.pageSize(201));
        assertEquals(200, RefundRequestPolicy.pageSize(250));
        assertEquals(200, RefundRequestPolicy.pageSize(500));
        assertEquals(200, RefundRequestPolicy.pageSize(1000));
        assertEquals(200, RefundRequestPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, RefundRequestPolicy.pageIndex(-100));
        assertEquals(0, RefundRequestPolicy.pageIndex(-10));
        assertEquals(0, RefundRequestPolicy.pageIndex(-2));
        assertEquals(0, RefundRequestPolicy.pageIndex(-1));
        assertEquals(0, RefundRequestPolicy.pageIndex(0));
        assertEquals(1, RefundRequestPolicy.pageIndex(1));
        assertEquals(2, RefundRequestPolicy.pageIndex(2));
        assertEquals(3, RefundRequestPolicy.pageIndex(3));
        assertEquals(4, RefundRequestPolicy.pageIndex(4));
        assertEquals(5, RefundRequestPolicy.pageIndex(5));
        assertEquals(6, RefundRequestPolicy.pageIndex(6));
        assertEquals(7, RefundRequestPolicy.pageIndex(7));
        assertEquals(8, RefundRequestPolicy.pageIndex(8));
        assertEquals(9, RefundRequestPolicy.pageIndex(9));
        assertEquals(10, RefundRequestPolicy.pageIndex(10));
        assertEquals(11, RefundRequestPolicy.pageIndex(11));
        assertEquals(12, RefundRequestPolicy.pageIndex(12));
        assertEquals(13, RefundRequestPolicy.pageIndex(13));
        assertEquals(14, RefundRequestPolicy.pageIndex(14));
        assertEquals(15, RefundRequestPolicy.pageIndex(15));
        assertEquals(20, RefundRequestPolicy.pageIndex(20));
        assertEquals(50, RefundRequestPolicy.pageIndex(50));
        assertEquals(100, RefundRequestPolicy.pageIndex(100));
        assertEquals(1000, RefundRequestPolicy.pageIndex(1000));
        assertEquals(99999, RefundRequestPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", RefundRequestPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", RefundRequestPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", RefundRequestPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", RefundRequestPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", RefundRequestPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", RefundRequestPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", RefundRequestPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", RefundRequestPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", RefundRequestPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", RefundRequestPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", RefundRequestPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", RefundRequestPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", RefundRequestPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", RefundRequestPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", RefundRequestPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", RefundRequestPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", RefundRequestPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", RefundRequestPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", RefundRequestPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", RefundRequestPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", RefundRequestPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", RefundRequestPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", RefundRequestPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", RefundRequestPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", RefundRequestPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", RefundRequestPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", RefundRequestPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", RefundRequestPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", RefundRequestPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", RefundRequestPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", RefundRequestPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", RefundRequestPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", RefundRequestPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", RefundRequestPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", RefundRequestPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", RefundRequestPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", RefundRequestPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", RefundRequestPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", RefundRequestPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", RefundRequestPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", RefundRequestPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", RefundRequestPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", RefundRequestPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", RefundRequestPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", RefundRequestPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", RefundRequestPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", RefundRequestPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", RefundRequestPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", RefundRequestPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", RefundRequestPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", RefundRequestPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", RefundRequestPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", RefundRequestPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", RefundRequestPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", RefundRequestPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", RefundRequestPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", RefundRequestPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", RefundRequestPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", RefundRequestPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", RefundRequestPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", RefundRequestPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", RefundRequestPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", RefundRequestPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", RefundRequestPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", RefundRequestPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", RefundRequestPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", RefundRequestPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", RefundRequestPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", RefundRequestPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", RefundRequestPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", RefundRequestPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", RefundRequestPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", RefundRequestPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", RefundRequestPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", RefundRequestPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), RefundRequestPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, RefundRequestPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, RefundRequestPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, RefundRequestPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", RefundRequestPolicy.normalizePriority("critical"));
        assertEquals("URGENT", RefundRequestPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", RefundRequestPolicy.normalizePriority("high"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("normal"));
        assertEquals("LOW", RefundRequestPolicy.normalizePriority("low"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority(null));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority(""));
        assertEquals("CRITICAL", RefundRequestPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", RefundRequestPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", RefundRequestPolicy.normalizePriority("low "));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", RefundRequestPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("none"));
        assertEquals("NORMAL", RefundRequestPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, RefundRequestPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", RefundRequestPolicy.redactForLog(""));
        assertEquals("****", RefundRequestPolicy.redactForLog("a"));
        assertEquals("****", RefundRequestPolicy.redactForLog("ab"));
        assertEquals("****", RefundRequestPolicy.redactForLog("abc"));
        assertEquals("****", RefundRequestPolicy.redactForLog("abcd"));
        assertEquals("ab***de", RefundRequestPolicy.redactForLog("abcde"));
        assertEquals("pa***23", RefundRequestPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", RefundRequestPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", RefundRequestPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", RefundRequestPolicy.redactForLog("Rahul"));
        assertEquals("do***01", RefundRequestPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", RefundRequestPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", RefundRequestPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", RefundRequestPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", RefundRequestPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", RefundRequestPolicy.redactForLog("test-value"));
        assertEquals("se***ef", RefundRequestPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", RefundRequestPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", RefundRequestPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", RefundRequestPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", RefundRequestPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", RefundRequestPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", RefundRequestPolicy.redactForLog("case-2026"));
        assertEquals("re***88", RefundRequestPolicy.redactForLog("record-88"));
        assertEquals("ad***77", RefundRequestPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", RefundRequestPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", RefundRequestPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", RefundRequestPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", RefundRequestPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", RefundRequestPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", RefundRequestPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", RefundRequestPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", RefundRequestPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", RefundRequestPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", RefundRequestPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", RefundRequestPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", RefundRequestPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", RefundRequestPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", RefundRequestPolicy.truncate("Access Review", 5));
        assertEquals("War…", RefundRequestPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", RefundRequestPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", RefundRequestPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", RefundRequestPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", RefundRequestPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", RefundRequestPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", RefundRequestPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", RefundRequestPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", RefundRequestPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", RefundRequestPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", RefundRequestPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(RefundRequestPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(RefundRequestPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(RefundRequestPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(RefundRequestPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(RefundRequestPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(RefundRequestPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(RefundRequestPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(RefundRequestPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(RefundRequestPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(RefundRequestPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(RefundRequestPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(RefundRequestPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(RefundRequestPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(RefundRequestPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(RefundRequestPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(RefundRequestPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(RefundRequestPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(RefundRequestPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, RefundRequestPolicy.requiresApproval(null, null));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, RefundRequestPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, RefundRequestPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
