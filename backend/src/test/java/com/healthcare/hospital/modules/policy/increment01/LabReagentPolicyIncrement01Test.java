package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.LabReagentPolicy;

class LabReagentPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", LabReagentPolicy.normalizeStatus(null));
        assertEquals("NEW", LabReagentPolicy.normalizeStatus(""));
        assertEquals("NEW", LabReagentPolicy.normalizeStatus(" "));
        assertEquals("NEW", LabReagentPolicy.normalizeStatus("new"));
        assertEquals("NEW", LabReagentPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", LabReagentPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", LabReagentPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", LabReagentPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", LabReagentPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", LabReagentPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", LabReagentPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", LabReagentPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", LabReagentPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", LabReagentPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", LabReagentPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", LabReagentPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", LabReagentPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", LabReagentPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", LabReagentPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", LabReagentPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", LabReagentPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", LabReagentPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", LabReagentPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", LabReagentPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", LabReagentPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, LabReagentPolicy.pageSize(-100));
        assertEquals(25, LabReagentPolicy.pageSize(-1));
        assertEquals(25, LabReagentPolicy.pageSize(0));
        assertEquals(1, LabReagentPolicy.pageSize(1));
        assertEquals(2, LabReagentPolicy.pageSize(2));
        assertEquals(5, LabReagentPolicy.pageSize(5));
        assertEquals(10, LabReagentPolicy.pageSize(10));
        assertEquals(20, LabReagentPolicy.pageSize(20));
        assertEquals(24, LabReagentPolicy.pageSize(24));
        assertEquals(25, LabReagentPolicy.pageSize(25));
        assertEquals(26, LabReagentPolicy.pageSize(26));
        assertEquals(49, LabReagentPolicy.pageSize(49));
        assertEquals(50, LabReagentPolicy.pageSize(50));
        assertEquals(75, LabReagentPolicy.pageSize(75));
        assertEquals(99, LabReagentPolicy.pageSize(99));
        assertEquals(100, LabReagentPolicy.pageSize(100));
        assertEquals(101, LabReagentPolicy.pageSize(101));
        assertEquals(150, LabReagentPolicy.pageSize(150));
        assertEquals(199, LabReagentPolicy.pageSize(199));
        assertEquals(200, LabReagentPolicy.pageSize(200));
        assertEquals(200, LabReagentPolicy.pageSize(201));
        assertEquals(200, LabReagentPolicy.pageSize(250));
        assertEquals(200, LabReagentPolicy.pageSize(500));
        assertEquals(200, LabReagentPolicy.pageSize(1000));
        assertEquals(200, LabReagentPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, LabReagentPolicy.pageIndex(-100));
        assertEquals(0, LabReagentPolicy.pageIndex(-10));
        assertEquals(0, LabReagentPolicy.pageIndex(-2));
        assertEquals(0, LabReagentPolicy.pageIndex(-1));
        assertEquals(0, LabReagentPolicy.pageIndex(0));
        assertEquals(1, LabReagentPolicy.pageIndex(1));
        assertEquals(2, LabReagentPolicy.pageIndex(2));
        assertEquals(3, LabReagentPolicy.pageIndex(3));
        assertEquals(4, LabReagentPolicy.pageIndex(4));
        assertEquals(5, LabReagentPolicy.pageIndex(5));
        assertEquals(6, LabReagentPolicy.pageIndex(6));
        assertEquals(7, LabReagentPolicy.pageIndex(7));
        assertEquals(8, LabReagentPolicy.pageIndex(8));
        assertEquals(9, LabReagentPolicy.pageIndex(9));
        assertEquals(10, LabReagentPolicy.pageIndex(10));
        assertEquals(11, LabReagentPolicy.pageIndex(11));
        assertEquals(12, LabReagentPolicy.pageIndex(12));
        assertEquals(13, LabReagentPolicy.pageIndex(13));
        assertEquals(14, LabReagentPolicy.pageIndex(14));
        assertEquals(15, LabReagentPolicy.pageIndex(15));
        assertEquals(20, LabReagentPolicy.pageIndex(20));
        assertEquals(50, LabReagentPolicy.pageIndex(50));
        assertEquals(100, LabReagentPolicy.pageIndex(100));
        assertEquals(1000, LabReagentPolicy.pageIndex(1000));
        assertEquals(99999, LabReagentPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", LabReagentPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabReagentPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", LabReagentPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", LabReagentPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", LabReagentPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabReagentPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabReagentPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabReagentPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", LabReagentPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabReagentPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabReagentPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", LabReagentPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabReagentPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabReagentPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabReagentPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabReagentPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabReagentPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabReagentPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabReagentPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", LabReagentPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", LabReagentPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabReagentPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabReagentPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabReagentPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", LabReagentPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", LabReagentPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabReagentPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", LabReagentPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", LabReagentPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", LabReagentPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabReagentPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabReagentPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabReagentPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", LabReagentPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabReagentPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabReagentPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", LabReagentPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabReagentPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabReagentPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabReagentPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabReagentPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabReagentPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabReagentPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabReagentPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", LabReagentPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", LabReagentPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabReagentPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabReagentPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabReagentPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", LabReagentPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", LabReagentPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", LabReagentPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", LabReagentPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", LabReagentPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", LabReagentPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", LabReagentPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", LabReagentPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", LabReagentPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", LabReagentPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", LabReagentPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", LabReagentPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", LabReagentPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", LabReagentPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", LabReagentPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", LabReagentPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", LabReagentPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", LabReagentPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", LabReagentPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", LabReagentPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", LabReagentPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", LabReagentPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", LabReagentPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", LabReagentPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", LabReagentPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", LabReagentPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), LabReagentPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, LabReagentPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, LabReagentPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, LabReagentPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", LabReagentPolicy.normalizePriority("critical"));
        assertEquals("URGENT", LabReagentPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", LabReagentPolicy.normalizePriority("high"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("normal"));
        assertEquals("LOW", LabReagentPolicy.normalizePriority("low"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority(null));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority(""));
        assertEquals("CRITICAL", LabReagentPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", LabReagentPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", LabReagentPolicy.normalizePriority("low "));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", LabReagentPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("none"));
        assertEquals("NORMAL", LabReagentPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, LabReagentPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, LabReagentPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, LabReagentPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, LabReagentPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, LabReagentPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, LabReagentPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, LabReagentPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, LabReagentPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, LabReagentPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, LabReagentPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, LabReagentPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, LabReagentPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, LabReagentPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, LabReagentPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, LabReagentPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, LabReagentPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, LabReagentPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, LabReagentPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, LabReagentPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, LabReagentPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, LabReagentPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, LabReagentPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, LabReagentPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, LabReagentPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, LabReagentPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", LabReagentPolicy.redactForLog(""));
        assertEquals("****", LabReagentPolicy.redactForLog("a"));
        assertEquals("****", LabReagentPolicy.redactForLog("ab"));
        assertEquals("****", LabReagentPolicy.redactForLog("abc"));
        assertEquals("****", LabReagentPolicy.redactForLog("abcd"));
        assertEquals("ab***de", LabReagentPolicy.redactForLog("abcde"));
        assertEquals("pa***23", LabReagentPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", LabReagentPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", LabReagentPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", LabReagentPolicy.redactForLog("Rahul"));
        assertEquals("do***01", LabReagentPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", LabReagentPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", LabReagentPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", LabReagentPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", LabReagentPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", LabReagentPolicy.redactForLog("test-value"));
        assertEquals("se***ef", LabReagentPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", LabReagentPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", LabReagentPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", LabReagentPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", LabReagentPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", LabReagentPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", LabReagentPolicy.redactForLog("case-2026"));
        assertEquals("re***88", LabReagentPolicy.redactForLog("record-88"));
        assertEquals("ad***77", LabReagentPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", LabReagentPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", LabReagentPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", LabReagentPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", LabReagentPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", LabReagentPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", LabReagentPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", LabReagentPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", LabReagentPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", LabReagentPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", LabReagentPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", LabReagentPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", LabReagentPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", LabReagentPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", LabReagentPolicy.truncate("Access Review", 5));
        assertEquals("War…", LabReagentPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", LabReagentPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", LabReagentPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", LabReagentPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", LabReagentPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", LabReagentPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", LabReagentPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", LabReagentPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", LabReagentPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", LabReagentPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", LabReagentPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(LabReagentPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(LabReagentPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(LabReagentPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(LabReagentPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(LabReagentPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(LabReagentPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(LabReagentPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(LabReagentPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(LabReagentPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(LabReagentPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(LabReagentPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(LabReagentPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(LabReagentPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(LabReagentPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(LabReagentPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(LabReagentPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(LabReagentPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(LabReagentPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(LabReagentPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(LabReagentPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(LabReagentPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(LabReagentPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(LabReagentPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(LabReagentPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(LabReagentPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, LabReagentPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, LabReagentPolicy.requiresApproval(null, null));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, LabReagentPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, LabReagentPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
