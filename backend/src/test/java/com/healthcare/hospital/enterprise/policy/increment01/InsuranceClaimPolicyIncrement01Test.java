package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.InsuranceClaimPolicy;

class InsuranceClaimPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", InsuranceClaimPolicy.normalizeStatus(null));
        assertEquals("NEW", InsuranceClaimPolicy.normalizeStatus(""));
        assertEquals("NEW", InsuranceClaimPolicy.normalizeStatus(" "));
        assertEquals("NEW", InsuranceClaimPolicy.normalizeStatus("new"));
        assertEquals("NEW", InsuranceClaimPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", InsuranceClaimPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", InsuranceClaimPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", InsuranceClaimPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", InsuranceClaimPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", InsuranceClaimPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", InsuranceClaimPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", InsuranceClaimPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", InsuranceClaimPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", InsuranceClaimPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", InsuranceClaimPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", InsuranceClaimPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", InsuranceClaimPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", InsuranceClaimPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", InsuranceClaimPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", InsuranceClaimPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", InsuranceClaimPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", InsuranceClaimPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", InsuranceClaimPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", InsuranceClaimPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", InsuranceClaimPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, InsuranceClaimPolicy.pageSize(-100));
        assertEquals(25, InsuranceClaimPolicy.pageSize(-1));
        assertEquals(25, InsuranceClaimPolicy.pageSize(0));
        assertEquals(1, InsuranceClaimPolicy.pageSize(1));
        assertEquals(2, InsuranceClaimPolicy.pageSize(2));
        assertEquals(5, InsuranceClaimPolicy.pageSize(5));
        assertEquals(10, InsuranceClaimPolicy.pageSize(10));
        assertEquals(20, InsuranceClaimPolicy.pageSize(20));
        assertEquals(24, InsuranceClaimPolicy.pageSize(24));
        assertEquals(25, InsuranceClaimPolicy.pageSize(25));
        assertEquals(26, InsuranceClaimPolicy.pageSize(26));
        assertEquals(49, InsuranceClaimPolicy.pageSize(49));
        assertEquals(50, InsuranceClaimPolicy.pageSize(50));
        assertEquals(75, InsuranceClaimPolicy.pageSize(75));
        assertEquals(99, InsuranceClaimPolicy.pageSize(99));
        assertEquals(100, InsuranceClaimPolicy.pageSize(100));
        assertEquals(101, InsuranceClaimPolicy.pageSize(101));
        assertEquals(150, InsuranceClaimPolicy.pageSize(150));
        assertEquals(199, InsuranceClaimPolicy.pageSize(199));
        assertEquals(200, InsuranceClaimPolicy.pageSize(200));
        assertEquals(200, InsuranceClaimPolicy.pageSize(201));
        assertEquals(200, InsuranceClaimPolicy.pageSize(250));
        assertEquals(200, InsuranceClaimPolicy.pageSize(500));
        assertEquals(200, InsuranceClaimPolicy.pageSize(1000));
        assertEquals(200, InsuranceClaimPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, InsuranceClaimPolicy.pageIndex(-100));
        assertEquals(0, InsuranceClaimPolicy.pageIndex(-10));
        assertEquals(0, InsuranceClaimPolicy.pageIndex(-2));
        assertEquals(0, InsuranceClaimPolicy.pageIndex(-1));
        assertEquals(0, InsuranceClaimPolicy.pageIndex(0));
        assertEquals(1, InsuranceClaimPolicy.pageIndex(1));
        assertEquals(2, InsuranceClaimPolicy.pageIndex(2));
        assertEquals(3, InsuranceClaimPolicy.pageIndex(3));
        assertEquals(4, InsuranceClaimPolicy.pageIndex(4));
        assertEquals(5, InsuranceClaimPolicy.pageIndex(5));
        assertEquals(6, InsuranceClaimPolicy.pageIndex(6));
        assertEquals(7, InsuranceClaimPolicy.pageIndex(7));
        assertEquals(8, InsuranceClaimPolicy.pageIndex(8));
        assertEquals(9, InsuranceClaimPolicy.pageIndex(9));
        assertEquals(10, InsuranceClaimPolicy.pageIndex(10));
        assertEquals(11, InsuranceClaimPolicy.pageIndex(11));
        assertEquals(12, InsuranceClaimPolicy.pageIndex(12));
        assertEquals(13, InsuranceClaimPolicy.pageIndex(13));
        assertEquals(14, InsuranceClaimPolicy.pageIndex(14));
        assertEquals(15, InsuranceClaimPolicy.pageIndex(15));
        assertEquals(20, InsuranceClaimPolicy.pageIndex(20));
        assertEquals(50, InsuranceClaimPolicy.pageIndex(50));
        assertEquals(100, InsuranceClaimPolicy.pageIndex(100));
        assertEquals(1000, InsuranceClaimPolicy.pageIndex(1000));
        assertEquals(99999, InsuranceClaimPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", InsuranceClaimPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", InsuranceClaimPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", InsuranceClaimPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", InsuranceClaimPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", InsuranceClaimPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", InsuranceClaimPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", InsuranceClaimPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", InsuranceClaimPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", InsuranceClaimPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", InsuranceClaimPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", InsuranceClaimPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", InsuranceClaimPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", InsuranceClaimPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", InsuranceClaimPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", InsuranceClaimPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", InsuranceClaimPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", InsuranceClaimPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", InsuranceClaimPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", InsuranceClaimPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", InsuranceClaimPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", InsuranceClaimPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", InsuranceClaimPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", InsuranceClaimPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", InsuranceClaimPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", InsuranceClaimPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", InsuranceClaimPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", InsuranceClaimPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", InsuranceClaimPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", InsuranceClaimPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", InsuranceClaimPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", InsuranceClaimPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", InsuranceClaimPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", InsuranceClaimPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", InsuranceClaimPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", InsuranceClaimPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", InsuranceClaimPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", InsuranceClaimPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", InsuranceClaimPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", InsuranceClaimPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", InsuranceClaimPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", InsuranceClaimPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", InsuranceClaimPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", InsuranceClaimPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", InsuranceClaimPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", InsuranceClaimPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", InsuranceClaimPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", InsuranceClaimPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", InsuranceClaimPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", InsuranceClaimPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", InsuranceClaimPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", InsuranceClaimPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", InsuranceClaimPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", InsuranceClaimPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", InsuranceClaimPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", InsuranceClaimPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", InsuranceClaimPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", InsuranceClaimPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", InsuranceClaimPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", InsuranceClaimPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", InsuranceClaimPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", InsuranceClaimPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", InsuranceClaimPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", InsuranceClaimPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", InsuranceClaimPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", InsuranceClaimPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", InsuranceClaimPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", InsuranceClaimPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", InsuranceClaimPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", InsuranceClaimPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", InsuranceClaimPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", InsuranceClaimPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", InsuranceClaimPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", InsuranceClaimPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", InsuranceClaimPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", InsuranceClaimPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), InsuranceClaimPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, InsuranceClaimPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, InsuranceClaimPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, InsuranceClaimPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", InsuranceClaimPolicy.normalizePriority("critical"));
        assertEquals("URGENT", InsuranceClaimPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", InsuranceClaimPolicy.normalizePriority("high"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("normal"));
        assertEquals("LOW", InsuranceClaimPolicy.normalizePriority("low"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority(null));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority(""));
        assertEquals("CRITICAL", InsuranceClaimPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", InsuranceClaimPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", InsuranceClaimPolicy.normalizePriority("low "));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", InsuranceClaimPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("none"));
        assertEquals("NORMAL", InsuranceClaimPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, InsuranceClaimPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", InsuranceClaimPolicy.redactForLog(""));
        assertEquals("****", InsuranceClaimPolicy.redactForLog("a"));
        assertEquals("****", InsuranceClaimPolicy.redactForLog("ab"));
        assertEquals("****", InsuranceClaimPolicy.redactForLog("abc"));
        assertEquals("****", InsuranceClaimPolicy.redactForLog("abcd"));
        assertEquals("ab***de", InsuranceClaimPolicy.redactForLog("abcde"));
        assertEquals("pa***23", InsuranceClaimPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", InsuranceClaimPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", InsuranceClaimPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", InsuranceClaimPolicy.redactForLog("Rahul"));
        assertEquals("do***01", InsuranceClaimPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", InsuranceClaimPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", InsuranceClaimPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", InsuranceClaimPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", InsuranceClaimPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", InsuranceClaimPolicy.redactForLog("test-value"));
        assertEquals("se***ef", InsuranceClaimPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", InsuranceClaimPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", InsuranceClaimPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", InsuranceClaimPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", InsuranceClaimPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", InsuranceClaimPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", InsuranceClaimPolicy.redactForLog("case-2026"));
        assertEquals("re***88", InsuranceClaimPolicy.redactForLog("record-88"));
        assertEquals("ad***77", InsuranceClaimPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", InsuranceClaimPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", InsuranceClaimPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", InsuranceClaimPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", InsuranceClaimPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", InsuranceClaimPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", InsuranceClaimPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", InsuranceClaimPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", InsuranceClaimPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", InsuranceClaimPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", InsuranceClaimPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", InsuranceClaimPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", InsuranceClaimPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", InsuranceClaimPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", InsuranceClaimPolicy.truncate("Access Review", 5));
        assertEquals("War…", InsuranceClaimPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", InsuranceClaimPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", InsuranceClaimPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", InsuranceClaimPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", InsuranceClaimPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", InsuranceClaimPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", InsuranceClaimPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", InsuranceClaimPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", InsuranceClaimPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", InsuranceClaimPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", InsuranceClaimPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(InsuranceClaimPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(InsuranceClaimPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(null, null));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, InsuranceClaimPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, InsuranceClaimPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
