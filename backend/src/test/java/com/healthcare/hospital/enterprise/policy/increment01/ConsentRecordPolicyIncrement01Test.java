package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.ConsentRecordPolicy;

class ConsentRecordPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", ConsentRecordPolicy.normalizeStatus(null));
        assertEquals("NEW", ConsentRecordPolicy.normalizeStatus(""));
        assertEquals("NEW", ConsentRecordPolicy.normalizeStatus(" "));
        assertEquals("NEW", ConsentRecordPolicy.normalizeStatus("new"));
        assertEquals("NEW", ConsentRecordPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", ConsentRecordPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", ConsentRecordPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", ConsentRecordPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", ConsentRecordPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", ConsentRecordPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", ConsentRecordPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", ConsentRecordPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", ConsentRecordPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", ConsentRecordPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", ConsentRecordPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", ConsentRecordPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", ConsentRecordPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", ConsentRecordPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", ConsentRecordPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", ConsentRecordPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", ConsentRecordPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", ConsentRecordPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", ConsentRecordPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", ConsentRecordPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", ConsentRecordPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, ConsentRecordPolicy.pageSize(-100));
        assertEquals(25, ConsentRecordPolicy.pageSize(-1));
        assertEquals(25, ConsentRecordPolicy.pageSize(0));
        assertEquals(1, ConsentRecordPolicy.pageSize(1));
        assertEquals(2, ConsentRecordPolicy.pageSize(2));
        assertEquals(5, ConsentRecordPolicy.pageSize(5));
        assertEquals(10, ConsentRecordPolicy.pageSize(10));
        assertEquals(20, ConsentRecordPolicy.pageSize(20));
        assertEquals(24, ConsentRecordPolicy.pageSize(24));
        assertEquals(25, ConsentRecordPolicy.pageSize(25));
        assertEquals(26, ConsentRecordPolicy.pageSize(26));
        assertEquals(49, ConsentRecordPolicy.pageSize(49));
        assertEquals(50, ConsentRecordPolicy.pageSize(50));
        assertEquals(75, ConsentRecordPolicy.pageSize(75));
        assertEquals(99, ConsentRecordPolicy.pageSize(99));
        assertEquals(100, ConsentRecordPolicy.pageSize(100));
        assertEquals(101, ConsentRecordPolicy.pageSize(101));
        assertEquals(150, ConsentRecordPolicy.pageSize(150));
        assertEquals(199, ConsentRecordPolicy.pageSize(199));
        assertEquals(200, ConsentRecordPolicy.pageSize(200));
        assertEquals(200, ConsentRecordPolicy.pageSize(201));
        assertEquals(200, ConsentRecordPolicy.pageSize(250));
        assertEquals(200, ConsentRecordPolicy.pageSize(500));
        assertEquals(200, ConsentRecordPolicy.pageSize(1000));
        assertEquals(200, ConsentRecordPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, ConsentRecordPolicy.pageIndex(-100));
        assertEquals(0, ConsentRecordPolicy.pageIndex(-10));
        assertEquals(0, ConsentRecordPolicy.pageIndex(-2));
        assertEquals(0, ConsentRecordPolicy.pageIndex(-1));
        assertEquals(0, ConsentRecordPolicy.pageIndex(0));
        assertEquals(1, ConsentRecordPolicy.pageIndex(1));
        assertEquals(2, ConsentRecordPolicy.pageIndex(2));
        assertEquals(3, ConsentRecordPolicy.pageIndex(3));
        assertEquals(4, ConsentRecordPolicy.pageIndex(4));
        assertEquals(5, ConsentRecordPolicy.pageIndex(5));
        assertEquals(6, ConsentRecordPolicy.pageIndex(6));
        assertEquals(7, ConsentRecordPolicy.pageIndex(7));
        assertEquals(8, ConsentRecordPolicy.pageIndex(8));
        assertEquals(9, ConsentRecordPolicy.pageIndex(9));
        assertEquals(10, ConsentRecordPolicy.pageIndex(10));
        assertEquals(11, ConsentRecordPolicy.pageIndex(11));
        assertEquals(12, ConsentRecordPolicy.pageIndex(12));
        assertEquals(13, ConsentRecordPolicy.pageIndex(13));
        assertEquals(14, ConsentRecordPolicy.pageIndex(14));
        assertEquals(15, ConsentRecordPolicy.pageIndex(15));
        assertEquals(20, ConsentRecordPolicy.pageIndex(20));
        assertEquals(50, ConsentRecordPolicy.pageIndex(50));
        assertEquals(100, ConsentRecordPolicy.pageIndex(100));
        assertEquals(1000, ConsentRecordPolicy.pageIndex(1000));
        assertEquals(99999, ConsentRecordPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", ConsentRecordPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ConsentRecordPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", ConsentRecordPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", ConsentRecordPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", ConsentRecordPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", ConsentRecordPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ConsentRecordPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ConsentRecordPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", ConsentRecordPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", ConsentRecordPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ConsentRecordPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", ConsentRecordPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", ConsentRecordPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ConsentRecordPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ConsentRecordPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ConsentRecordPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", ConsentRecordPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ConsentRecordPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", ConsentRecordPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", ConsentRecordPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", ConsentRecordPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ConsentRecordPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ConsentRecordPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ConsentRecordPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", ConsentRecordPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", ConsentRecordPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ConsentRecordPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", ConsentRecordPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", ConsentRecordPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", ConsentRecordPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", ConsentRecordPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ConsentRecordPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ConsentRecordPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", ConsentRecordPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", ConsentRecordPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ConsentRecordPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", ConsentRecordPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", ConsentRecordPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ConsentRecordPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ConsentRecordPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ConsentRecordPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", ConsentRecordPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ConsentRecordPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", ConsentRecordPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", ConsentRecordPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", ConsentRecordPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ConsentRecordPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ConsentRecordPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ConsentRecordPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", ConsentRecordPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", ConsentRecordPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", ConsentRecordPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", ConsentRecordPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", ConsentRecordPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", ConsentRecordPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", ConsentRecordPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", ConsentRecordPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", ConsentRecordPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", ConsentRecordPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", ConsentRecordPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", ConsentRecordPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", ConsentRecordPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", ConsentRecordPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", ConsentRecordPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", ConsentRecordPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", ConsentRecordPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", ConsentRecordPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", ConsentRecordPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", ConsentRecordPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", ConsentRecordPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", ConsentRecordPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", ConsentRecordPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", ConsentRecordPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", ConsentRecordPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", ConsentRecordPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), ConsentRecordPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, ConsentRecordPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, ConsentRecordPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, ConsentRecordPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", ConsentRecordPolicy.normalizePriority("critical"));
        assertEquals("URGENT", ConsentRecordPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", ConsentRecordPolicy.normalizePriority("high"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("normal"));
        assertEquals("LOW", ConsentRecordPolicy.normalizePriority("low"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority(null));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority(""));
        assertEquals("CRITICAL", ConsentRecordPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", ConsentRecordPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", ConsentRecordPolicy.normalizePriority("low "));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", ConsentRecordPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("none"));
        assertEquals("NORMAL", ConsentRecordPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, ConsentRecordPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", ConsentRecordPolicy.redactForLog(""));
        assertEquals("****", ConsentRecordPolicy.redactForLog("a"));
        assertEquals("****", ConsentRecordPolicy.redactForLog("ab"));
        assertEquals("****", ConsentRecordPolicy.redactForLog("abc"));
        assertEquals("****", ConsentRecordPolicy.redactForLog("abcd"));
        assertEquals("ab***de", ConsentRecordPolicy.redactForLog("abcde"));
        assertEquals("pa***23", ConsentRecordPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", ConsentRecordPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", ConsentRecordPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", ConsentRecordPolicy.redactForLog("Rahul"));
        assertEquals("do***01", ConsentRecordPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", ConsentRecordPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", ConsentRecordPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", ConsentRecordPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", ConsentRecordPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", ConsentRecordPolicy.redactForLog("test-value"));
        assertEquals("se***ef", ConsentRecordPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", ConsentRecordPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", ConsentRecordPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", ConsentRecordPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", ConsentRecordPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", ConsentRecordPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", ConsentRecordPolicy.redactForLog("case-2026"));
        assertEquals("re***88", ConsentRecordPolicy.redactForLog("record-88"));
        assertEquals("ad***77", ConsentRecordPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", ConsentRecordPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", ConsentRecordPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", ConsentRecordPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", ConsentRecordPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", ConsentRecordPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", ConsentRecordPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", ConsentRecordPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", ConsentRecordPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", ConsentRecordPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", ConsentRecordPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", ConsentRecordPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", ConsentRecordPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", ConsentRecordPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", ConsentRecordPolicy.truncate("Access Review", 5));
        assertEquals("War…", ConsentRecordPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", ConsentRecordPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", ConsentRecordPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", ConsentRecordPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", ConsentRecordPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", ConsentRecordPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", ConsentRecordPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", ConsentRecordPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", ConsentRecordPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", ConsentRecordPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", ConsentRecordPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(ConsentRecordPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(ConsentRecordPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(ConsentRecordPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(ConsentRecordPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(ConsentRecordPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(ConsentRecordPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(ConsentRecordPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(ConsentRecordPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(ConsentRecordPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(ConsentRecordPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(null, null));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, ConsentRecordPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, ConsentRecordPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
