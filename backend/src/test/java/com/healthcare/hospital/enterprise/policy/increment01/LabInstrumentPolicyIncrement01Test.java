package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.LabInstrumentPolicy;

class LabInstrumentPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", LabInstrumentPolicy.normalizeStatus(null));
        assertEquals("NEW", LabInstrumentPolicy.normalizeStatus(""));
        assertEquals("NEW", LabInstrumentPolicy.normalizeStatus(" "));
        assertEquals("NEW", LabInstrumentPolicy.normalizeStatus("new"));
        assertEquals("NEW", LabInstrumentPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", LabInstrumentPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", LabInstrumentPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", LabInstrumentPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", LabInstrumentPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", LabInstrumentPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", LabInstrumentPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", LabInstrumentPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", LabInstrumentPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", LabInstrumentPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", LabInstrumentPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", LabInstrumentPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", LabInstrumentPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", LabInstrumentPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", LabInstrumentPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", LabInstrumentPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", LabInstrumentPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", LabInstrumentPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", LabInstrumentPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", LabInstrumentPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", LabInstrumentPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, LabInstrumentPolicy.pageSize(-100));
        assertEquals(25, LabInstrumentPolicy.pageSize(-1));
        assertEquals(25, LabInstrumentPolicy.pageSize(0));
        assertEquals(1, LabInstrumentPolicy.pageSize(1));
        assertEquals(2, LabInstrumentPolicy.pageSize(2));
        assertEquals(5, LabInstrumentPolicy.pageSize(5));
        assertEquals(10, LabInstrumentPolicy.pageSize(10));
        assertEquals(20, LabInstrumentPolicy.pageSize(20));
        assertEquals(24, LabInstrumentPolicy.pageSize(24));
        assertEquals(25, LabInstrumentPolicy.pageSize(25));
        assertEquals(26, LabInstrumentPolicy.pageSize(26));
        assertEquals(49, LabInstrumentPolicy.pageSize(49));
        assertEquals(50, LabInstrumentPolicy.pageSize(50));
        assertEquals(75, LabInstrumentPolicy.pageSize(75));
        assertEquals(99, LabInstrumentPolicy.pageSize(99));
        assertEquals(100, LabInstrumentPolicy.pageSize(100));
        assertEquals(101, LabInstrumentPolicy.pageSize(101));
        assertEquals(150, LabInstrumentPolicy.pageSize(150));
        assertEquals(199, LabInstrumentPolicy.pageSize(199));
        assertEquals(200, LabInstrumentPolicy.pageSize(200));
        assertEquals(200, LabInstrumentPolicy.pageSize(201));
        assertEquals(200, LabInstrumentPolicy.pageSize(250));
        assertEquals(200, LabInstrumentPolicy.pageSize(500));
        assertEquals(200, LabInstrumentPolicy.pageSize(1000));
        assertEquals(200, LabInstrumentPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, LabInstrumentPolicy.pageIndex(-100));
        assertEquals(0, LabInstrumentPolicy.pageIndex(-10));
        assertEquals(0, LabInstrumentPolicy.pageIndex(-2));
        assertEquals(0, LabInstrumentPolicy.pageIndex(-1));
        assertEquals(0, LabInstrumentPolicy.pageIndex(0));
        assertEquals(1, LabInstrumentPolicy.pageIndex(1));
        assertEquals(2, LabInstrumentPolicy.pageIndex(2));
        assertEquals(3, LabInstrumentPolicy.pageIndex(3));
        assertEquals(4, LabInstrumentPolicy.pageIndex(4));
        assertEquals(5, LabInstrumentPolicy.pageIndex(5));
        assertEquals(6, LabInstrumentPolicy.pageIndex(6));
        assertEquals(7, LabInstrumentPolicy.pageIndex(7));
        assertEquals(8, LabInstrumentPolicy.pageIndex(8));
        assertEquals(9, LabInstrumentPolicy.pageIndex(9));
        assertEquals(10, LabInstrumentPolicy.pageIndex(10));
        assertEquals(11, LabInstrumentPolicy.pageIndex(11));
        assertEquals(12, LabInstrumentPolicy.pageIndex(12));
        assertEquals(13, LabInstrumentPolicy.pageIndex(13));
        assertEquals(14, LabInstrumentPolicy.pageIndex(14));
        assertEquals(15, LabInstrumentPolicy.pageIndex(15));
        assertEquals(20, LabInstrumentPolicy.pageIndex(20));
        assertEquals(50, LabInstrumentPolicy.pageIndex(50));
        assertEquals(100, LabInstrumentPolicy.pageIndex(100));
        assertEquals(1000, LabInstrumentPolicy.pageIndex(1000));
        assertEquals(99999, LabInstrumentPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", LabInstrumentPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabInstrumentPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", LabInstrumentPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", LabInstrumentPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", LabInstrumentPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabInstrumentPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabInstrumentPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabInstrumentPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", LabInstrumentPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabInstrumentPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabInstrumentPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", LabInstrumentPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabInstrumentPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabInstrumentPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabInstrumentPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabInstrumentPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabInstrumentPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabInstrumentPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabInstrumentPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", LabInstrumentPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", LabInstrumentPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabInstrumentPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabInstrumentPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabInstrumentPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", LabInstrumentPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", LabInstrumentPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabInstrumentPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", LabInstrumentPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", LabInstrumentPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", LabInstrumentPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabInstrumentPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabInstrumentPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabInstrumentPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", LabInstrumentPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabInstrumentPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabInstrumentPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", LabInstrumentPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabInstrumentPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabInstrumentPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabInstrumentPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabInstrumentPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabInstrumentPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabInstrumentPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabInstrumentPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", LabInstrumentPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", LabInstrumentPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabInstrumentPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabInstrumentPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabInstrumentPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", LabInstrumentPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", LabInstrumentPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", LabInstrumentPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", LabInstrumentPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", LabInstrumentPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", LabInstrumentPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", LabInstrumentPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", LabInstrumentPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", LabInstrumentPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", LabInstrumentPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", LabInstrumentPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", LabInstrumentPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", LabInstrumentPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", LabInstrumentPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", LabInstrumentPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", LabInstrumentPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", LabInstrumentPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", LabInstrumentPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", LabInstrumentPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", LabInstrumentPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", LabInstrumentPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", LabInstrumentPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", LabInstrumentPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", LabInstrumentPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", LabInstrumentPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", LabInstrumentPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), LabInstrumentPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, LabInstrumentPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, LabInstrumentPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, LabInstrumentPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", LabInstrumentPolicy.normalizePriority("critical"));
        assertEquals("URGENT", LabInstrumentPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", LabInstrumentPolicy.normalizePriority("high"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("normal"));
        assertEquals("LOW", LabInstrumentPolicy.normalizePriority("low"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority(null));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority(""));
        assertEquals("CRITICAL", LabInstrumentPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", LabInstrumentPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", LabInstrumentPolicy.normalizePriority("low "));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", LabInstrumentPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("none"));
        assertEquals("NORMAL", LabInstrumentPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, LabInstrumentPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", LabInstrumentPolicy.redactForLog(""));
        assertEquals("****", LabInstrumentPolicy.redactForLog("a"));
        assertEquals("****", LabInstrumentPolicy.redactForLog("ab"));
        assertEquals("****", LabInstrumentPolicy.redactForLog("abc"));
        assertEquals("****", LabInstrumentPolicy.redactForLog("abcd"));
        assertEquals("ab***de", LabInstrumentPolicy.redactForLog("abcde"));
        assertEquals("pa***23", LabInstrumentPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", LabInstrumentPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", LabInstrumentPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", LabInstrumentPolicy.redactForLog("Rahul"));
        assertEquals("do***01", LabInstrumentPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", LabInstrumentPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", LabInstrumentPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", LabInstrumentPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", LabInstrumentPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", LabInstrumentPolicy.redactForLog("test-value"));
        assertEquals("se***ef", LabInstrumentPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", LabInstrumentPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", LabInstrumentPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", LabInstrumentPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", LabInstrumentPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", LabInstrumentPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", LabInstrumentPolicy.redactForLog("case-2026"));
        assertEquals("re***88", LabInstrumentPolicy.redactForLog("record-88"));
        assertEquals("ad***77", LabInstrumentPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", LabInstrumentPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", LabInstrumentPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", LabInstrumentPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", LabInstrumentPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", LabInstrumentPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", LabInstrumentPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", LabInstrumentPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", LabInstrumentPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", LabInstrumentPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", LabInstrumentPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", LabInstrumentPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", LabInstrumentPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", LabInstrumentPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", LabInstrumentPolicy.truncate("Access Review", 5));
        assertEquals("War…", LabInstrumentPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", LabInstrumentPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", LabInstrumentPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", LabInstrumentPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", LabInstrumentPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", LabInstrumentPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", LabInstrumentPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", LabInstrumentPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", LabInstrumentPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", LabInstrumentPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", LabInstrumentPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(LabInstrumentPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(LabInstrumentPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(LabInstrumentPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(LabInstrumentPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(LabInstrumentPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(LabInstrumentPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(LabInstrumentPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(LabInstrumentPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(LabInstrumentPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(LabInstrumentPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(null, null));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, LabInstrumentPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, LabInstrumentPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
