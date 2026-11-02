package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.LabQualityControlPolicy;

class LabQualityControlPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", LabQualityControlPolicy.normalizeStatus(null));
        assertEquals("NEW", LabQualityControlPolicy.normalizeStatus(""));
        assertEquals("NEW", LabQualityControlPolicy.normalizeStatus(" "));
        assertEquals("NEW", LabQualityControlPolicy.normalizeStatus("new"));
        assertEquals("NEW", LabQualityControlPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", LabQualityControlPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", LabQualityControlPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", LabQualityControlPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", LabQualityControlPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", LabQualityControlPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", LabQualityControlPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", LabQualityControlPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", LabQualityControlPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", LabQualityControlPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", LabQualityControlPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", LabQualityControlPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", LabQualityControlPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", LabQualityControlPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", LabQualityControlPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", LabQualityControlPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", LabQualityControlPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", LabQualityControlPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", LabQualityControlPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", LabQualityControlPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", LabQualityControlPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, LabQualityControlPolicy.pageSize(-100));
        assertEquals(25, LabQualityControlPolicy.pageSize(-1));
        assertEquals(25, LabQualityControlPolicy.pageSize(0));
        assertEquals(1, LabQualityControlPolicy.pageSize(1));
        assertEquals(2, LabQualityControlPolicy.pageSize(2));
        assertEquals(5, LabQualityControlPolicy.pageSize(5));
        assertEquals(10, LabQualityControlPolicy.pageSize(10));
        assertEquals(20, LabQualityControlPolicy.pageSize(20));
        assertEquals(24, LabQualityControlPolicy.pageSize(24));
        assertEquals(25, LabQualityControlPolicy.pageSize(25));
        assertEquals(26, LabQualityControlPolicy.pageSize(26));
        assertEquals(49, LabQualityControlPolicy.pageSize(49));
        assertEquals(50, LabQualityControlPolicy.pageSize(50));
        assertEquals(75, LabQualityControlPolicy.pageSize(75));
        assertEquals(99, LabQualityControlPolicy.pageSize(99));
        assertEquals(100, LabQualityControlPolicy.pageSize(100));
        assertEquals(101, LabQualityControlPolicy.pageSize(101));
        assertEquals(150, LabQualityControlPolicy.pageSize(150));
        assertEquals(199, LabQualityControlPolicy.pageSize(199));
        assertEquals(200, LabQualityControlPolicy.pageSize(200));
        assertEquals(200, LabQualityControlPolicy.pageSize(201));
        assertEquals(200, LabQualityControlPolicy.pageSize(250));
        assertEquals(200, LabQualityControlPolicy.pageSize(500));
        assertEquals(200, LabQualityControlPolicy.pageSize(1000));
        assertEquals(200, LabQualityControlPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, LabQualityControlPolicy.pageIndex(-100));
        assertEquals(0, LabQualityControlPolicy.pageIndex(-10));
        assertEquals(0, LabQualityControlPolicy.pageIndex(-2));
        assertEquals(0, LabQualityControlPolicy.pageIndex(-1));
        assertEquals(0, LabQualityControlPolicy.pageIndex(0));
        assertEquals(1, LabQualityControlPolicy.pageIndex(1));
        assertEquals(2, LabQualityControlPolicy.pageIndex(2));
        assertEquals(3, LabQualityControlPolicy.pageIndex(3));
        assertEquals(4, LabQualityControlPolicy.pageIndex(4));
        assertEquals(5, LabQualityControlPolicy.pageIndex(5));
        assertEquals(6, LabQualityControlPolicy.pageIndex(6));
        assertEquals(7, LabQualityControlPolicy.pageIndex(7));
        assertEquals(8, LabQualityControlPolicy.pageIndex(8));
        assertEquals(9, LabQualityControlPolicy.pageIndex(9));
        assertEquals(10, LabQualityControlPolicy.pageIndex(10));
        assertEquals(11, LabQualityControlPolicy.pageIndex(11));
        assertEquals(12, LabQualityControlPolicy.pageIndex(12));
        assertEquals(13, LabQualityControlPolicy.pageIndex(13));
        assertEquals(14, LabQualityControlPolicy.pageIndex(14));
        assertEquals(15, LabQualityControlPolicy.pageIndex(15));
        assertEquals(20, LabQualityControlPolicy.pageIndex(20));
        assertEquals(50, LabQualityControlPolicy.pageIndex(50));
        assertEquals(100, LabQualityControlPolicy.pageIndex(100));
        assertEquals(1000, LabQualityControlPolicy.pageIndex(1000));
        assertEquals(99999, LabQualityControlPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", LabQualityControlPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabQualityControlPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", LabQualityControlPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", LabQualityControlPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", LabQualityControlPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabQualityControlPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabQualityControlPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabQualityControlPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", LabQualityControlPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabQualityControlPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabQualityControlPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", LabQualityControlPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabQualityControlPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabQualityControlPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabQualityControlPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabQualityControlPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabQualityControlPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabQualityControlPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabQualityControlPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", LabQualityControlPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", LabQualityControlPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabQualityControlPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabQualityControlPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabQualityControlPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", LabQualityControlPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", LabQualityControlPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", LabQualityControlPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", LabQualityControlPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", LabQualityControlPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", LabQualityControlPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", LabQualityControlPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", LabQualityControlPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", LabQualityControlPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", LabQualityControlPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", LabQualityControlPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", LabQualityControlPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", LabQualityControlPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", LabQualityControlPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", LabQualityControlPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", LabQualityControlPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", LabQualityControlPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", LabQualityControlPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", LabQualityControlPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", LabQualityControlPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", LabQualityControlPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", LabQualityControlPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", LabQualityControlPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", LabQualityControlPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", LabQualityControlPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", LabQualityControlPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", LabQualityControlPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", LabQualityControlPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", LabQualityControlPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", LabQualityControlPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", LabQualityControlPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", LabQualityControlPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", LabQualityControlPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", LabQualityControlPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", LabQualityControlPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", LabQualityControlPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", LabQualityControlPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", LabQualityControlPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", LabQualityControlPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", LabQualityControlPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", LabQualityControlPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", LabQualityControlPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", LabQualityControlPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", LabQualityControlPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", LabQualityControlPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", LabQualityControlPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", LabQualityControlPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", LabQualityControlPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", LabQualityControlPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", LabQualityControlPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", LabQualityControlPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), LabQualityControlPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, LabQualityControlPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, LabQualityControlPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, LabQualityControlPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", LabQualityControlPolicy.normalizePriority("critical"));
        assertEquals("URGENT", LabQualityControlPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", LabQualityControlPolicy.normalizePriority("high"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("normal"));
        assertEquals("LOW", LabQualityControlPolicy.normalizePriority("low"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority(null));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority(""));
        assertEquals("CRITICAL", LabQualityControlPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", LabQualityControlPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", LabQualityControlPolicy.normalizePriority("low "));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", LabQualityControlPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("none"));
        assertEquals("NORMAL", LabQualityControlPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, LabQualityControlPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", LabQualityControlPolicy.redactForLog(""));
        assertEquals("****", LabQualityControlPolicy.redactForLog("a"));
        assertEquals("****", LabQualityControlPolicy.redactForLog("ab"));
        assertEquals("****", LabQualityControlPolicy.redactForLog("abc"));
        assertEquals("****", LabQualityControlPolicy.redactForLog("abcd"));
        assertEquals("ab***de", LabQualityControlPolicy.redactForLog("abcde"));
        assertEquals("pa***23", LabQualityControlPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", LabQualityControlPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", LabQualityControlPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", LabQualityControlPolicy.redactForLog("Rahul"));
        assertEquals("do***01", LabQualityControlPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", LabQualityControlPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", LabQualityControlPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", LabQualityControlPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", LabQualityControlPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", LabQualityControlPolicy.redactForLog("test-value"));
        assertEquals("se***ef", LabQualityControlPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", LabQualityControlPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", LabQualityControlPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", LabQualityControlPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", LabQualityControlPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", LabQualityControlPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", LabQualityControlPolicy.redactForLog("case-2026"));
        assertEquals("re***88", LabQualityControlPolicy.redactForLog("record-88"));
        assertEquals("ad***77", LabQualityControlPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", LabQualityControlPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", LabQualityControlPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", LabQualityControlPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", LabQualityControlPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", LabQualityControlPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", LabQualityControlPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", LabQualityControlPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", LabQualityControlPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", LabQualityControlPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", LabQualityControlPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", LabQualityControlPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", LabQualityControlPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", LabQualityControlPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", LabQualityControlPolicy.truncate("Access Review", 5));
        assertEquals("War…", LabQualityControlPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", LabQualityControlPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", LabQualityControlPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", LabQualityControlPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", LabQualityControlPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", LabQualityControlPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", LabQualityControlPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", LabQualityControlPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", LabQualityControlPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", LabQualityControlPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", LabQualityControlPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(LabQualityControlPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(LabQualityControlPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(LabQualityControlPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(LabQualityControlPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(LabQualityControlPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(LabQualityControlPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(LabQualityControlPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(LabQualityControlPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(LabQualityControlPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(LabQualityControlPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(null, null));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, LabQualityControlPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, LabQualityControlPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
