package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.StaffCredentialPolicy;

class StaffCredentialPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", StaffCredentialPolicy.normalizeStatus(null));
        assertEquals("NEW", StaffCredentialPolicy.normalizeStatus(""));
        assertEquals("NEW", StaffCredentialPolicy.normalizeStatus(" "));
        assertEquals("NEW", StaffCredentialPolicy.normalizeStatus("new"));
        assertEquals("NEW", StaffCredentialPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", StaffCredentialPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", StaffCredentialPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", StaffCredentialPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", StaffCredentialPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", StaffCredentialPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", StaffCredentialPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", StaffCredentialPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", StaffCredentialPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", StaffCredentialPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", StaffCredentialPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", StaffCredentialPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", StaffCredentialPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", StaffCredentialPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", StaffCredentialPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", StaffCredentialPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", StaffCredentialPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", StaffCredentialPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", StaffCredentialPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", StaffCredentialPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", StaffCredentialPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, StaffCredentialPolicy.pageSize(-100));
        assertEquals(25, StaffCredentialPolicy.pageSize(-1));
        assertEquals(25, StaffCredentialPolicy.pageSize(0));
        assertEquals(1, StaffCredentialPolicy.pageSize(1));
        assertEquals(2, StaffCredentialPolicy.pageSize(2));
        assertEquals(5, StaffCredentialPolicy.pageSize(5));
        assertEquals(10, StaffCredentialPolicy.pageSize(10));
        assertEquals(20, StaffCredentialPolicy.pageSize(20));
        assertEquals(24, StaffCredentialPolicy.pageSize(24));
        assertEquals(25, StaffCredentialPolicy.pageSize(25));
        assertEquals(26, StaffCredentialPolicy.pageSize(26));
        assertEquals(49, StaffCredentialPolicy.pageSize(49));
        assertEquals(50, StaffCredentialPolicy.pageSize(50));
        assertEquals(75, StaffCredentialPolicy.pageSize(75));
        assertEquals(99, StaffCredentialPolicy.pageSize(99));
        assertEquals(100, StaffCredentialPolicy.pageSize(100));
        assertEquals(101, StaffCredentialPolicy.pageSize(101));
        assertEquals(150, StaffCredentialPolicy.pageSize(150));
        assertEquals(199, StaffCredentialPolicy.pageSize(199));
        assertEquals(200, StaffCredentialPolicy.pageSize(200));
        assertEquals(200, StaffCredentialPolicy.pageSize(201));
        assertEquals(200, StaffCredentialPolicy.pageSize(250));
        assertEquals(200, StaffCredentialPolicy.pageSize(500));
        assertEquals(200, StaffCredentialPolicy.pageSize(1000));
        assertEquals(200, StaffCredentialPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, StaffCredentialPolicy.pageIndex(-100));
        assertEquals(0, StaffCredentialPolicy.pageIndex(-10));
        assertEquals(0, StaffCredentialPolicy.pageIndex(-2));
        assertEquals(0, StaffCredentialPolicy.pageIndex(-1));
        assertEquals(0, StaffCredentialPolicy.pageIndex(0));
        assertEquals(1, StaffCredentialPolicy.pageIndex(1));
        assertEquals(2, StaffCredentialPolicy.pageIndex(2));
        assertEquals(3, StaffCredentialPolicy.pageIndex(3));
        assertEquals(4, StaffCredentialPolicy.pageIndex(4));
        assertEquals(5, StaffCredentialPolicy.pageIndex(5));
        assertEquals(6, StaffCredentialPolicy.pageIndex(6));
        assertEquals(7, StaffCredentialPolicy.pageIndex(7));
        assertEquals(8, StaffCredentialPolicy.pageIndex(8));
        assertEquals(9, StaffCredentialPolicy.pageIndex(9));
        assertEquals(10, StaffCredentialPolicy.pageIndex(10));
        assertEquals(11, StaffCredentialPolicy.pageIndex(11));
        assertEquals(12, StaffCredentialPolicy.pageIndex(12));
        assertEquals(13, StaffCredentialPolicy.pageIndex(13));
        assertEquals(14, StaffCredentialPolicy.pageIndex(14));
        assertEquals(15, StaffCredentialPolicy.pageIndex(15));
        assertEquals(20, StaffCredentialPolicy.pageIndex(20));
        assertEquals(50, StaffCredentialPolicy.pageIndex(50));
        assertEquals(100, StaffCredentialPolicy.pageIndex(100));
        assertEquals(1000, StaffCredentialPolicy.pageIndex(1000));
        assertEquals(99999, StaffCredentialPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", StaffCredentialPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", StaffCredentialPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", StaffCredentialPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", StaffCredentialPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", StaffCredentialPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", StaffCredentialPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", StaffCredentialPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", StaffCredentialPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", StaffCredentialPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", StaffCredentialPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", StaffCredentialPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", StaffCredentialPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", StaffCredentialPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", StaffCredentialPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", StaffCredentialPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", StaffCredentialPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", StaffCredentialPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", StaffCredentialPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", StaffCredentialPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", StaffCredentialPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", StaffCredentialPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", StaffCredentialPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", StaffCredentialPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", StaffCredentialPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", StaffCredentialPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", StaffCredentialPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", StaffCredentialPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", StaffCredentialPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", StaffCredentialPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", StaffCredentialPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", StaffCredentialPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", StaffCredentialPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", StaffCredentialPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", StaffCredentialPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", StaffCredentialPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", StaffCredentialPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", StaffCredentialPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", StaffCredentialPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", StaffCredentialPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", StaffCredentialPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", StaffCredentialPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", StaffCredentialPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", StaffCredentialPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", StaffCredentialPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", StaffCredentialPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", StaffCredentialPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", StaffCredentialPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", StaffCredentialPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", StaffCredentialPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", StaffCredentialPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", StaffCredentialPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", StaffCredentialPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", StaffCredentialPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", StaffCredentialPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", StaffCredentialPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", StaffCredentialPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", StaffCredentialPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", StaffCredentialPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", StaffCredentialPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", StaffCredentialPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", StaffCredentialPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", StaffCredentialPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", StaffCredentialPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", StaffCredentialPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", StaffCredentialPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", StaffCredentialPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", StaffCredentialPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", StaffCredentialPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", StaffCredentialPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", StaffCredentialPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", StaffCredentialPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", StaffCredentialPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", StaffCredentialPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", StaffCredentialPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", StaffCredentialPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), StaffCredentialPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, StaffCredentialPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, StaffCredentialPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, StaffCredentialPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", StaffCredentialPolicy.normalizePriority("critical"));
        assertEquals("URGENT", StaffCredentialPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", StaffCredentialPolicy.normalizePriority("high"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("normal"));
        assertEquals("LOW", StaffCredentialPolicy.normalizePriority("low"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority(null));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority(""));
        assertEquals("CRITICAL", StaffCredentialPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", StaffCredentialPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", StaffCredentialPolicy.normalizePriority("low "));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", StaffCredentialPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("none"));
        assertEquals("NORMAL", StaffCredentialPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, StaffCredentialPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", StaffCredentialPolicy.redactForLog(""));
        assertEquals("****", StaffCredentialPolicy.redactForLog("a"));
        assertEquals("****", StaffCredentialPolicy.redactForLog("ab"));
        assertEquals("****", StaffCredentialPolicy.redactForLog("abc"));
        assertEquals("****", StaffCredentialPolicy.redactForLog("abcd"));
        assertEquals("ab***de", StaffCredentialPolicy.redactForLog("abcde"));
        assertEquals("pa***23", StaffCredentialPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", StaffCredentialPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", StaffCredentialPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", StaffCredentialPolicy.redactForLog("Rahul"));
        assertEquals("do***01", StaffCredentialPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", StaffCredentialPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", StaffCredentialPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", StaffCredentialPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", StaffCredentialPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", StaffCredentialPolicy.redactForLog("test-value"));
        assertEquals("se***ef", StaffCredentialPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", StaffCredentialPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", StaffCredentialPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", StaffCredentialPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", StaffCredentialPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", StaffCredentialPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", StaffCredentialPolicy.redactForLog("case-2026"));
        assertEquals("re***88", StaffCredentialPolicy.redactForLog("record-88"));
        assertEquals("ad***77", StaffCredentialPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", StaffCredentialPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", StaffCredentialPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", StaffCredentialPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", StaffCredentialPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", StaffCredentialPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", StaffCredentialPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", StaffCredentialPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", StaffCredentialPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", StaffCredentialPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", StaffCredentialPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", StaffCredentialPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", StaffCredentialPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", StaffCredentialPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", StaffCredentialPolicy.truncate("Access Review", 5));
        assertEquals("War…", StaffCredentialPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", StaffCredentialPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", StaffCredentialPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", StaffCredentialPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", StaffCredentialPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", StaffCredentialPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", StaffCredentialPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", StaffCredentialPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", StaffCredentialPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", StaffCredentialPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", StaffCredentialPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(StaffCredentialPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(StaffCredentialPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(StaffCredentialPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(StaffCredentialPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(StaffCredentialPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(StaffCredentialPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(StaffCredentialPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(StaffCredentialPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(StaffCredentialPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(StaffCredentialPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(null, null));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, StaffCredentialPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, StaffCredentialPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
