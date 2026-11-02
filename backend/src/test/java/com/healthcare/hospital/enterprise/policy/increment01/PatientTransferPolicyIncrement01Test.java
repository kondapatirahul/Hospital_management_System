package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.PatientTransferPolicy;

class PatientTransferPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", PatientTransferPolicy.normalizeStatus(null));
        assertEquals("NEW", PatientTransferPolicy.normalizeStatus(""));
        assertEquals("NEW", PatientTransferPolicy.normalizeStatus(" "));
        assertEquals("NEW", PatientTransferPolicy.normalizeStatus("new"));
        assertEquals("NEW", PatientTransferPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", PatientTransferPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", PatientTransferPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", PatientTransferPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", PatientTransferPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", PatientTransferPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", PatientTransferPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", PatientTransferPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", PatientTransferPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", PatientTransferPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", PatientTransferPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", PatientTransferPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", PatientTransferPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", PatientTransferPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", PatientTransferPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", PatientTransferPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", PatientTransferPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", PatientTransferPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", PatientTransferPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", PatientTransferPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", PatientTransferPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, PatientTransferPolicy.pageSize(-100));
        assertEquals(25, PatientTransferPolicy.pageSize(-1));
        assertEquals(25, PatientTransferPolicy.pageSize(0));
        assertEquals(1, PatientTransferPolicy.pageSize(1));
        assertEquals(2, PatientTransferPolicy.pageSize(2));
        assertEquals(5, PatientTransferPolicy.pageSize(5));
        assertEquals(10, PatientTransferPolicy.pageSize(10));
        assertEquals(20, PatientTransferPolicy.pageSize(20));
        assertEquals(24, PatientTransferPolicy.pageSize(24));
        assertEquals(25, PatientTransferPolicy.pageSize(25));
        assertEquals(26, PatientTransferPolicy.pageSize(26));
        assertEquals(49, PatientTransferPolicy.pageSize(49));
        assertEquals(50, PatientTransferPolicy.pageSize(50));
        assertEquals(75, PatientTransferPolicy.pageSize(75));
        assertEquals(99, PatientTransferPolicy.pageSize(99));
        assertEquals(100, PatientTransferPolicy.pageSize(100));
        assertEquals(101, PatientTransferPolicy.pageSize(101));
        assertEquals(150, PatientTransferPolicy.pageSize(150));
        assertEquals(199, PatientTransferPolicy.pageSize(199));
        assertEquals(200, PatientTransferPolicy.pageSize(200));
        assertEquals(200, PatientTransferPolicy.pageSize(201));
        assertEquals(200, PatientTransferPolicy.pageSize(250));
        assertEquals(200, PatientTransferPolicy.pageSize(500));
        assertEquals(200, PatientTransferPolicy.pageSize(1000));
        assertEquals(200, PatientTransferPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, PatientTransferPolicy.pageIndex(-100));
        assertEquals(0, PatientTransferPolicy.pageIndex(-10));
        assertEquals(0, PatientTransferPolicy.pageIndex(-2));
        assertEquals(0, PatientTransferPolicy.pageIndex(-1));
        assertEquals(0, PatientTransferPolicy.pageIndex(0));
        assertEquals(1, PatientTransferPolicy.pageIndex(1));
        assertEquals(2, PatientTransferPolicy.pageIndex(2));
        assertEquals(3, PatientTransferPolicy.pageIndex(3));
        assertEquals(4, PatientTransferPolicy.pageIndex(4));
        assertEquals(5, PatientTransferPolicy.pageIndex(5));
        assertEquals(6, PatientTransferPolicy.pageIndex(6));
        assertEquals(7, PatientTransferPolicy.pageIndex(7));
        assertEquals(8, PatientTransferPolicy.pageIndex(8));
        assertEquals(9, PatientTransferPolicy.pageIndex(9));
        assertEquals(10, PatientTransferPolicy.pageIndex(10));
        assertEquals(11, PatientTransferPolicy.pageIndex(11));
        assertEquals(12, PatientTransferPolicy.pageIndex(12));
        assertEquals(13, PatientTransferPolicy.pageIndex(13));
        assertEquals(14, PatientTransferPolicy.pageIndex(14));
        assertEquals(15, PatientTransferPolicy.pageIndex(15));
        assertEquals(20, PatientTransferPolicy.pageIndex(20));
        assertEquals(50, PatientTransferPolicy.pageIndex(50));
        assertEquals(100, PatientTransferPolicy.pageIndex(100));
        assertEquals(1000, PatientTransferPolicy.pageIndex(1000));
        assertEquals(99999, PatientTransferPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", PatientTransferPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PatientTransferPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", PatientTransferPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", PatientTransferPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", PatientTransferPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", PatientTransferPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PatientTransferPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PatientTransferPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", PatientTransferPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", PatientTransferPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PatientTransferPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", PatientTransferPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", PatientTransferPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PatientTransferPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PatientTransferPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PatientTransferPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", PatientTransferPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PatientTransferPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", PatientTransferPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", PatientTransferPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", PatientTransferPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PatientTransferPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PatientTransferPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PatientTransferPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", PatientTransferPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", PatientTransferPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PatientTransferPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", PatientTransferPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", PatientTransferPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", PatientTransferPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", PatientTransferPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PatientTransferPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PatientTransferPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", PatientTransferPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", PatientTransferPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PatientTransferPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", PatientTransferPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", PatientTransferPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PatientTransferPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PatientTransferPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PatientTransferPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", PatientTransferPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PatientTransferPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", PatientTransferPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", PatientTransferPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", PatientTransferPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PatientTransferPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PatientTransferPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PatientTransferPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", PatientTransferPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", PatientTransferPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", PatientTransferPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", PatientTransferPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", PatientTransferPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", PatientTransferPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", PatientTransferPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", PatientTransferPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", PatientTransferPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", PatientTransferPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", PatientTransferPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", PatientTransferPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", PatientTransferPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", PatientTransferPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", PatientTransferPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", PatientTransferPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", PatientTransferPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", PatientTransferPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", PatientTransferPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", PatientTransferPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", PatientTransferPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", PatientTransferPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", PatientTransferPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", PatientTransferPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", PatientTransferPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", PatientTransferPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), PatientTransferPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, PatientTransferPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, PatientTransferPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, PatientTransferPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", PatientTransferPolicy.normalizePriority("critical"));
        assertEquals("URGENT", PatientTransferPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", PatientTransferPolicy.normalizePriority("high"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("normal"));
        assertEquals("LOW", PatientTransferPolicy.normalizePriority("low"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority(null));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority(""));
        assertEquals("CRITICAL", PatientTransferPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", PatientTransferPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", PatientTransferPolicy.normalizePriority("low "));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", PatientTransferPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("none"));
        assertEquals("NORMAL", PatientTransferPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, PatientTransferPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", PatientTransferPolicy.redactForLog(""));
        assertEquals("****", PatientTransferPolicy.redactForLog("a"));
        assertEquals("****", PatientTransferPolicy.redactForLog("ab"));
        assertEquals("****", PatientTransferPolicy.redactForLog("abc"));
        assertEquals("****", PatientTransferPolicy.redactForLog("abcd"));
        assertEquals("ab***de", PatientTransferPolicy.redactForLog("abcde"));
        assertEquals("pa***23", PatientTransferPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", PatientTransferPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", PatientTransferPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", PatientTransferPolicy.redactForLog("Rahul"));
        assertEquals("do***01", PatientTransferPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", PatientTransferPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", PatientTransferPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", PatientTransferPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", PatientTransferPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", PatientTransferPolicy.redactForLog("test-value"));
        assertEquals("se***ef", PatientTransferPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", PatientTransferPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", PatientTransferPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", PatientTransferPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", PatientTransferPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", PatientTransferPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", PatientTransferPolicy.redactForLog("case-2026"));
        assertEquals("re***88", PatientTransferPolicy.redactForLog("record-88"));
        assertEquals("ad***77", PatientTransferPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", PatientTransferPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", PatientTransferPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", PatientTransferPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", PatientTransferPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", PatientTransferPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", PatientTransferPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", PatientTransferPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", PatientTransferPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", PatientTransferPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", PatientTransferPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", PatientTransferPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", PatientTransferPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", PatientTransferPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", PatientTransferPolicy.truncate("Access Review", 5));
        assertEquals("War…", PatientTransferPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", PatientTransferPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", PatientTransferPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", PatientTransferPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", PatientTransferPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", PatientTransferPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", PatientTransferPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", PatientTransferPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", PatientTransferPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", PatientTransferPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", PatientTransferPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(PatientTransferPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(PatientTransferPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(PatientTransferPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(PatientTransferPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(PatientTransferPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(PatientTransferPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(PatientTransferPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(PatientTransferPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(PatientTransferPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(PatientTransferPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(PatientTransferPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(PatientTransferPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(PatientTransferPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(PatientTransferPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(PatientTransferPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(PatientTransferPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(PatientTransferPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(PatientTransferPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, PatientTransferPolicy.requiresApproval(null, null));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, PatientTransferPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, PatientTransferPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
