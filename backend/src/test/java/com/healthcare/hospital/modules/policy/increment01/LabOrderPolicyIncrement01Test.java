package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.LabOrderPolicy;

class LabOrderPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", LabOrderPolicy.normalizeStatus(null));
        assertEquals("NEW", LabOrderPolicy.normalizeStatus(""));
        assertEquals("NEW", LabOrderPolicy.normalizeStatus(" "));
        assertEquals("NEW", LabOrderPolicy.normalizeStatus("new"));
        assertEquals("NEW", LabOrderPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", LabOrderPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", LabOrderPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", LabOrderPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", LabOrderPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", LabOrderPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", LabOrderPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", LabOrderPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", LabOrderPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", LabOrderPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", LabOrderPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", LabOrderPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", LabOrderPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", LabOrderPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", LabOrderPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", LabOrderPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", LabOrderPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", LabOrderPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", LabOrderPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", LabOrderPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", LabOrderPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, LabOrderPolicy.pageSize(-100));
        assertEquals(25, LabOrderPolicy.pageSize(-1));
        assertEquals(25, LabOrderPolicy.pageSize(0));
        assertEquals(1, LabOrderPolicy.pageSize(1));
        assertEquals(2, LabOrderPolicy.pageSize(2));
        assertEquals(5, LabOrderPolicy.pageSize(5));
        assertEquals(10, LabOrderPolicy.pageSize(10));
        assertEquals(20, LabOrderPolicy.pageSize(20));
        assertEquals(24, LabOrderPolicy.pageSize(24));
        assertEquals(25, LabOrderPolicy.pageSize(25));
        assertEquals(26, LabOrderPolicy.pageSize(26));
        assertEquals(49, LabOrderPolicy.pageSize(49));
        assertEquals(50, LabOrderPolicy.pageSize(50));
        assertEquals(75, LabOrderPolicy.pageSize(75));
        assertEquals(99, LabOrderPolicy.pageSize(99));
        assertEquals(100, LabOrderPolicy.pageSize(100));
        assertEquals(101, LabOrderPolicy.pageSize(101));
        assertEquals(150, LabOrderPolicy.pageSize(150));
        assertEquals(199, LabOrderPolicy.pageSize(199));
        assertEquals(200, LabOrderPolicy.pageSize(200));
        assertEquals(200, LabOrderPolicy.pageSize(201));
        assertEquals(200, LabOrderPolicy.pageSize(250));
        assertEquals(200, LabOrderPolicy.pageSize(500));
        assertEquals(200, LabOrderPolicy.pageSize(1000));
        assertEquals(200, LabOrderPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, LabOrderPolicy.pageIndex(-100));
        assertEquals(0, LabOrderPolicy.pageIndex(-10));
        assertEquals(0, LabOrderPolicy.pageIndex(-2));
        assertEquals(0, LabOrderPolicy.pageIndex(-1));
        assertEquals(0, LabOrderPolicy.pageIndex(0));
        assertEquals(1, LabOrderPolicy.pageIndex(1));
        assertEquals(2, LabOrderPolicy.pageIndex(2));
        assertEquals(3, LabOrderPolicy.pageIndex(3));
        assertEquals(4, LabOrderPolicy.pageIndex(4));
        assertEquals(5, LabOrderPolicy.pageIndex(5));
        assertEquals(6, LabOrderPolicy.pageIndex(6));
        assertEquals(7, LabOrderPolicy.pageIndex(7));
        assertEquals(8, LabOrderPolicy.pageIndex(8));
        assertEquals(9, LabOrderPolicy.pageIndex(9));
        assertEquals(10, LabOrderPolicy.pageIndex(10));
        assertEquals(11, LabOrderPolicy.pageIndex(11));
        assertEquals(12, LabOrderPolicy.pageIndex(12));
        assertEquals(13, LabOrderPolicy.pageIndex(13));
        assertEquals(14, LabOrderPolicy.pageIndex(14));
        assertEquals(15, LabOrderPolicy.pageIndex(15));
        assertEquals(20, LabOrderPolicy.pageIndex(20));
        assertEquals(50, LabOrderPolicy.pageIndex(50));
        assertEquals(100, LabOrderPolicy.pageIndex(100));
        assertEquals(1000, LabOrderPolicy.pageIndex(1000));
        assertEquals(99999, LabOrderPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", LabOrderPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabOrderPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", LabOrderPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", LabOrderPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", LabOrderPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabOrderPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabOrderPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabOrderPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", LabOrderPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabOrderPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabOrderPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", LabOrderPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabOrderPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabOrderPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabOrderPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabOrderPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabOrderPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabOrderPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabOrderPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", LabOrderPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", LabOrderPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabOrderPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabOrderPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabOrderPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", LabOrderPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", LabOrderPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabOrderPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", LabOrderPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", LabOrderPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", LabOrderPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabOrderPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabOrderPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabOrderPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", LabOrderPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabOrderPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabOrderPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", LabOrderPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabOrderPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabOrderPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabOrderPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabOrderPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabOrderPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabOrderPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabOrderPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", LabOrderPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", LabOrderPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabOrderPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabOrderPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabOrderPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", LabOrderPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", LabOrderPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", LabOrderPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", LabOrderPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", LabOrderPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", LabOrderPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", LabOrderPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", LabOrderPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", LabOrderPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", LabOrderPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", LabOrderPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", LabOrderPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", LabOrderPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", LabOrderPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", LabOrderPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", LabOrderPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", LabOrderPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", LabOrderPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", LabOrderPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", LabOrderPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", LabOrderPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", LabOrderPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", LabOrderPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", LabOrderPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", LabOrderPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", LabOrderPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), LabOrderPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, LabOrderPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, LabOrderPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, LabOrderPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", LabOrderPolicy.normalizePriority("critical"));
        assertEquals("URGENT", LabOrderPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", LabOrderPolicy.normalizePriority("high"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("normal"));
        assertEquals("LOW", LabOrderPolicy.normalizePriority("low"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority(null));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority(""));
        assertEquals("CRITICAL", LabOrderPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", LabOrderPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", LabOrderPolicy.normalizePriority("low "));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", LabOrderPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("none"));
        assertEquals("NORMAL", LabOrderPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, LabOrderPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, LabOrderPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, LabOrderPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, LabOrderPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, LabOrderPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, LabOrderPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, LabOrderPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, LabOrderPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, LabOrderPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, LabOrderPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, LabOrderPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, LabOrderPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, LabOrderPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, LabOrderPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, LabOrderPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, LabOrderPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, LabOrderPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, LabOrderPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, LabOrderPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, LabOrderPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, LabOrderPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, LabOrderPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, LabOrderPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, LabOrderPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, LabOrderPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", LabOrderPolicy.redactForLog(""));
        assertEquals("****", LabOrderPolicy.redactForLog("a"));
        assertEquals("****", LabOrderPolicy.redactForLog("ab"));
        assertEquals("****", LabOrderPolicy.redactForLog("abc"));
        assertEquals("****", LabOrderPolicy.redactForLog("abcd"));
        assertEquals("ab***de", LabOrderPolicy.redactForLog("abcde"));
        assertEquals("pa***23", LabOrderPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", LabOrderPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", LabOrderPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", LabOrderPolicy.redactForLog("Rahul"));
        assertEquals("do***01", LabOrderPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", LabOrderPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", LabOrderPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", LabOrderPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", LabOrderPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", LabOrderPolicy.redactForLog("test-value"));
        assertEquals("se***ef", LabOrderPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", LabOrderPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", LabOrderPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", LabOrderPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", LabOrderPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", LabOrderPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", LabOrderPolicy.redactForLog("case-2026"));
        assertEquals("re***88", LabOrderPolicy.redactForLog("record-88"));
        assertEquals("ad***77", LabOrderPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", LabOrderPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", LabOrderPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", LabOrderPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", LabOrderPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", LabOrderPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", LabOrderPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", LabOrderPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", LabOrderPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", LabOrderPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", LabOrderPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", LabOrderPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", LabOrderPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", LabOrderPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", LabOrderPolicy.truncate("Access Review", 5));
        assertEquals("War…", LabOrderPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", LabOrderPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", LabOrderPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", LabOrderPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", LabOrderPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", LabOrderPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", LabOrderPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", LabOrderPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", LabOrderPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", LabOrderPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", LabOrderPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(LabOrderPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(LabOrderPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(LabOrderPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(LabOrderPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(LabOrderPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(LabOrderPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(LabOrderPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(LabOrderPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(LabOrderPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(LabOrderPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(LabOrderPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(LabOrderPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(LabOrderPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(LabOrderPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(LabOrderPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(LabOrderPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(LabOrderPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(LabOrderPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(LabOrderPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(LabOrderPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(LabOrderPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(LabOrderPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(LabOrderPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(LabOrderPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(LabOrderPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, LabOrderPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, LabOrderPolicy.requiresApproval(null, null));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, LabOrderPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, LabOrderPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
