package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.BackupRunPolicy;

class BackupRunPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", BackupRunPolicy.normalizeStatus(null));
        assertEquals("NEW", BackupRunPolicy.normalizeStatus(""));
        assertEquals("NEW", BackupRunPolicy.normalizeStatus(" "));
        assertEquals("NEW", BackupRunPolicy.normalizeStatus("new"));
        assertEquals("NEW", BackupRunPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", BackupRunPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", BackupRunPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", BackupRunPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", BackupRunPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", BackupRunPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", BackupRunPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", BackupRunPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", BackupRunPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", BackupRunPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", BackupRunPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", BackupRunPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", BackupRunPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", BackupRunPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", BackupRunPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", BackupRunPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", BackupRunPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", BackupRunPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", BackupRunPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", BackupRunPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", BackupRunPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, BackupRunPolicy.pageSize(-100));
        assertEquals(25, BackupRunPolicy.pageSize(-1));
        assertEquals(25, BackupRunPolicy.pageSize(0));
        assertEquals(1, BackupRunPolicy.pageSize(1));
        assertEquals(2, BackupRunPolicy.pageSize(2));
        assertEquals(5, BackupRunPolicy.pageSize(5));
        assertEquals(10, BackupRunPolicy.pageSize(10));
        assertEquals(20, BackupRunPolicy.pageSize(20));
        assertEquals(24, BackupRunPolicy.pageSize(24));
        assertEquals(25, BackupRunPolicy.pageSize(25));
        assertEquals(26, BackupRunPolicy.pageSize(26));
        assertEquals(49, BackupRunPolicy.pageSize(49));
        assertEquals(50, BackupRunPolicy.pageSize(50));
        assertEquals(75, BackupRunPolicy.pageSize(75));
        assertEquals(99, BackupRunPolicy.pageSize(99));
        assertEquals(100, BackupRunPolicy.pageSize(100));
        assertEquals(101, BackupRunPolicy.pageSize(101));
        assertEquals(150, BackupRunPolicy.pageSize(150));
        assertEquals(199, BackupRunPolicy.pageSize(199));
        assertEquals(200, BackupRunPolicy.pageSize(200));
        assertEquals(200, BackupRunPolicy.pageSize(201));
        assertEquals(200, BackupRunPolicy.pageSize(250));
        assertEquals(200, BackupRunPolicy.pageSize(500));
        assertEquals(200, BackupRunPolicy.pageSize(1000));
        assertEquals(200, BackupRunPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, BackupRunPolicy.pageIndex(-100));
        assertEquals(0, BackupRunPolicy.pageIndex(-10));
        assertEquals(0, BackupRunPolicy.pageIndex(-2));
        assertEquals(0, BackupRunPolicy.pageIndex(-1));
        assertEquals(0, BackupRunPolicy.pageIndex(0));
        assertEquals(1, BackupRunPolicy.pageIndex(1));
        assertEquals(2, BackupRunPolicy.pageIndex(2));
        assertEquals(3, BackupRunPolicy.pageIndex(3));
        assertEquals(4, BackupRunPolicy.pageIndex(4));
        assertEquals(5, BackupRunPolicy.pageIndex(5));
        assertEquals(6, BackupRunPolicy.pageIndex(6));
        assertEquals(7, BackupRunPolicy.pageIndex(7));
        assertEquals(8, BackupRunPolicy.pageIndex(8));
        assertEquals(9, BackupRunPolicy.pageIndex(9));
        assertEquals(10, BackupRunPolicy.pageIndex(10));
        assertEquals(11, BackupRunPolicy.pageIndex(11));
        assertEquals(12, BackupRunPolicy.pageIndex(12));
        assertEquals(13, BackupRunPolicy.pageIndex(13));
        assertEquals(14, BackupRunPolicy.pageIndex(14));
        assertEquals(15, BackupRunPolicy.pageIndex(15));
        assertEquals(20, BackupRunPolicy.pageIndex(20));
        assertEquals(50, BackupRunPolicy.pageIndex(50));
        assertEquals(100, BackupRunPolicy.pageIndex(100));
        assertEquals(1000, BackupRunPolicy.pageIndex(1000));
        assertEquals(99999, BackupRunPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", BackupRunPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", BackupRunPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", BackupRunPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", BackupRunPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", BackupRunPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", BackupRunPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", BackupRunPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", BackupRunPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", BackupRunPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", BackupRunPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", BackupRunPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", BackupRunPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", BackupRunPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", BackupRunPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", BackupRunPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", BackupRunPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", BackupRunPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", BackupRunPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", BackupRunPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", BackupRunPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", BackupRunPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", BackupRunPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", BackupRunPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", BackupRunPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", BackupRunPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", BackupRunPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", BackupRunPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", BackupRunPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", BackupRunPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", BackupRunPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", BackupRunPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", BackupRunPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", BackupRunPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", BackupRunPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", BackupRunPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", BackupRunPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", BackupRunPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", BackupRunPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", BackupRunPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", BackupRunPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", BackupRunPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", BackupRunPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", BackupRunPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", BackupRunPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", BackupRunPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", BackupRunPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", BackupRunPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", BackupRunPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", BackupRunPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", BackupRunPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", BackupRunPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", BackupRunPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", BackupRunPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", BackupRunPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", BackupRunPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", BackupRunPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", BackupRunPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", BackupRunPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", BackupRunPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", BackupRunPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", BackupRunPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", BackupRunPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", BackupRunPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", BackupRunPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", BackupRunPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", BackupRunPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", BackupRunPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", BackupRunPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", BackupRunPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", BackupRunPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", BackupRunPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", BackupRunPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", BackupRunPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", BackupRunPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", BackupRunPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), BackupRunPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, BackupRunPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, BackupRunPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, BackupRunPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", BackupRunPolicy.normalizePriority("critical"));
        assertEquals("URGENT", BackupRunPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", BackupRunPolicy.normalizePriority("high"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("normal"));
        assertEquals("LOW", BackupRunPolicy.normalizePriority("low"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority(null));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority(""));
        assertEquals("CRITICAL", BackupRunPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", BackupRunPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", BackupRunPolicy.normalizePriority("low "));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", BackupRunPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("none"));
        assertEquals("NORMAL", BackupRunPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, BackupRunPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, BackupRunPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, BackupRunPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, BackupRunPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, BackupRunPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, BackupRunPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, BackupRunPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, BackupRunPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, BackupRunPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, BackupRunPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, BackupRunPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, BackupRunPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, BackupRunPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, BackupRunPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, BackupRunPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, BackupRunPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, BackupRunPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, BackupRunPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, BackupRunPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, BackupRunPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, BackupRunPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, BackupRunPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, BackupRunPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, BackupRunPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, BackupRunPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", BackupRunPolicy.redactForLog(""));
        assertEquals("****", BackupRunPolicy.redactForLog("a"));
        assertEquals("****", BackupRunPolicy.redactForLog("ab"));
        assertEquals("****", BackupRunPolicy.redactForLog("abc"));
        assertEquals("****", BackupRunPolicy.redactForLog("abcd"));
        assertEquals("ab***de", BackupRunPolicy.redactForLog("abcde"));
        assertEquals("pa***23", BackupRunPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", BackupRunPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", BackupRunPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", BackupRunPolicy.redactForLog("Rahul"));
        assertEquals("do***01", BackupRunPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", BackupRunPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", BackupRunPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", BackupRunPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", BackupRunPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", BackupRunPolicy.redactForLog("test-value"));
        assertEquals("se***ef", BackupRunPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", BackupRunPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", BackupRunPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", BackupRunPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", BackupRunPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", BackupRunPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", BackupRunPolicy.redactForLog("case-2026"));
        assertEquals("re***88", BackupRunPolicy.redactForLog("record-88"));
        assertEquals("ad***77", BackupRunPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", BackupRunPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", BackupRunPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", BackupRunPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", BackupRunPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", BackupRunPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", BackupRunPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", BackupRunPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", BackupRunPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", BackupRunPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", BackupRunPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", BackupRunPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", BackupRunPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", BackupRunPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", BackupRunPolicy.truncate("Access Review", 5));
        assertEquals("War…", BackupRunPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", BackupRunPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", BackupRunPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", BackupRunPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", BackupRunPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", BackupRunPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", BackupRunPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", BackupRunPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", BackupRunPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", BackupRunPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", BackupRunPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(BackupRunPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(BackupRunPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(BackupRunPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(BackupRunPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(BackupRunPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(BackupRunPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(BackupRunPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(BackupRunPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(BackupRunPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(BackupRunPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(BackupRunPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(BackupRunPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(BackupRunPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(BackupRunPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(BackupRunPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(BackupRunPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(BackupRunPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(BackupRunPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(BackupRunPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(BackupRunPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(BackupRunPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(BackupRunPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(BackupRunPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(BackupRunPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(BackupRunPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, BackupRunPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, BackupRunPolicy.requiresApproval(null, null));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, BackupRunPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, BackupRunPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
