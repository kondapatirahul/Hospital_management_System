package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.PharmacyRecallPolicy;

class PharmacyRecallPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", PharmacyRecallPolicy.normalizeStatus(null));
        assertEquals("NEW", PharmacyRecallPolicy.normalizeStatus(""));
        assertEquals("NEW", PharmacyRecallPolicy.normalizeStatus(" "));
        assertEquals("NEW", PharmacyRecallPolicy.normalizeStatus("new"));
        assertEquals("NEW", PharmacyRecallPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", PharmacyRecallPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", PharmacyRecallPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", PharmacyRecallPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", PharmacyRecallPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", PharmacyRecallPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", PharmacyRecallPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", PharmacyRecallPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", PharmacyRecallPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", PharmacyRecallPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", PharmacyRecallPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", PharmacyRecallPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", PharmacyRecallPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", PharmacyRecallPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", PharmacyRecallPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", PharmacyRecallPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", PharmacyRecallPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", PharmacyRecallPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", PharmacyRecallPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", PharmacyRecallPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", PharmacyRecallPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, PharmacyRecallPolicy.pageSize(-100));
        assertEquals(25, PharmacyRecallPolicy.pageSize(-1));
        assertEquals(25, PharmacyRecallPolicy.pageSize(0));
        assertEquals(1, PharmacyRecallPolicy.pageSize(1));
        assertEquals(2, PharmacyRecallPolicy.pageSize(2));
        assertEquals(5, PharmacyRecallPolicy.pageSize(5));
        assertEquals(10, PharmacyRecallPolicy.pageSize(10));
        assertEquals(20, PharmacyRecallPolicy.pageSize(20));
        assertEquals(24, PharmacyRecallPolicy.pageSize(24));
        assertEquals(25, PharmacyRecallPolicy.pageSize(25));
        assertEquals(26, PharmacyRecallPolicy.pageSize(26));
        assertEquals(49, PharmacyRecallPolicy.pageSize(49));
        assertEquals(50, PharmacyRecallPolicy.pageSize(50));
        assertEquals(75, PharmacyRecallPolicy.pageSize(75));
        assertEquals(99, PharmacyRecallPolicy.pageSize(99));
        assertEquals(100, PharmacyRecallPolicy.pageSize(100));
        assertEquals(101, PharmacyRecallPolicy.pageSize(101));
        assertEquals(150, PharmacyRecallPolicy.pageSize(150));
        assertEquals(199, PharmacyRecallPolicy.pageSize(199));
        assertEquals(200, PharmacyRecallPolicy.pageSize(200));
        assertEquals(200, PharmacyRecallPolicy.pageSize(201));
        assertEquals(200, PharmacyRecallPolicy.pageSize(250));
        assertEquals(200, PharmacyRecallPolicy.pageSize(500));
        assertEquals(200, PharmacyRecallPolicy.pageSize(1000));
        assertEquals(200, PharmacyRecallPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, PharmacyRecallPolicy.pageIndex(-100));
        assertEquals(0, PharmacyRecallPolicy.pageIndex(-10));
        assertEquals(0, PharmacyRecallPolicy.pageIndex(-2));
        assertEquals(0, PharmacyRecallPolicy.pageIndex(-1));
        assertEquals(0, PharmacyRecallPolicy.pageIndex(0));
        assertEquals(1, PharmacyRecallPolicy.pageIndex(1));
        assertEquals(2, PharmacyRecallPolicy.pageIndex(2));
        assertEquals(3, PharmacyRecallPolicy.pageIndex(3));
        assertEquals(4, PharmacyRecallPolicy.pageIndex(4));
        assertEquals(5, PharmacyRecallPolicy.pageIndex(5));
        assertEquals(6, PharmacyRecallPolicy.pageIndex(6));
        assertEquals(7, PharmacyRecallPolicy.pageIndex(7));
        assertEquals(8, PharmacyRecallPolicy.pageIndex(8));
        assertEquals(9, PharmacyRecallPolicy.pageIndex(9));
        assertEquals(10, PharmacyRecallPolicy.pageIndex(10));
        assertEquals(11, PharmacyRecallPolicy.pageIndex(11));
        assertEquals(12, PharmacyRecallPolicy.pageIndex(12));
        assertEquals(13, PharmacyRecallPolicy.pageIndex(13));
        assertEquals(14, PharmacyRecallPolicy.pageIndex(14));
        assertEquals(15, PharmacyRecallPolicy.pageIndex(15));
        assertEquals(20, PharmacyRecallPolicy.pageIndex(20));
        assertEquals(50, PharmacyRecallPolicy.pageIndex(50));
        assertEquals(100, PharmacyRecallPolicy.pageIndex(100));
        assertEquals(1000, PharmacyRecallPolicy.pageIndex(1000));
        assertEquals(99999, PharmacyRecallPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", PharmacyRecallPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PharmacyRecallPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", PharmacyRecallPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", PharmacyRecallPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", PharmacyRecallPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", PharmacyRecallPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PharmacyRecallPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PharmacyRecallPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", PharmacyRecallPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", PharmacyRecallPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PharmacyRecallPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", PharmacyRecallPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", PharmacyRecallPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PharmacyRecallPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PharmacyRecallPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PharmacyRecallPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", PharmacyRecallPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PharmacyRecallPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", PharmacyRecallPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", PharmacyRecallPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", PharmacyRecallPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PharmacyRecallPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PharmacyRecallPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PharmacyRecallPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", PharmacyRecallPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", PharmacyRecallPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PharmacyRecallPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", PharmacyRecallPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", PharmacyRecallPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", PharmacyRecallPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", PharmacyRecallPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PharmacyRecallPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PharmacyRecallPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", PharmacyRecallPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", PharmacyRecallPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PharmacyRecallPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", PharmacyRecallPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", PharmacyRecallPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PharmacyRecallPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PharmacyRecallPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PharmacyRecallPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", PharmacyRecallPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PharmacyRecallPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", PharmacyRecallPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", PharmacyRecallPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", PharmacyRecallPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PharmacyRecallPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PharmacyRecallPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PharmacyRecallPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", PharmacyRecallPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", PharmacyRecallPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", PharmacyRecallPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", PharmacyRecallPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", PharmacyRecallPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", PharmacyRecallPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", PharmacyRecallPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", PharmacyRecallPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", PharmacyRecallPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", PharmacyRecallPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", PharmacyRecallPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", PharmacyRecallPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", PharmacyRecallPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", PharmacyRecallPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", PharmacyRecallPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", PharmacyRecallPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", PharmacyRecallPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", PharmacyRecallPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", PharmacyRecallPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", PharmacyRecallPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", PharmacyRecallPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", PharmacyRecallPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", PharmacyRecallPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", PharmacyRecallPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", PharmacyRecallPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", PharmacyRecallPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), PharmacyRecallPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, PharmacyRecallPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, PharmacyRecallPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, PharmacyRecallPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", PharmacyRecallPolicy.normalizePriority("critical"));
        assertEquals("URGENT", PharmacyRecallPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", PharmacyRecallPolicy.normalizePriority("high"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("normal"));
        assertEquals("LOW", PharmacyRecallPolicy.normalizePriority("low"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority(null));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority(""));
        assertEquals("CRITICAL", PharmacyRecallPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", PharmacyRecallPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", PharmacyRecallPolicy.normalizePriority("low "));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", PharmacyRecallPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("none"));
        assertEquals("NORMAL", PharmacyRecallPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, PharmacyRecallPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", PharmacyRecallPolicy.redactForLog(""));
        assertEquals("****", PharmacyRecallPolicy.redactForLog("a"));
        assertEquals("****", PharmacyRecallPolicy.redactForLog("ab"));
        assertEquals("****", PharmacyRecallPolicy.redactForLog("abc"));
        assertEquals("****", PharmacyRecallPolicy.redactForLog("abcd"));
        assertEquals("ab***de", PharmacyRecallPolicy.redactForLog("abcde"));
        assertEquals("pa***23", PharmacyRecallPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", PharmacyRecallPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", PharmacyRecallPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", PharmacyRecallPolicy.redactForLog("Rahul"));
        assertEquals("do***01", PharmacyRecallPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", PharmacyRecallPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", PharmacyRecallPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", PharmacyRecallPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", PharmacyRecallPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", PharmacyRecallPolicy.redactForLog("test-value"));
        assertEquals("se***ef", PharmacyRecallPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", PharmacyRecallPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", PharmacyRecallPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", PharmacyRecallPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", PharmacyRecallPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", PharmacyRecallPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", PharmacyRecallPolicy.redactForLog("case-2026"));
        assertEquals("re***88", PharmacyRecallPolicy.redactForLog("record-88"));
        assertEquals("ad***77", PharmacyRecallPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", PharmacyRecallPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", PharmacyRecallPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", PharmacyRecallPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", PharmacyRecallPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", PharmacyRecallPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", PharmacyRecallPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", PharmacyRecallPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", PharmacyRecallPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", PharmacyRecallPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", PharmacyRecallPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", PharmacyRecallPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", PharmacyRecallPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", PharmacyRecallPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", PharmacyRecallPolicy.truncate("Access Review", 5));
        assertEquals("War…", PharmacyRecallPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", PharmacyRecallPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", PharmacyRecallPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", PharmacyRecallPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", PharmacyRecallPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", PharmacyRecallPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", PharmacyRecallPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", PharmacyRecallPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", PharmacyRecallPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", PharmacyRecallPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", PharmacyRecallPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(PharmacyRecallPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(PharmacyRecallPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(null, null));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, PharmacyRecallPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, PharmacyRecallPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
