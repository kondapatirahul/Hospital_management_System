package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.DocumentRegistryPolicy;

class DocumentRegistryPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", DocumentRegistryPolicy.normalizeStatus(null));
        assertEquals("NEW", DocumentRegistryPolicy.normalizeStatus(""));
        assertEquals("NEW", DocumentRegistryPolicy.normalizeStatus(" "));
        assertEquals("NEW", DocumentRegistryPolicy.normalizeStatus("new"));
        assertEquals("NEW", DocumentRegistryPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", DocumentRegistryPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", DocumentRegistryPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", DocumentRegistryPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", DocumentRegistryPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", DocumentRegistryPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", DocumentRegistryPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", DocumentRegistryPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", DocumentRegistryPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", DocumentRegistryPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", DocumentRegistryPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", DocumentRegistryPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", DocumentRegistryPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", DocumentRegistryPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", DocumentRegistryPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", DocumentRegistryPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", DocumentRegistryPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", DocumentRegistryPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", DocumentRegistryPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", DocumentRegistryPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", DocumentRegistryPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, DocumentRegistryPolicy.pageSize(-100));
        assertEquals(25, DocumentRegistryPolicy.pageSize(-1));
        assertEquals(25, DocumentRegistryPolicy.pageSize(0));
        assertEquals(1, DocumentRegistryPolicy.pageSize(1));
        assertEquals(2, DocumentRegistryPolicy.pageSize(2));
        assertEquals(5, DocumentRegistryPolicy.pageSize(5));
        assertEquals(10, DocumentRegistryPolicy.pageSize(10));
        assertEquals(20, DocumentRegistryPolicy.pageSize(20));
        assertEquals(24, DocumentRegistryPolicy.pageSize(24));
        assertEquals(25, DocumentRegistryPolicy.pageSize(25));
        assertEquals(26, DocumentRegistryPolicy.pageSize(26));
        assertEquals(49, DocumentRegistryPolicy.pageSize(49));
        assertEquals(50, DocumentRegistryPolicy.pageSize(50));
        assertEquals(75, DocumentRegistryPolicy.pageSize(75));
        assertEquals(99, DocumentRegistryPolicy.pageSize(99));
        assertEquals(100, DocumentRegistryPolicy.pageSize(100));
        assertEquals(101, DocumentRegistryPolicy.pageSize(101));
        assertEquals(150, DocumentRegistryPolicy.pageSize(150));
        assertEquals(199, DocumentRegistryPolicy.pageSize(199));
        assertEquals(200, DocumentRegistryPolicy.pageSize(200));
        assertEquals(200, DocumentRegistryPolicy.pageSize(201));
        assertEquals(200, DocumentRegistryPolicy.pageSize(250));
        assertEquals(200, DocumentRegistryPolicy.pageSize(500));
        assertEquals(200, DocumentRegistryPolicy.pageSize(1000));
        assertEquals(200, DocumentRegistryPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, DocumentRegistryPolicy.pageIndex(-100));
        assertEquals(0, DocumentRegistryPolicy.pageIndex(-10));
        assertEquals(0, DocumentRegistryPolicy.pageIndex(-2));
        assertEquals(0, DocumentRegistryPolicy.pageIndex(-1));
        assertEquals(0, DocumentRegistryPolicy.pageIndex(0));
        assertEquals(1, DocumentRegistryPolicy.pageIndex(1));
        assertEquals(2, DocumentRegistryPolicy.pageIndex(2));
        assertEquals(3, DocumentRegistryPolicy.pageIndex(3));
        assertEquals(4, DocumentRegistryPolicy.pageIndex(4));
        assertEquals(5, DocumentRegistryPolicy.pageIndex(5));
        assertEquals(6, DocumentRegistryPolicy.pageIndex(6));
        assertEquals(7, DocumentRegistryPolicy.pageIndex(7));
        assertEquals(8, DocumentRegistryPolicy.pageIndex(8));
        assertEquals(9, DocumentRegistryPolicy.pageIndex(9));
        assertEquals(10, DocumentRegistryPolicy.pageIndex(10));
        assertEquals(11, DocumentRegistryPolicy.pageIndex(11));
        assertEquals(12, DocumentRegistryPolicy.pageIndex(12));
        assertEquals(13, DocumentRegistryPolicy.pageIndex(13));
        assertEquals(14, DocumentRegistryPolicy.pageIndex(14));
        assertEquals(15, DocumentRegistryPolicy.pageIndex(15));
        assertEquals(20, DocumentRegistryPolicy.pageIndex(20));
        assertEquals(50, DocumentRegistryPolicy.pageIndex(50));
        assertEquals(100, DocumentRegistryPolicy.pageIndex(100));
        assertEquals(1000, DocumentRegistryPolicy.pageIndex(1000));
        assertEquals(99999, DocumentRegistryPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", DocumentRegistryPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", DocumentRegistryPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", DocumentRegistryPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", DocumentRegistryPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", DocumentRegistryPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", DocumentRegistryPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", DocumentRegistryPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", DocumentRegistryPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", DocumentRegistryPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", DocumentRegistryPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", DocumentRegistryPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", DocumentRegistryPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", DocumentRegistryPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", DocumentRegistryPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", DocumentRegistryPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", DocumentRegistryPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", DocumentRegistryPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", DocumentRegistryPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", DocumentRegistryPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", DocumentRegistryPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", DocumentRegistryPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", DocumentRegistryPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", DocumentRegistryPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", DocumentRegistryPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", DocumentRegistryPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", DocumentRegistryPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", DocumentRegistryPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", DocumentRegistryPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", DocumentRegistryPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", DocumentRegistryPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", DocumentRegistryPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", DocumentRegistryPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", DocumentRegistryPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", DocumentRegistryPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", DocumentRegistryPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", DocumentRegistryPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", DocumentRegistryPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", DocumentRegistryPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", DocumentRegistryPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", DocumentRegistryPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", DocumentRegistryPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", DocumentRegistryPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", DocumentRegistryPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", DocumentRegistryPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", DocumentRegistryPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", DocumentRegistryPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", DocumentRegistryPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", DocumentRegistryPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", DocumentRegistryPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", DocumentRegistryPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", DocumentRegistryPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", DocumentRegistryPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", DocumentRegistryPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", DocumentRegistryPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", DocumentRegistryPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", DocumentRegistryPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", DocumentRegistryPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", DocumentRegistryPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", DocumentRegistryPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", DocumentRegistryPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", DocumentRegistryPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", DocumentRegistryPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", DocumentRegistryPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", DocumentRegistryPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", DocumentRegistryPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", DocumentRegistryPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", DocumentRegistryPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", DocumentRegistryPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", DocumentRegistryPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", DocumentRegistryPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", DocumentRegistryPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", DocumentRegistryPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", DocumentRegistryPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", DocumentRegistryPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", DocumentRegistryPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), DocumentRegistryPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, DocumentRegistryPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, DocumentRegistryPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, DocumentRegistryPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", DocumentRegistryPolicy.normalizePriority("critical"));
        assertEquals("URGENT", DocumentRegistryPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", DocumentRegistryPolicy.normalizePriority("high"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("normal"));
        assertEquals("LOW", DocumentRegistryPolicy.normalizePriority("low"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority(null));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority(""));
        assertEquals("CRITICAL", DocumentRegistryPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", DocumentRegistryPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", DocumentRegistryPolicy.normalizePriority("low "));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", DocumentRegistryPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("none"));
        assertEquals("NORMAL", DocumentRegistryPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, DocumentRegistryPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", DocumentRegistryPolicy.redactForLog(""));
        assertEquals("****", DocumentRegistryPolicy.redactForLog("a"));
        assertEquals("****", DocumentRegistryPolicy.redactForLog("ab"));
        assertEquals("****", DocumentRegistryPolicy.redactForLog("abc"));
        assertEquals("****", DocumentRegistryPolicy.redactForLog("abcd"));
        assertEquals("ab***de", DocumentRegistryPolicy.redactForLog("abcde"));
        assertEquals("pa***23", DocumentRegistryPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", DocumentRegistryPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", DocumentRegistryPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", DocumentRegistryPolicy.redactForLog("Rahul"));
        assertEquals("do***01", DocumentRegistryPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", DocumentRegistryPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", DocumentRegistryPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", DocumentRegistryPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", DocumentRegistryPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", DocumentRegistryPolicy.redactForLog("test-value"));
        assertEquals("se***ef", DocumentRegistryPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", DocumentRegistryPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", DocumentRegistryPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", DocumentRegistryPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", DocumentRegistryPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", DocumentRegistryPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", DocumentRegistryPolicy.redactForLog("case-2026"));
        assertEquals("re***88", DocumentRegistryPolicy.redactForLog("record-88"));
        assertEquals("ad***77", DocumentRegistryPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", DocumentRegistryPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", DocumentRegistryPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", DocumentRegistryPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", DocumentRegistryPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", DocumentRegistryPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", DocumentRegistryPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", DocumentRegistryPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", DocumentRegistryPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", DocumentRegistryPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", DocumentRegistryPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", DocumentRegistryPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", DocumentRegistryPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", DocumentRegistryPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", DocumentRegistryPolicy.truncate("Access Review", 5));
        assertEquals("War…", DocumentRegistryPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", DocumentRegistryPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", DocumentRegistryPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", DocumentRegistryPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", DocumentRegistryPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", DocumentRegistryPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", DocumentRegistryPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", DocumentRegistryPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", DocumentRegistryPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", DocumentRegistryPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", DocumentRegistryPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(DocumentRegistryPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(DocumentRegistryPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(null, null));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, DocumentRegistryPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, DocumentRegistryPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
