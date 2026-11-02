package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.DataRetentionPolicyPolicy;

class DataRetentionPolicyPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", DataRetentionPolicyPolicy.normalizeStatus(null));
        assertEquals("NEW", DataRetentionPolicyPolicy.normalizeStatus(""));
        assertEquals("NEW", DataRetentionPolicyPolicy.normalizeStatus(" "));
        assertEquals("NEW", DataRetentionPolicyPolicy.normalizeStatus("new"));
        assertEquals("NEW", DataRetentionPolicyPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", DataRetentionPolicyPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", DataRetentionPolicyPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", DataRetentionPolicyPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", DataRetentionPolicyPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", DataRetentionPolicyPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", DataRetentionPolicyPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", DataRetentionPolicyPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", DataRetentionPolicyPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", DataRetentionPolicyPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", DataRetentionPolicyPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", DataRetentionPolicyPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", DataRetentionPolicyPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", DataRetentionPolicyPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", DataRetentionPolicyPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", DataRetentionPolicyPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", DataRetentionPolicyPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", DataRetentionPolicyPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", DataRetentionPolicyPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", DataRetentionPolicyPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", DataRetentionPolicyPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, DataRetentionPolicyPolicy.pageSize(-100));
        assertEquals(25, DataRetentionPolicyPolicy.pageSize(-1));
        assertEquals(25, DataRetentionPolicyPolicy.pageSize(0));
        assertEquals(1, DataRetentionPolicyPolicy.pageSize(1));
        assertEquals(2, DataRetentionPolicyPolicy.pageSize(2));
        assertEquals(5, DataRetentionPolicyPolicy.pageSize(5));
        assertEquals(10, DataRetentionPolicyPolicy.pageSize(10));
        assertEquals(20, DataRetentionPolicyPolicy.pageSize(20));
        assertEquals(24, DataRetentionPolicyPolicy.pageSize(24));
        assertEquals(25, DataRetentionPolicyPolicy.pageSize(25));
        assertEquals(26, DataRetentionPolicyPolicy.pageSize(26));
        assertEquals(49, DataRetentionPolicyPolicy.pageSize(49));
        assertEquals(50, DataRetentionPolicyPolicy.pageSize(50));
        assertEquals(75, DataRetentionPolicyPolicy.pageSize(75));
        assertEquals(99, DataRetentionPolicyPolicy.pageSize(99));
        assertEquals(100, DataRetentionPolicyPolicy.pageSize(100));
        assertEquals(101, DataRetentionPolicyPolicy.pageSize(101));
        assertEquals(150, DataRetentionPolicyPolicy.pageSize(150));
        assertEquals(199, DataRetentionPolicyPolicy.pageSize(199));
        assertEquals(200, DataRetentionPolicyPolicy.pageSize(200));
        assertEquals(200, DataRetentionPolicyPolicy.pageSize(201));
        assertEquals(200, DataRetentionPolicyPolicy.pageSize(250));
        assertEquals(200, DataRetentionPolicyPolicy.pageSize(500));
        assertEquals(200, DataRetentionPolicyPolicy.pageSize(1000));
        assertEquals(200, DataRetentionPolicyPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, DataRetentionPolicyPolicy.pageIndex(-100));
        assertEquals(0, DataRetentionPolicyPolicy.pageIndex(-10));
        assertEquals(0, DataRetentionPolicyPolicy.pageIndex(-2));
        assertEquals(0, DataRetentionPolicyPolicy.pageIndex(-1));
        assertEquals(0, DataRetentionPolicyPolicy.pageIndex(0));
        assertEquals(1, DataRetentionPolicyPolicy.pageIndex(1));
        assertEquals(2, DataRetentionPolicyPolicy.pageIndex(2));
        assertEquals(3, DataRetentionPolicyPolicy.pageIndex(3));
        assertEquals(4, DataRetentionPolicyPolicy.pageIndex(4));
        assertEquals(5, DataRetentionPolicyPolicy.pageIndex(5));
        assertEquals(6, DataRetentionPolicyPolicy.pageIndex(6));
        assertEquals(7, DataRetentionPolicyPolicy.pageIndex(7));
        assertEquals(8, DataRetentionPolicyPolicy.pageIndex(8));
        assertEquals(9, DataRetentionPolicyPolicy.pageIndex(9));
        assertEquals(10, DataRetentionPolicyPolicy.pageIndex(10));
        assertEquals(11, DataRetentionPolicyPolicy.pageIndex(11));
        assertEquals(12, DataRetentionPolicyPolicy.pageIndex(12));
        assertEquals(13, DataRetentionPolicyPolicy.pageIndex(13));
        assertEquals(14, DataRetentionPolicyPolicy.pageIndex(14));
        assertEquals(15, DataRetentionPolicyPolicy.pageIndex(15));
        assertEquals(20, DataRetentionPolicyPolicy.pageIndex(20));
        assertEquals(50, DataRetentionPolicyPolicy.pageIndex(50));
        assertEquals(100, DataRetentionPolicyPolicy.pageIndex(100));
        assertEquals(1000, DataRetentionPolicyPolicy.pageIndex(1000));
        assertEquals(99999, DataRetentionPolicyPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", DataRetentionPolicyPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", DataRetentionPolicyPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", DataRetentionPolicyPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", DataRetentionPolicyPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", DataRetentionPolicyPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", DataRetentionPolicyPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", DataRetentionPolicyPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", DataRetentionPolicyPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", DataRetentionPolicyPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", DataRetentionPolicyPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", DataRetentionPolicyPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", DataRetentionPolicyPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", DataRetentionPolicyPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", DataRetentionPolicyPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", DataRetentionPolicyPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", DataRetentionPolicyPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", DataRetentionPolicyPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", DataRetentionPolicyPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", DataRetentionPolicyPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", DataRetentionPolicyPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", DataRetentionPolicyPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", DataRetentionPolicyPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", DataRetentionPolicyPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", DataRetentionPolicyPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", DataRetentionPolicyPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", DataRetentionPolicyPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", DataRetentionPolicyPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", DataRetentionPolicyPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", DataRetentionPolicyPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", DataRetentionPolicyPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", DataRetentionPolicyPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", DataRetentionPolicyPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", DataRetentionPolicyPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", DataRetentionPolicyPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", DataRetentionPolicyPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", DataRetentionPolicyPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", DataRetentionPolicyPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", DataRetentionPolicyPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", DataRetentionPolicyPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", DataRetentionPolicyPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", DataRetentionPolicyPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", DataRetentionPolicyPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", DataRetentionPolicyPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", DataRetentionPolicyPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", DataRetentionPolicyPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", DataRetentionPolicyPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", DataRetentionPolicyPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", DataRetentionPolicyPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", DataRetentionPolicyPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", DataRetentionPolicyPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", DataRetentionPolicyPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", DataRetentionPolicyPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", DataRetentionPolicyPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", DataRetentionPolicyPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", DataRetentionPolicyPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", DataRetentionPolicyPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", DataRetentionPolicyPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", DataRetentionPolicyPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", DataRetentionPolicyPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", DataRetentionPolicyPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", DataRetentionPolicyPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", DataRetentionPolicyPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", DataRetentionPolicyPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", DataRetentionPolicyPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", DataRetentionPolicyPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", DataRetentionPolicyPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", DataRetentionPolicyPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", DataRetentionPolicyPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", DataRetentionPolicyPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", DataRetentionPolicyPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", DataRetentionPolicyPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", DataRetentionPolicyPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", DataRetentionPolicyPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", DataRetentionPolicyPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", DataRetentionPolicyPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), DataRetentionPolicyPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, DataRetentionPolicyPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, DataRetentionPolicyPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, DataRetentionPolicyPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", DataRetentionPolicyPolicy.normalizePriority("critical"));
        assertEquals("URGENT", DataRetentionPolicyPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", DataRetentionPolicyPolicy.normalizePriority("high"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("normal"));
        assertEquals("LOW", DataRetentionPolicyPolicy.normalizePriority("low"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority(null));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority(""));
        assertEquals("CRITICAL", DataRetentionPolicyPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", DataRetentionPolicyPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", DataRetentionPolicyPolicy.normalizePriority("low "));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", DataRetentionPolicyPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("none"));
        assertEquals("NORMAL", DataRetentionPolicyPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, DataRetentionPolicyPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", DataRetentionPolicyPolicy.redactForLog(""));
        assertEquals("****", DataRetentionPolicyPolicy.redactForLog("a"));
        assertEquals("****", DataRetentionPolicyPolicy.redactForLog("ab"));
        assertEquals("****", DataRetentionPolicyPolicy.redactForLog("abc"));
        assertEquals("****", DataRetentionPolicyPolicy.redactForLog("abcd"));
        assertEquals("ab***de", DataRetentionPolicyPolicy.redactForLog("abcde"));
        assertEquals("pa***23", DataRetentionPolicyPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", DataRetentionPolicyPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", DataRetentionPolicyPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", DataRetentionPolicyPolicy.redactForLog("Rahul"));
        assertEquals("do***01", DataRetentionPolicyPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", DataRetentionPolicyPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", DataRetentionPolicyPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", DataRetentionPolicyPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", DataRetentionPolicyPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", DataRetentionPolicyPolicy.redactForLog("test-value"));
        assertEquals("se***ef", DataRetentionPolicyPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", DataRetentionPolicyPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", DataRetentionPolicyPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", DataRetentionPolicyPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", DataRetentionPolicyPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", DataRetentionPolicyPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", DataRetentionPolicyPolicy.redactForLog("case-2026"));
        assertEquals("re***88", DataRetentionPolicyPolicy.redactForLog("record-88"));
        assertEquals("ad***77", DataRetentionPolicyPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", DataRetentionPolicyPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", DataRetentionPolicyPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", DataRetentionPolicyPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", DataRetentionPolicyPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", DataRetentionPolicyPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", DataRetentionPolicyPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", DataRetentionPolicyPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", DataRetentionPolicyPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", DataRetentionPolicyPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", DataRetentionPolicyPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", DataRetentionPolicyPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", DataRetentionPolicyPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", DataRetentionPolicyPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", DataRetentionPolicyPolicy.truncate("Access Review", 5));
        assertEquals("War…", DataRetentionPolicyPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", DataRetentionPolicyPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", DataRetentionPolicyPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", DataRetentionPolicyPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", DataRetentionPolicyPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", DataRetentionPolicyPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", DataRetentionPolicyPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", DataRetentionPolicyPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", DataRetentionPolicyPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", DataRetentionPolicyPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", DataRetentionPolicyPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(DataRetentionPolicyPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(DataRetentionPolicyPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(null, null));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, DataRetentionPolicyPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
