package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.AccessReviewPolicy;

class AccessReviewPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", AccessReviewPolicy.normalizeStatus(null));
        assertEquals("NEW", AccessReviewPolicy.normalizeStatus(""));
        assertEquals("NEW", AccessReviewPolicy.normalizeStatus(" "));
        assertEquals("NEW", AccessReviewPolicy.normalizeStatus("new"));
        assertEquals("NEW", AccessReviewPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", AccessReviewPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", AccessReviewPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", AccessReviewPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", AccessReviewPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", AccessReviewPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", AccessReviewPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", AccessReviewPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", AccessReviewPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", AccessReviewPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", AccessReviewPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", AccessReviewPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", AccessReviewPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", AccessReviewPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", AccessReviewPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", AccessReviewPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", AccessReviewPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", AccessReviewPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", AccessReviewPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", AccessReviewPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", AccessReviewPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, AccessReviewPolicy.pageSize(-100));
        assertEquals(25, AccessReviewPolicy.pageSize(-1));
        assertEquals(25, AccessReviewPolicy.pageSize(0));
        assertEquals(1, AccessReviewPolicy.pageSize(1));
        assertEquals(2, AccessReviewPolicy.pageSize(2));
        assertEquals(5, AccessReviewPolicy.pageSize(5));
        assertEquals(10, AccessReviewPolicy.pageSize(10));
        assertEquals(20, AccessReviewPolicy.pageSize(20));
        assertEquals(24, AccessReviewPolicy.pageSize(24));
        assertEquals(25, AccessReviewPolicy.pageSize(25));
        assertEquals(26, AccessReviewPolicy.pageSize(26));
        assertEquals(49, AccessReviewPolicy.pageSize(49));
        assertEquals(50, AccessReviewPolicy.pageSize(50));
        assertEquals(75, AccessReviewPolicy.pageSize(75));
        assertEquals(99, AccessReviewPolicy.pageSize(99));
        assertEquals(100, AccessReviewPolicy.pageSize(100));
        assertEquals(101, AccessReviewPolicy.pageSize(101));
        assertEquals(150, AccessReviewPolicy.pageSize(150));
        assertEquals(199, AccessReviewPolicy.pageSize(199));
        assertEquals(200, AccessReviewPolicy.pageSize(200));
        assertEquals(200, AccessReviewPolicy.pageSize(201));
        assertEquals(200, AccessReviewPolicy.pageSize(250));
        assertEquals(200, AccessReviewPolicy.pageSize(500));
        assertEquals(200, AccessReviewPolicy.pageSize(1000));
        assertEquals(200, AccessReviewPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, AccessReviewPolicy.pageIndex(-100));
        assertEquals(0, AccessReviewPolicy.pageIndex(-10));
        assertEquals(0, AccessReviewPolicy.pageIndex(-2));
        assertEquals(0, AccessReviewPolicy.pageIndex(-1));
        assertEquals(0, AccessReviewPolicy.pageIndex(0));
        assertEquals(1, AccessReviewPolicy.pageIndex(1));
        assertEquals(2, AccessReviewPolicy.pageIndex(2));
        assertEquals(3, AccessReviewPolicy.pageIndex(3));
        assertEquals(4, AccessReviewPolicy.pageIndex(4));
        assertEquals(5, AccessReviewPolicy.pageIndex(5));
        assertEquals(6, AccessReviewPolicy.pageIndex(6));
        assertEquals(7, AccessReviewPolicy.pageIndex(7));
        assertEquals(8, AccessReviewPolicy.pageIndex(8));
        assertEquals(9, AccessReviewPolicy.pageIndex(9));
        assertEquals(10, AccessReviewPolicy.pageIndex(10));
        assertEquals(11, AccessReviewPolicy.pageIndex(11));
        assertEquals(12, AccessReviewPolicy.pageIndex(12));
        assertEquals(13, AccessReviewPolicy.pageIndex(13));
        assertEquals(14, AccessReviewPolicy.pageIndex(14));
        assertEquals(15, AccessReviewPolicy.pageIndex(15));
        assertEquals(20, AccessReviewPolicy.pageIndex(20));
        assertEquals(50, AccessReviewPolicy.pageIndex(50));
        assertEquals(100, AccessReviewPolicy.pageIndex(100));
        assertEquals(1000, AccessReviewPolicy.pageIndex(1000));
        assertEquals(99999, AccessReviewPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", AccessReviewPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", AccessReviewPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", AccessReviewPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", AccessReviewPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", AccessReviewPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", AccessReviewPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", AccessReviewPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", AccessReviewPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", AccessReviewPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", AccessReviewPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", AccessReviewPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", AccessReviewPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", AccessReviewPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", AccessReviewPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", AccessReviewPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", AccessReviewPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", AccessReviewPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", AccessReviewPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", AccessReviewPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", AccessReviewPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", AccessReviewPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", AccessReviewPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", AccessReviewPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", AccessReviewPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", AccessReviewPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", AccessReviewPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", AccessReviewPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", AccessReviewPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", AccessReviewPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", AccessReviewPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", AccessReviewPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", AccessReviewPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", AccessReviewPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", AccessReviewPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", AccessReviewPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", AccessReviewPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", AccessReviewPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", AccessReviewPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", AccessReviewPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", AccessReviewPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", AccessReviewPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", AccessReviewPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", AccessReviewPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", AccessReviewPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", AccessReviewPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", AccessReviewPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", AccessReviewPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", AccessReviewPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", AccessReviewPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", AccessReviewPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", AccessReviewPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", AccessReviewPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", AccessReviewPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", AccessReviewPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", AccessReviewPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", AccessReviewPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", AccessReviewPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", AccessReviewPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", AccessReviewPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", AccessReviewPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", AccessReviewPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", AccessReviewPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", AccessReviewPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", AccessReviewPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", AccessReviewPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", AccessReviewPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", AccessReviewPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", AccessReviewPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", AccessReviewPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", AccessReviewPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", AccessReviewPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", AccessReviewPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", AccessReviewPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", AccessReviewPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", AccessReviewPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), AccessReviewPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, AccessReviewPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, AccessReviewPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, AccessReviewPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", AccessReviewPolicy.normalizePriority("critical"));
        assertEquals("URGENT", AccessReviewPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", AccessReviewPolicy.normalizePriority("high"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("normal"));
        assertEquals("LOW", AccessReviewPolicy.normalizePriority("low"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority(null));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority(""));
        assertEquals("CRITICAL", AccessReviewPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", AccessReviewPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", AccessReviewPolicy.normalizePriority("low "));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", AccessReviewPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("none"));
        assertEquals("NORMAL", AccessReviewPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, AccessReviewPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", AccessReviewPolicy.redactForLog(""));
        assertEquals("****", AccessReviewPolicy.redactForLog("a"));
        assertEquals("****", AccessReviewPolicy.redactForLog("ab"));
        assertEquals("****", AccessReviewPolicy.redactForLog("abc"));
        assertEquals("****", AccessReviewPolicy.redactForLog("abcd"));
        assertEquals("ab***de", AccessReviewPolicy.redactForLog("abcde"));
        assertEquals("pa***23", AccessReviewPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", AccessReviewPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", AccessReviewPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", AccessReviewPolicy.redactForLog("Rahul"));
        assertEquals("do***01", AccessReviewPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", AccessReviewPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", AccessReviewPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", AccessReviewPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", AccessReviewPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", AccessReviewPolicy.redactForLog("test-value"));
        assertEquals("se***ef", AccessReviewPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", AccessReviewPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", AccessReviewPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", AccessReviewPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", AccessReviewPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", AccessReviewPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", AccessReviewPolicy.redactForLog("case-2026"));
        assertEquals("re***88", AccessReviewPolicy.redactForLog("record-88"));
        assertEquals("ad***77", AccessReviewPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", AccessReviewPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", AccessReviewPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", AccessReviewPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", AccessReviewPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", AccessReviewPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", AccessReviewPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", AccessReviewPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", AccessReviewPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", AccessReviewPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", AccessReviewPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", AccessReviewPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", AccessReviewPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", AccessReviewPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", AccessReviewPolicy.truncate("Access Review", 5));
        assertEquals("War…", AccessReviewPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", AccessReviewPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", AccessReviewPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", AccessReviewPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", AccessReviewPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", AccessReviewPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", AccessReviewPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", AccessReviewPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", AccessReviewPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", AccessReviewPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", AccessReviewPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(AccessReviewPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(AccessReviewPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(AccessReviewPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(AccessReviewPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(AccessReviewPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(AccessReviewPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(AccessReviewPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(AccessReviewPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(AccessReviewPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(AccessReviewPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(AccessReviewPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(AccessReviewPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(AccessReviewPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(AccessReviewPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(AccessReviewPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(AccessReviewPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(AccessReviewPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(AccessReviewPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, AccessReviewPolicy.requiresApproval(null, null));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, AccessReviewPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, AccessReviewPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
