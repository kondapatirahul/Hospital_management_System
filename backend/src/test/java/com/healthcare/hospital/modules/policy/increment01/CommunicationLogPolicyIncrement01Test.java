package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.CommunicationLogPolicy;

class CommunicationLogPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", CommunicationLogPolicy.normalizeStatus(null));
        assertEquals("NEW", CommunicationLogPolicy.normalizeStatus(""));
        assertEquals("NEW", CommunicationLogPolicy.normalizeStatus(" "));
        assertEquals("NEW", CommunicationLogPolicy.normalizeStatus("new"));
        assertEquals("NEW", CommunicationLogPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", CommunicationLogPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", CommunicationLogPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", CommunicationLogPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", CommunicationLogPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", CommunicationLogPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", CommunicationLogPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", CommunicationLogPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", CommunicationLogPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", CommunicationLogPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", CommunicationLogPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", CommunicationLogPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", CommunicationLogPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", CommunicationLogPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", CommunicationLogPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", CommunicationLogPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", CommunicationLogPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", CommunicationLogPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", CommunicationLogPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", CommunicationLogPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", CommunicationLogPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, CommunicationLogPolicy.pageSize(-100));
        assertEquals(25, CommunicationLogPolicy.pageSize(-1));
        assertEquals(25, CommunicationLogPolicy.pageSize(0));
        assertEquals(1, CommunicationLogPolicy.pageSize(1));
        assertEquals(2, CommunicationLogPolicy.pageSize(2));
        assertEquals(5, CommunicationLogPolicy.pageSize(5));
        assertEquals(10, CommunicationLogPolicy.pageSize(10));
        assertEquals(20, CommunicationLogPolicy.pageSize(20));
        assertEquals(24, CommunicationLogPolicy.pageSize(24));
        assertEquals(25, CommunicationLogPolicy.pageSize(25));
        assertEquals(26, CommunicationLogPolicy.pageSize(26));
        assertEquals(49, CommunicationLogPolicy.pageSize(49));
        assertEquals(50, CommunicationLogPolicy.pageSize(50));
        assertEquals(75, CommunicationLogPolicy.pageSize(75));
        assertEquals(99, CommunicationLogPolicy.pageSize(99));
        assertEquals(100, CommunicationLogPolicy.pageSize(100));
        assertEquals(101, CommunicationLogPolicy.pageSize(101));
        assertEquals(150, CommunicationLogPolicy.pageSize(150));
        assertEquals(199, CommunicationLogPolicy.pageSize(199));
        assertEquals(200, CommunicationLogPolicy.pageSize(200));
        assertEquals(200, CommunicationLogPolicy.pageSize(201));
        assertEquals(200, CommunicationLogPolicy.pageSize(250));
        assertEquals(200, CommunicationLogPolicy.pageSize(500));
        assertEquals(200, CommunicationLogPolicy.pageSize(1000));
        assertEquals(200, CommunicationLogPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, CommunicationLogPolicy.pageIndex(-100));
        assertEquals(0, CommunicationLogPolicy.pageIndex(-10));
        assertEquals(0, CommunicationLogPolicy.pageIndex(-2));
        assertEquals(0, CommunicationLogPolicy.pageIndex(-1));
        assertEquals(0, CommunicationLogPolicy.pageIndex(0));
        assertEquals(1, CommunicationLogPolicy.pageIndex(1));
        assertEquals(2, CommunicationLogPolicy.pageIndex(2));
        assertEquals(3, CommunicationLogPolicy.pageIndex(3));
        assertEquals(4, CommunicationLogPolicy.pageIndex(4));
        assertEquals(5, CommunicationLogPolicy.pageIndex(5));
        assertEquals(6, CommunicationLogPolicy.pageIndex(6));
        assertEquals(7, CommunicationLogPolicy.pageIndex(7));
        assertEquals(8, CommunicationLogPolicy.pageIndex(8));
        assertEquals(9, CommunicationLogPolicy.pageIndex(9));
        assertEquals(10, CommunicationLogPolicy.pageIndex(10));
        assertEquals(11, CommunicationLogPolicy.pageIndex(11));
        assertEquals(12, CommunicationLogPolicy.pageIndex(12));
        assertEquals(13, CommunicationLogPolicy.pageIndex(13));
        assertEquals(14, CommunicationLogPolicy.pageIndex(14));
        assertEquals(15, CommunicationLogPolicy.pageIndex(15));
        assertEquals(20, CommunicationLogPolicy.pageIndex(20));
        assertEquals(50, CommunicationLogPolicy.pageIndex(50));
        assertEquals(100, CommunicationLogPolicy.pageIndex(100));
        assertEquals(1000, CommunicationLogPolicy.pageIndex(1000));
        assertEquals(99999, CommunicationLogPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", CommunicationLogPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", CommunicationLogPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", CommunicationLogPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", CommunicationLogPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", CommunicationLogPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", CommunicationLogPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", CommunicationLogPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", CommunicationLogPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", CommunicationLogPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", CommunicationLogPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", CommunicationLogPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", CommunicationLogPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", CommunicationLogPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", CommunicationLogPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", CommunicationLogPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", CommunicationLogPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", CommunicationLogPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", CommunicationLogPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", CommunicationLogPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", CommunicationLogPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", CommunicationLogPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", CommunicationLogPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", CommunicationLogPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", CommunicationLogPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", CommunicationLogPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", CommunicationLogPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", CommunicationLogPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", CommunicationLogPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", CommunicationLogPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", CommunicationLogPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", CommunicationLogPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", CommunicationLogPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", CommunicationLogPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", CommunicationLogPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", CommunicationLogPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", CommunicationLogPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", CommunicationLogPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", CommunicationLogPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", CommunicationLogPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", CommunicationLogPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", CommunicationLogPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", CommunicationLogPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", CommunicationLogPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", CommunicationLogPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", CommunicationLogPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", CommunicationLogPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", CommunicationLogPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", CommunicationLogPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", CommunicationLogPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", CommunicationLogPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", CommunicationLogPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", CommunicationLogPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", CommunicationLogPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", CommunicationLogPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", CommunicationLogPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", CommunicationLogPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", CommunicationLogPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", CommunicationLogPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", CommunicationLogPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", CommunicationLogPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", CommunicationLogPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", CommunicationLogPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", CommunicationLogPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", CommunicationLogPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", CommunicationLogPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", CommunicationLogPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", CommunicationLogPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", CommunicationLogPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", CommunicationLogPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", CommunicationLogPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", CommunicationLogPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", CommunicationLogPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", CommunicationLogPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", CommunicationLogPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", CommunicationLogPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), CommunicationLogPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, CommunicationLogPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, CommunicationLogPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, CommunicationLogPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", CommunicationLogPolicy.normalizePriority("critical"));
        assertEquals("URGENT", CommunicationLogPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", CommunicationLogPolicy.normalizePriority("high"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("normal"));
        assertEquals("LOW", CommunicationLogPolicy.normalizePriority("low"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority(null));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority(""));
        assertEquals("CRITICAL", CommunicationLogPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", CommunicationLogPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", CommunicationLogPolicy.normalizePriority("low "));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", CommunicationLogPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("none"));
        assertEquals("NORMAL", CommunicationLogPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, CommunicationLogPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", CommunicationLogPolicy.redactForLog(""));
        assertEquals("****", CommunicationLogPolicy.redactForLog("a"));
        assertEquals("****", CommunicationLogPolicy.redactForLog("ab"));
        assertEquals("****", CommunicationLogPolicy.redactForLog("abc"));
        assertEquals("****", CommunicationLogPolicy.redactForLog("abcd"));
        assertEquals("ab***de", CommunicationLogPolicy.redactForLog("abcde"));
        assertEquals("pa***23", CommunicationLogPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", CommunicationLogPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", CommunicationLogPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", CommunicationLogPolicy.redactForLog("Rahul"));
        assertEquals("do***01", CommunicationLogPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", CommunicationLogPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", CommunicationLogPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", CommunicationLogPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", CommunicationLogPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", CommunicationLogPolicy.redactForLog("test-value"));
        assertEquals("se***ef", CommunicationLogPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", CommunicationLogPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", CommunicationLogPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", CommunicationLogPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", CommunicationLogPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", CommunicationLogPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", CommunicationLogPolicy.redactForLog("case-2026"));
        assertEquals("re***88", CommunicationLogPolicy.redactForLog("record-88"));
        assertEquals("ad***77", CommunicationLogPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", CommunicationLogPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", CommunicationLogPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", CommunicationLogPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", CommunicationLogPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", CommunicationLogPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", CommunicationLogPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", CommunicationLogPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", CommunicationLogPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", CommunicationLogPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", CommunicationLogPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", CommunicationLogPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", CommunicationLogPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", CommunicationLogPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", CommunicationLogPolicy.truncate("Access Review", 5));
        assertEquals("War…", CommunicationLogPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", CommunicationLogPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", CommunicationLogPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", CommunicationLogPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", CommunicationLogPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", CommunicationLogPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", CommunicationLogPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", CommunicationLogPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", CommunicationLogPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", CommunicationLogPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", CommunicationLogPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(CommunicationLogPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(CommunicationLogPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(CommunicationLogPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(CommunicationLogPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(CommunicationLogPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(CommunicationLogPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(CommunicationLogPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(CommunicationLogPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(CommunicationLogPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(CommunicationLogPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(null, null));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, CommunicationLogPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, CommunicationLogPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
