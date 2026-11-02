package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.IncidentReportPolicy;

class IncidentReportPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", IncidentReportPolicy.normalizeStatus(null));
        assertEquals("NEW", IncidentReportPolicy.normalizeStatus(""));
        assertEquals("NEW", IncidentReportPolicy.normalizeStatus(" "));
        assertEquals("NEW", IncidentReportPolicy.normalizeStatus("new"));
        assertEquals("NEW", IncidentReportPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", IncidentReportPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", IncidentReportPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", IncidentReportPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", IncidentReportPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", IncidentReportPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", IncidentReportPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", IncidentReportPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", IncidentReportPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", IncidentReportPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", IncidentReportPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", IncidentReportPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", IncidentReportPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", IncidentReportPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", IncidentReportPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", IncidentReportPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", IncidentReportPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", IncidentReportPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", IncidentReportPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", IncidentReportPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", IncidentReportPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, IncidentReportPolicy.pageSize(-100));
        assertEquals(25, IncidentReportPolicy.pageSize(-1));
        assertEquals(25, IncidentReportPolicy.pageSize(0));
        assertEquals(1, IncidentReportPolicy.pageSize(1));
        assertEquals(2, IncidentReportPolicy.pageSize(2));
        assertEquals(5, IncidentReportPolicy.pageSize(5));
        assertEquals(10, IncidentReportPolicy.pageSize(10));
        assertEquals(20, IncidentReportPolicy.pageSize(20));
        assertEquals(24, IncidentReportPolicy.pageSize(24));
        assertEquals(25, IncidentReportPolicy.pageSize(25));
        assertEquals(26, IncidentReportPolicy.pageSize(26));
        assertEquals(49, IncidentReportPolicy.pageSize(49));
        assertEquals(50, IncidentReportPolicy.pageSize(50));
        assertEquals(75, IncidentReportPolicy.pageSize(75));
        assertEquals(99, IncidentReportPolicy.pageSize(99));
        assertEquals(100, IncidentReportPolicy.pageSize(100));
        assertEquals(101, IncidentReportPolicy.pageSize(101));
        assertEquals(150, IncidentReportPolicy.pageSize(150));
        assertEquals(199, IncidentReportPolicy.pageSize(199));
        assertEquals(200, IncidentReportPolicy.pageSize(200));
        assertEquals(200, IncidentReportPolicy.pageSize(201));
        assertEquals(200, IncidentReportPolicy.pageSize(250));
        assertEquals(200, IncidentReportPolicy.pageSize(500));
        assertEquals(200, IncidentReportPolicy.pageSize(1000));
        assertEquals(200, IncidentReportPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, IncidentReportPolicy.pageIndex(-100));
        assertEquals(0, IncidentReportPolicy.pageIndex(-10));
        assertEquals(0, IncidentReportPolicy.pageIndex(-2));
        assertEquals(0, IncidentReportPolicy.pageIndex(-1));
        assertEquals(0, IncidentReportPolicy.pageIndex(0));
        assertEquals(1, IncidentReportPolicy.pageIndex(1));
        assertEquals(2, IncidentReportPolicy.pageIndex(2));
        assertEquals(3, IncidentReportPolicy.pageIndex(3));
        assertEquals(4, IncidentReportPolicy.pageIndex(4));
        assertEquals(5, IncidentReportPolicy.pageIndex(5));
        assertEquals(6, IncidentReportPolicy.pageIndex(6));
        assertEquals(7, IncidentReportPolicy.pageIndex(7));
        assertEquals(8, IncidentReportPolicy.pageIndex(8));
        assertEquals(9, IncidentReportPolicy.pageIndex(9));
        assertEquals(10, IncidentReportPolicy.pageIndex(10));
        assertEquals(11, IncidentReportPolicy.pageIndex(11));
        assertEquals(12, IncidentReportPolicy.pageIndex(12));
        assertEquals(13, IncidentReportPolicy.pageIndex(13));
        assertEquals(14, IncidentReportPolicy.pageIndex(14));
        assertEquals(15, IncidentReportPolicy.pageIndex(15));
        assertEquals(20, IncidentReportPolicy.pageIndex(20));
        assertEquals(50, IncidentReportPolicy.pageIndex(50));
        assertEquals(100, IncidentReportPolicy.pageIndex(100));
        assertEquals(1000, IncidentReportPolicy.pageIndex(1000));
        assertEquals(99999, IncidentReportPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", IncidentReportPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", IncidentReportPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", IncidentReportPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", IncidentReportPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", IncidentReportPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", IncidentReportPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", IncidentReportPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", IncidentReportPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", IncidentReportPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", IncidentReportPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", IncidentReportPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", IncidentReportPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", IncidentReportPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", IncidentReportPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", IncidentReportPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", IncidentReportPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", IncidentReportPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", IncidentReportPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", IncidentReportPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", IncidentReportPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", IncidentReportPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", IncidentReportPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", IncidentReportPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", IncidentReportPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", IncidentReportPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", IncidentReportPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", IncidentReportPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", IncidentReportPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", IncidentReportPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", IncidentReportPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", IncidentReportPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", IncidentReportPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", IncidentReportPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", IncidentReportPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", IncidentReportPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", IncidentReportPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", IncidentReportPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", IncidentReportPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", IncidentReportPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", IncidentReportPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", IncidentReportPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", IncidentReportPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", IncidentReportPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", IncidentReportPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", IncidentReportPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", IncidentReportPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", IncidentReportPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", IncidentReportPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", IncidentReportPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", IncidentReportPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", IncidentReportPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", IncidentReportPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", IncidentReportPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", IncidentReportPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", IncidentReportPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", IncidentReportPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", IncidentReportPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", IncidentReportPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", IncidentReportPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", IncidentReportPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", IncidentReportPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", IncidentReportPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", IncidentReportPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", IncidentReportPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", IncidentReportPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", IncidentReportPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", IncidentReportPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", IncidentReportPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", IncidentReportPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", IncidentReportPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", IncidentReportPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", IncidentReportPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", IncidentReportPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", IncidentReportPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", IncidentReportPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), IncidentReportPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, IncidentReportPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, IncidentReportPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, IncidentReportPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", IncidentReportPolicy.normalizePriority("critical"));
        assertEquals("URGENT", IncidentReportPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", IncidentReportPolicy.normalizePriority("high"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("normal"));
        assertEquals("LOW", IncidentReportPolicy.normalizePriority("low"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority(null));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority(""));
        assertEquals("CRITICAL", IncidentReportPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", IncidentReportPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", IncidentReportPolicy.normalizePriority("low "));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", IncidentReportPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("none"));
        assertEquals("NORMAL", IncidentReportPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, IncidentReportPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", IncidentReportPolicy.redactForLog(""));
        assertEquals("****", IncidentReportPolicy.redactForLog("a"));
        assertEquals("****", IncidentReportPolicy.redactForLog("ab"));
        assertEquals("****", IncidentReportPolicy.redactForLog("abc"));
        assertEquals("****", IncidentReportPolicy.redactForLog("abcd"));
        assertEquals("ab***de", IncidentReportPolicy.redactForLog("abcde"));
        assertEquals("pa***23", IncidentReportPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", IncidentReportPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", IncidentReportPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", IncidentReportPolicy.redactForLog("Rahul"));
        assertEquals("do***01", IncidentReportPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", IncidentReportPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", IncidentReportPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", IncidentReportPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", IncidentReportPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", IncidentReportPolicy.redactForLog("test-value"));
        assertEquals("se***ef", IncidentReportPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", IncidentReportPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", IncidentReportPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", IncidentReportPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", IncidentReportPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", IncidentReportPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", IncidentReportPolicy.redactForLog("case-2026"));
        assertEquals("re***88", IncidentReportPolicy.redactForLog("record-88"));
        assertEquals("ad***77", IncidentReportPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", IncidentReportPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", IncidentReportPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", IncidentReportPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", IncidentReportPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", IncidentReportPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", IncidentReportPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", IncidentReportPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", IncidentReportPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", IncidentReportPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", IncidentReportPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", IncidentReportPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", IncidentReportPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", IncidentReportPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", IncidentReportPolicy.truncate("Access Review", 5));
        assertEquals("War…", IncidentReportPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", IncidentReportPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", IncidentReportPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", IncidentReportPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", IncidentReportPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", IncidentReportPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", IncidentReportPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", IncidentReportPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", IncidentReportPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", IncidentReportPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", IncidentReportPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(IncidentReportPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(IncidentReportPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(IncidentReportPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(IncidentReportPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(IncidentReportPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(IncidentReportPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(IncidentReportPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(IncidentReportPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(IncidentReportPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(IncidentReportPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(IncidentReportPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(IncidentReportPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(IncidentReportPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(IncidentReportPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(IncidentReportPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(IncidentReportPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(IncidentReportPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(IncidentReportPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, IncidentReportPolicy.requiresApproval(null, null));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, IncidentReportPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, IncidentReportPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
