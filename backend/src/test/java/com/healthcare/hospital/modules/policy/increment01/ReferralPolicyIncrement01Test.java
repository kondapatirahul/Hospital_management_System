package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.ReferralPolicy;

class ReferralPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", ReferralPolicy.normalizeStatus(null));
        assertEquals("NEW", ReferralPolicy.normalizeStatus(""));
        assertEquals("NEW", ReferralPolicy.normalizeStatus(" "));
        assertEquals("NEW", ReferralPolicy.normalizeStatus("new"));
        assertEquals("NEW", ReferralPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", ReferralPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", ReferralPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", ReferralPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", ReferralPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", ReferralPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", ReferralPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", ReferralPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", ReferralPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", ReferralPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", ReferralPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", ReferralPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", ReferralPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", ReferralPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", ReferralPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", ReferralPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", ReferralPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", ReferralPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", ReferralPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", ReferralPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", ReferralPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, ReferralPolicy.pageSize(-100));
        assertEquals(25, ReferralPolicy.pageSize(-1));
        assertEquals(25, ReferralPolicy.pageSize(0));
        assertEquals(1, ReferralPolicy.pageSize(1));
        assertEquals(2, ReferralPolicy.pageSize(2));
        assertEquals(5, ReferralPolicy.pageSize(5));
        assertEquals(10, ReferralPolicy.pageSize(10));
        assertEquals(20, ReferralPolicy.pageSize(20));
        assertEquals(24, ReferralPolicy.pageSize(24));
        assertEquals(25, ReferralPolicy.pageSize(25));
        assertEquals(26, ReferralPolicy.pageSize(26));
        assertEquals(49, ReferralPolicy.pageSize(49));
        assertEquals(50, ReferralPolicy.pageSize(50));
        assertEquals(75, ReferralPolicy.pageSize(75));
        assertEquals(99, ReferralPolicy.pageSize(99));
        assertEquals(100, ReferralPolicy.pageSize(100));
        assertEquals(101, ReferralPolicy.pageSize(101));
        assertEquals(150, ReferralPolicy.pageSize(150));
        assertEquals(199, ReferralPolicy.pageSize(199));
        assertEquals(200, ReferralPolicy.pageSize(200));
        assertEquals(200, ReferralPolicy.pageSize(201));
        assertEquals(200, ReferralPolicy.pageSize(250));
        assertEquals(200, ReferralPolicy.pageSize(500));
        assertEquals(200, ReferralPolicy.pageSize(1000));
        assertEquals(200, ReferralPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, ReferralPolicy.pageIndex(-100));
        assertEquals(0, ReferralPolicy.pageIndex(-10));
        assertEquals(0, ReferralPolicy.pageIndex(-2));
        assertEquals(0, ReferralPolicy.pageIndex(-1));
        assertEquals(0, ReferralPolicy.pageIndex(0));
        assertEquals(1, ReferralPolicy.pageIndex(1));
        assertEquals(2, ReferralPolicy.pageIndex(2));
        assertEquals(3, ReferralPolicy.pageIndex(3));
        assertEquals(4, ReferralPolicy.pageIndex(4));
        assertEquals(5, ReferralPolicy.pageIndex(5));
        assertEquals(6, ReferralPolicy.pageIndex(6));
        assertEquals(7, ReferralPolicy.pageIndex(7));
        assertEquals(8, ReferralPolicy.pageIndex(8));
        assertEquals(9, ReferralPolicy.pageIndex(9));
        assertEquals(10, ReferralPolicy.pageIndex(10));
        assertEquals(11, ReferralPolicy.pageIndex(11));
        assertEquals(12, ReferralPolicy.pageIndex(12));
        assertEquals(13, ReferralPolicy.pageIndex(13));
        assertEquals(14, ReferralPolicy.pageIndex(14));
        assertEquals(15, ReferralPolicy.pageIndex(15));
        assertEquals(20, ReferralPolicy.pageIndex(20));
        assertEquals(50, ReferralPolicy.pageIndex(50));
        assertEquals(100, ReferralPolicy.pageIndex(100));
        assertEquals(1000, ReferralPolicy.pageIndex(1000));
        assertEquals(99999, ReferralPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", ReferralPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ReferralPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", ReferralPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", ReferralPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", ReferralPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", ReferralPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ReferralPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ReferralPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", ReferralPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", ReferralPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ReferralPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", ReferralPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", ReferralPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ReferralPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ReferralPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ReferralPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", ReferralPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ReferralPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", ReferralPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", ReferralPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", ReferralPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ReferralPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ReferralPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ReferralPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", ReferralPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", ReferralPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ReferralPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", ReferralPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", ReferralPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", ReferralPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", ReferralPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ReferralPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ReferralPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", ReferralPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", ReferralPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ReferralPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", ReferralPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", ReferralPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ReferralPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ReferralPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ReferralPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", ReferralPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ReferralPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", ReferralPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", ReferralPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", ReferralPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ReferralPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ReferralPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ReferralPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", ReferralPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", ReferralPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", ReferralPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", ReferralPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", ReferralPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", ReferralPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", ReferralPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", ReferralPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", ReferralPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", ReferralPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", ReferralPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", ReferralPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", ReferralPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", ReferralPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", ReferralPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", ReferralPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", ReferralPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", ReferralPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", ReferralPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", ReferralPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", ReferralPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", ReferralPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", ReferralPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", ReferralPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", ReferralPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", ReferralPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), ReferralPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), ReferralPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), ReferralPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), ReferralPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), ReferralPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), ReferralPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), ReferralPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), ReferralPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), ReferralPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), ReferralPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), ReferralPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), ReferralPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), ReferralPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, ReferralPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, ReferralPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, ReferralPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", ReferralPolicy.normalizePriority("critical"));
        assertEquals("URGENT", ReferralPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", ReferralPolicy.normalizePriority("high"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("normal"));
        assertEquals("LOW", ReferralPolicy.normalizePriority("low"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority(null));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority(""));
        assertEquals("CRITICAL", ReferralPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", ReferralPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", ReferralPolicy.normalizePriority("low "));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", ReferralPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("none"));
        assertEquals("NORMAL", ReferralPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, ReferralPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, ReferralPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, ReferralPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, ReferralPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, ReferralPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, ReferralPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, ReferralPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, ReferralPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, ReferralPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, ReferralPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, ReferralPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, ReferralPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, ReferralPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, ReferralPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, ReferralPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, ReferralPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, ReferralPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, ReferralPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, ReferralPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, ReferralPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, ReferralPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, ReferralPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, ReferralPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, ReferralPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, ReferralPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", ReferralPolicy.redactForLog(""));
        assertEquals("****", ReferralPolicy.redactForLog("a"));
        assertEquals("****", ReferralPolicy.redactForLog("ab"));
        assertEquals("****", ReferralPolicy.redactForLog("abc"));
        assertEquals("****", ReferralPolicy.redactForLog("abcd"));
        assertEquals("ab***de", ReferralPolicy.redactForLog("abcde"));
        assertEquals("pa***23", ReferralPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", ReferralPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", ReferralPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", ReferralPolicy.redactForLog("Rahul"));
        assertEquals("do***01", ReferralPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", ReferralPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", ReferralPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", ReferralPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", ReferralPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", ReferralPolicy.redactForLog("test-value"));
        assertEquals("se***ef", ReferralPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", ReferralPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", ReferralPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", ReferralPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", ReferralPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", ReferralPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", ReferralPolicy.redactForLog("case-2026"));
        assertEquals("re***88", ReferralPolicy.redactForLog("record-88"));
        assertEquals("ad***77", ReferralPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", ReferralPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", ReferralPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", ReferralPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", ReferralPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", ReferralPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", ReferralPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", ReferralPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", ReferralPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", ReferralPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", ReferralPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", ReferralPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", ReferralPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", ReferralPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", ReferralPolicy.truncate("Access Review", 5));
        assertEquals("War…", ReferralPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", ReferralPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", ReferralPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", ReferralPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", ReferralPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", ReferralPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", ReferralPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", ReferralPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", ReferralPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", ReferralPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", ReferralPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(ReferralPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(ReferralPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(ReferralPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(ReferralPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(ReferralPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(ReferralPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(ReferralPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(ReferralPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(ReferralPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(ReferralPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(ReferralPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(ReferralPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(ReferralPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(ReferralPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(ReferralPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(ReferralPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(ReferralPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(ReferralPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(ReferralPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(ReferralPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(ReferralPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(ReferralPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(ReferralPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(ReferralPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(ReferralPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, ReferralPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, ReferralPolicy.requiresApproval(null, null));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, ReferralPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, ReferralPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
