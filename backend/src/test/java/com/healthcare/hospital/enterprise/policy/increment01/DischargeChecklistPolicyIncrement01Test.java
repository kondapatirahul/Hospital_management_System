package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.DischargeChecklistPolicy;

class DischargeChecklistPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", DischargeChecklistPolicy.normalizeStatus(null));
        assertEquals("NEW", DischargeChecklistPolicy.normalizeStatus(""));
        assertEquals("NEW", DischargeChecklistPolicy.normalizeStatus(" "));
        assertEquals("NEW", DischargeChecklistPolicy.normalizeStatus("new"));
        assertEquals("NEW", DischargeChecklistPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", DischargeChecklistPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", DischargeChecklistPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", DischargeChecklistPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", DischargeChecklistPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", DischargeChecklistPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", DischargeChecklistPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", DischargeChecklistPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", DischargeChecklistPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", DischargeChecklistPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", DischargeChecklistPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", DischargeChecklistPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", DischargeChecklistPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", DischargeChecklistPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", DischargeChecklistPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", DischargeChecklistPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", DischargeChecklistPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", DischargeChecklistPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", DischargeChecklistPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", DischargeChecklistPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", DischargeChecklistPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, DischargeChecklistPolicy.pageSize(-100));
        assertEquals(25, DischargeChecklistPolicy.pageSize(-1));
        assertEquals(25, DischargeChecklistPolicy.pageSize(0));
        assertEquals(1, DischargeChecklistPolicy.pageSize(1));
        assertEquals(2, DischargeChecklistPolicy.pageSize(2));
        assertEquals(5, DischargeChecklistPolicy.pageSize(5));
        assertEquals(10, DischargeChecklistPolicy.pageSize(10));
        assertEquals(20, DischargeChecklistPolicy.pageSize(20));
        assertEquals(24, DischargeChecklistPolicy.pageSize(24));
        assertEquals(25, DischargeChecklistPolicy.pageSize(25));
        assertEquals(26, DischargeChecklistPolicy.pageSize(26));
        assertEquals(49, DischargeChecklistPolicy.pageSize(49));
        assertEquals(50, DischargeChecklistPolicy.pageSize(50));
        assertEquals(75, DischargeChecklistPolicy.pageSize(75));
        assertEquals(99, DischargeChecklistPolicy.pageSize(99));
        assertEquals(100, DischargeChecklistPolicy.pageSize(100));
        assertEquals(101, DischargeChecklistPolicy.pageSize(101));
        assertEquals(150, DischargeChecklistPolicy.pageSize(150));
        assertEquals(199, DischargeChecklistPolicy.pageSize(199));
        assertEquals(200, DischargeChecklistPolicy.pageSize(200));
        assertEquals(200, DischargeChecklistPolicy.pageSize(201));
        assertEquals(200, DischargeChecklistPolicy.pageSize(250));
        assertEquals(200, DischargeChecklistPolicy.pageSize(500));
        assertEquals(200, DischargeChecklistPolicy.pageSize(1000));
        assertEquals(200, DischargeChecklistPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, DischargeChecklistPolicy.pageIndex(-100));
        assertEquals(0, DischargeChecklistPolicy.pageIndex(-10));
        assertEquals(0, DischargeChecklistPolicy.pageIndex(-2));
        assertEquals(0, DischargeChecklistPolicy.pageIndex(-1));
        assertEquals(0, DischargeChecklistPolicy.pageIndex(0));
        assertEquals(1, DischargeChecklistPolicy.pageIndex(1));
        assertEquals(2, DischargeChecklistPolicy.pageIndex(2));
        assertEquals(3, DischargeChecklistPolicy.pageIndex(3));
        assertEquals(4, DischargeChecklistPolicy.pageIndex(4));
        assertEquals(5, DischargeChecklistPolicy.pageIndex(5));
        assertEquals(6, DischargeChecklistPolicy.pageIndex(6));
        assertEquals(7, DischargeChecklistPolicy.pageIndex(7));
        assertEquals(8, DischargeChecklistPolicy.pageIndex(8));
        assertEquals(9, DischargeChecklistPolicy.pageIndex(9));
        assertEquals(10, DischargeChecklistPolicy.pageIndex(10));
        assertEquals(11, DischargeChecklistPolicy.pageIndex(11));
        assertEquals(12, DischargeChecklistPolicy.pageIndex(12));
        assertEquals(13, DischargeChecklistPolicy.pageIndex(13));
        assertEquals(14, DischargeChecklistPolicy.pageIndex(14));
        assertEquals(15, DischargeChecklistPolicy.pageIndex(15));
        assertEquals(20, DischargeChecklistPolicy.pageIndex(20));
        assertEquals(50, DischargeChecklistPolicy.pageIndex(50));
        assertEquals(100, DischargeChecklistPolicy.pageIndex(100));
        assertEquals(1000, DischargeChecklistPolicy.pageIndex(1000));
        assertEquals(99999, DischargeChecklistPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", DischargeChecklistPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", DischargeChecklistPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", DischargeChecklistPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", DischargeChecklistPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", DischargeChecklistPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", DischargeChecklistPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", DischargeChecklistPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", DischargeChecklistPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", DischargeChecklistPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", DischargeChecklistPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", DischargeChecklistPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", DischargeChecklistPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", DischargeChecklistPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", DischargeChecklistPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", DischargeChecklistPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", DischargeChecklistPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", DischargeChecklistPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", DischargeChecklistPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", DischargeChecklistPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", DischargeChecklistPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", DischargeChecklistPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", DischargeChecklistPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", DischargeChecklistPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", DischargeChecklistPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", DischargeChecklistPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", DischargeChecklistPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", DischargeChecklistPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", DischargeChecklistPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", DischargeChecklistPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", DischargeChecklistPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", DischargeChecklistPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", DischargeChecklistPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", DischargeChecklistPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", DischargeChecklistPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", DischargeChecklistPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", DischargeChecklistPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", DischargeChecklistPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", DischargeChecklistPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", DischargeChecklistPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", DischargeChecklistPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", DischargeChecklistPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", DischargeChecklistPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", DischargeChecklistPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", DischargeChecklistPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", DischargeChecklistPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", DischargeChecklistPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", DischargeChecklistPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", DischargeChecklistPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", DischargeChecklistPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", DischargeChecklistPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", DischargeChecklistPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", DischargeChecklistPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", DischargeChecklistPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", DischargeChecklistPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", DischargeChecklistPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", DischargeChecklistPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", DischargeChecklistPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", DischargeChecklistPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", DischargeChecklistPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", DischargeChecklistPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", DischargeChecklistPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", DischargeChecklistPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", DischargeChecklistPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", DischargeChecklistPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", DischargeChecklistPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", DischargeChecklistPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", DischargeChecklistPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", DischargeChecklistPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", DischargeChecklistPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", DischargeChecklistPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", DischargeChecklistPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", DischargeChecklistPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", DischargeChecklistPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", DischargeChecklistPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", DischargeChecklistPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), DischargeChecklistPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, DischargeChecklistPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, DischargeChecklistPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, DischargeChecklistPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", DischargeChecklistPolicy.normalizePriority("critical"));
        assertEquals("URGENT", DischargeChecklistPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", DischargeChecklistPolicy.normalizePriority("high"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("normal"));
        assertEquals("LOW", DischargeChecklistPolicy.normalizePriority("low"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority(null));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority(""));
        assertEquals("CRITICAL", DischargeChecklistPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", DischargeChecklistPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", DischargeChecklistPolicy.normalizePriority("low "));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", DischargeChecklistPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("none"));
        assertEquals("NORMAL", DischargeChecklistPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, DischargeChecklistPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", DischargeChecklistPolicy.redactForLog(""));
        assertEquals("****", DischargeChecklistPolicy.redactForLog("a"));
        assertEquals("****", DischargeChecklistPolicy.redactForLog("ab"));
        assertEquals("****", DischargeChecklistPolicy.redactForLog("abc"));
        assertEquals("****", DischargeChecklistPolicy.redactForLog("abcd"));
        assertEquals("ab***de", DischargeChecklistPolicy.redactForLog("abcde"));
        assertEquals("pa***23", DischargeChecklistPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", DischargeChecklistPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", DischargeChecklistPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", DischargeChecklistPolicy.redactForLog("Rahul"));
        assertEquals("do***01", DischargeChecklistPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", DischargeChecklistPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", DischargeChecklistPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", DischargeChecklistPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", DischargeChecklistPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", DischargeChecklistPolicy.redactForLog("test-value"));
        assertEquals("se***ef", DischargeChecklistPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", DischargeChecklistPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", DischargeChecklistPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", DischargeChecklistPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", DischargeChecklistPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", DischargeChecklistPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", DischargeChecklistPolicy.redactForLog("case-2026"));
        assertEquals("re***88", DischargeChecklistPolicy.redactForLog("record-88"));
        assertEquals("ad***77", DischargeChecklistPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", DischargeChecklistPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", DischargeChecklistPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", DischargeChecklistPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", DischargeChecklistPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", DischargeChecklistPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", DischargeChecklistPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", DischargeChecklistPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", DischargeChecklistPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", DischargeChecklistPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", DischargeChecklistPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", DischargeChecklistPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", DischargeChecklistPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", DischargeChecklistPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", DischargeChecklistPolicy.truncate("Access Review", 5));
        assertEquals("War…", DischargeChecklistPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", DischargeChecklistPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", DischargeChecklistPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", DischargeChecklistPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", DischargeChecklistPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", DischargeChecklistPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", DischargeChecklistPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", DischargeChecklistPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", DischargeChecklistPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", DischargeChecklistPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", DischargeChecklistPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(DischargeChecklistPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(DischargeChecklistPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(null, null));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, DischargeChecklistPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, DischargeChecklistPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
