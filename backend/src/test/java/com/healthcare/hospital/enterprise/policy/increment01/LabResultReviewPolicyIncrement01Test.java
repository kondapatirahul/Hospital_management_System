package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.LabResultReviewPolicy;

class LabResultReviewPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", LabResultReviewPolicy.normalizeStatus(null));
        assertEquals("NEW", LabResultReviewPolicy.normalizeStatus(""));
        assertEquals("NEW", LabResultReviewPolicy.normalizeStatus(" "));
        assertEquals("NEW", LabResultReviewPolicy.normalizeStatus("new"));
        assertEquals("NEW", LabResultReviewPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", LabResultReviewPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", LabResultReviewPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", LabResultReviewPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", LabResultReviewPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", LabResultReviewPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", LabResultReviewPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", LabResultReviewPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", LabResultReviewPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", LabResultReviewPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", LabResultReviewPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", LabResultReviewPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", LabResultReviewPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", LabResultReviewPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", LabResultReviewPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", LabResultReviewPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", LabResultReviewPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", LabResultReviewPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", LabResultReviewPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", LabResultReviewPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", LabResultReviewPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, LabResultReviewPolicy.pageSize(-100));
        assertEquals(25, LabResultReviewPolicy.pageSize(-1));
        assertEquals(25, LabResultReviewPolicy.pageSize(0));
        assertEquals(1, LabResultReviewPolicy.pageSize(1));
        assertEquals(2, LabResultReviewPolicy.pageSize(2));
        assertEquals(5, LabResultReviewPolicy.pageSize(5));
        assertEquals(10, LabResultReviewPolicy.pageSize(10));
        assertEquals(20, LabResultReviewPolicy.pageSize(20));
        assertEquals(24, LabResultReviewPolicy.pageSize(24));
        assertEquals(25, LabResultReviewPolicy.pageSize(25));
        assertEquals(26, LabResultReviewPolicy.pageSize(26));
        assertEquals(49, LabResultReviewPolicy.pageSize(49));
        assertEquals(50, LabResultReviewPolicy.pageSize(50));
        assertEquals(75, LabResultReviewPolicy.pageSize(75));
        assertEquals(99, LabResultReviewPolicy.pageSize(99));
        assertEquals(100, LabResultReviewPolicy.pageSize(100));
        assertEquals(101, LabResultReviewPolicy.pageSize(101));
        assertEquals(150, LabResultReviewPolicy.pageSize(150));
        assertEquals(199, LabResultReviewPolicy.pageSize(199));
        assertEquals(200, LabResultReviewPolicy.pageSize(200));
        assertEquals(200, LabResultReviewPolicy.pageSize(201));
        assertEquals(200, LabResultReviewPolicy.pageSize(250));
        assertEquals(200, LabResultReviewPolicy.pageSize(500));
        assertEquals(200, LabResultReviewPolicy.pageSize(1000));
        assertEquals(200, LabResultReviewPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, LabResultReviewPolicy.pageIndex(-100));
        assertEquals(0, LabResultReviewPolicy.pageIndex(-10));
        assertEquals(0, LabResultReviewPolicy.pageIndex(-2));
        assertEquals(0, LabResultReviewPolicy.pageIndex(-1));
        assertEquals(0, LabResultReviewPolicy.pageIndex(0));
        assertEquals(1, LabResultReviewPolicy.pageIndex(1));
        assertEquals(2, LabResultReviewPolicy.pageIndex(2));
        assertEquals(3, LabResultReviewPolicy.pageIndex(3));
        assertEquals(4, LabResultReviewPolicy.pageIndex(4));
        assertEquals(5, LabResultReviewPolicy.pageIndex(5));
        assertEquals(6, LabResultReviewPolicy.pageIndex(6));
        assertEquals(7, LabResultReviewPolicy.pageIndex(7));
        assertEquals(8, LabResultReviewPolicy.pageIndex(8));
        assertEquals(9, LabResultReviewPolicy.pageIndex(9));
        assertEquals(10, LabResultReviewPolicy.pageIndex(10));
        assertEquals(11, LabResultReviewPolicy.pageIndex(11));
        assertEquals(12, LabResultReviewPolicy.pageIndex(12));
        assertEquals(13, LabResultReviewPolicy.pageIndex(13));
        assertEquals(14, LabResultReviewPolicy.pageIndex(14));
        assertEquals(15, LabResultReviewPolicy.pageIndex(15));
        assertEquals(20, LabResultReviewPolicy.pageIndex(20));
        assertEquals(50, LabResultReviewPolicy.pageIndex(50));
        assertEquals(100, LabResultReviewPolicy.pageIndex(100));
        assertEquals(1000, LabResultReviewPolicy.pageIndex(1000));
        assertEquals(99999, LabResultReviewPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", LabResultReviewPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabResultReviewPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", LabResultReviewPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", LabResultReviewPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", LabResultReviewPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabResultReviewPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabResultReviewPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabResultReviewPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", LabResultReviewPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabResultReviewPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabResultReviewPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", LabResultReviewPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabResultReviewPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabResultReviewPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabResultReviewPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabResultReviewPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabResultReviewPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabResultReviewPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabResultReviewPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", LabResultReviewPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", LabResultReviewPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabResultReviewPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabResultReviewPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabResultReviewPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", LabResultReviewPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", LabResultReviewPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabResultReviewPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", LabResultReviewPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", LabResultReviewPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", LabResultReviewPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabResultReviewPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabResultReviewPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabResultReviewPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", LabResultReviewPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabResultReviewPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabResultReviewPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", LabResultReviewPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabResultReviewPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabResultReviewPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabResultReviewPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabResultReviewPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabResultReviewPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabResultReviewPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabResultReviewPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", LabResultReviewPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", LabResultReviewPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabResultReviewPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabResultReviewPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabResultReviewPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", LabResultReviewPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", LabResultReviewPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", LabResultReviewPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", LabResultReviewPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", LabResultReviewPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", LabResultReviewPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", LabResultReviewPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", LabResultReviewPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", LabResultReviewPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", LabResultReviewPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", LabResultReviewPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", LabResultReviewPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", LabResultReviewPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", LabResultReviewPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", LabResultReviewPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", LabResultReviewPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", LabResultReviewPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", LabResultReviewPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", LabResultReviewPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", LabResultReviewPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", LabResultReviewPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", LabResultReviewPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", LabResultReviewPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", LabResultReviewPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", LabResultReviewPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", LabResultReviewPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), LabResultReviewPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, LabResultReviewPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, LabResultReviewPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, LabResultReviewPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", LabResultReviewPolicy.normalizePriority("critical"));
        assertEquals("URGENT", LabResultReviewPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", LabResultReviewPolicy.normalizePriority("high"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("normal"));
        assertEquals("LOW", LabResultReviewPolicy.normalizePriority("low"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority(null));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority(""));
        assertEquals("CRITICAL", LabResultReviewPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", LabResultReviewPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", LabResultReviewPolicy.normalizePriority("low "));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", LabResultReviewPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("none"));
        assertEquals("NORMAL", LabResultReviewPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, LabResultReviewPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", LabResultReviewPolicy.redactForLog(""));
        assertEquals("****", LabResultReviewPolicy.redactForLog("a"));
        assertEquals("****", LabResultReviewPolicy.redactForLog("ab"));
        assertEquals("****", LabResultReviewPolicy.redactForLog("abc"));
        assertEquals("****", LabResultReviewPolicy.redactForLog("abcd"));
        assertEquals("ab***de", LabResultReviewPolicy.redactForLog("abcde"));
        assertEquals("pa***23", LabResultReviewPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", LabResultReviewPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", LabResultReviewPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", LabResultReviewPolicy.redactForLog("Rahul"));
        assertEquals("do***01", LabResultReviewPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", LabResultReviewPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", LabResultReviewPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", LabResultReviewPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", LabResultReviewPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", LabResultReviewPolicy.redactForLog("test-value"));
        assertEquals("se***ef", LabResultReviewPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", LabResultReviewPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", LabResultReviewPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", LabResultReviewPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", LabResultReviewPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", LabResultReviewPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", LabResultReviewPolicy.redactForLog("case-2026"));
        assertEquals("re***88", LabResultReviewPolicy.redactForLog("record-88"));
        assertEquals("ad***77", LabResultReviewPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", LabResultReviewPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", LabResultReviewPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", LabResultReviewPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", LabResultReviewPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", LabResultReviewPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", LabResultReviewPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", LabResultReviewPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", LabResultReviewPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", LabResultReviewPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", LabResultReviewPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", LabResultReviewPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", LabResultReviewPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", LabResultReviewPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", LabResultReviewPolicy.truncate("Access Review", 5));
        assertEquals("War…", LabResultReviewPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", LabResultReviewPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", LabResultReviewPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", LabResultReviewPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", LabResultReviewPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", LabResultReviewPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", LabResultReviewPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", LabResultReviewPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", LabResultReviewPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", LabResultReviewPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", LabResultReviewPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(LabResultReviewPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(LabResultReviewPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(LabResultReviewPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(LabResultReviewPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(LabResultReviewPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(LabResultReviewPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(LabResultReviewPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(LabResultReviewPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(LabResultReviewPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(LabResultReviewPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(null, null));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, LabResultReviewPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, LabResultReviewPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
