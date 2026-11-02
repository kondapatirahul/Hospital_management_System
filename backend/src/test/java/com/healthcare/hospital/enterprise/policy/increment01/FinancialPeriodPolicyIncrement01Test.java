package com.healthcare.hospital.enterprise.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.enterprise.policy.FinancialPeriodPolicy;

class FinancialPeriodPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", FinancialPeriodPolicy.normalizeStatus(null));
        assertEquals("NEW", FinancialPeriodPolicy.normalizeStatus(""));
        assertEquals("NEW", FinancialPeriodPolicy.normalizeStatus(" "));
        assertEquals("NEW", FinancialPeriodPolicy.normalizeStatus("new"));
        assertEquals("NEW", FinancialPeriodPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", FinancialPeriodPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", FinancialPeriodPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", FinancialPeriodPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", FinancialPeriodPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", FinancialPeriodPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", FinancialPeriodPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", FinancialPeriodPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", FinancialPeriodPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", FinancialPeriodPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", FinancialPeriodPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", FinancialPeriodPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", FinancialPeriodPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", FinancialPeriodPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", FinancialPeriodPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", FinancialPeriodPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", FinancialPeriodPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", FinancialPeriodPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", FinancialPeriodPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", FinancialPeriodPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", FinancialPeriodPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, FinancialPeriodPolicy.pageSize(-100));
        assertEquals(25, FinancialPeriodPolicy.pageSize(-1));
        assertEquals(25, FinancialPeriodPolicy.pageSize(0));
        assertEquals(1, FinancialPeriodPolicy.pageSize(1));
        assertEquals(2, FinancialPeriodPolicy.pageSize(2));
        assertEquals(5, FinancialPeriodPolicy.pageSize(5));
        assertEquals(10, FinancialPeriodPolicy.pageSize(10));
        assertEquals(20, FinancialPeriodPolicy.pageSize(20));
        assertEquals(24, FinancialPeriodPolicy.pageSize(24));
        assertEquals(25, FinancialPeriodPolicy.pageSize(25));
        assertEquals(26, FinancialPeriodPolicy.pageSize(26));
        assertEquals(49, FinancialPeriodPolicy.pageSize(49));
        assertEquals(50, FinancialPeriodPolicy.pageSize(50));
        assertEquals(75, FinancialPeriodPolicy.pageSize(75));
        assertEquals(99, FinancialPeriodPolicy.pageSize(99));
        assertEquals(100, FinancialPeriodPolicy.pageSize(100));
        assertEquals(101, FinancialPeriodPolicy.pageSize(101));
        assertEquals(150, FinancialPeriodPolicy.pageSize(150));
        assertEquals(199, FinancialPeriodPolicy.pageSize(199));
        assertEquals(200, FinancialPeriodPolicy.pageSize(200));
        assertEquals(200, FinancialPeriodPolicy.pageSize(201));
        assertEquals(200, FinancialPeriodPolicy.pageSize(250));
        assertEquals(200, FinancialPeriodPolicy.pageSize(500));
        assertEquals(200, FinancialPeriodPolicy.pageSize(1000));
        assertEquals(200, FinancialPeriodPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, FinancialPeriodPolicy.pageIndex(-100));
        assertEquals(0, FinancialPeriodPolicy.pageIndex(-10));
        assertEquals(0, FinancialPeriodPolicy.pageIndex(-2));
        assertEquals(0, FinancialPeriodPolicy.pageIndex(-1));
        assertEquals(0, FinancialPeriodPolicy.pageIndex(0));
        assertEquals(1, FinancialPeriodPolicy.pageIndex(1));
        assertEquals(2, FinancialPeriodPolicy.pageIndex(2));
        assertEquals(3, FinancialPeriodPolicy.pageIndex(3));
        assertEquals(4, FinancialPeriodPolicy.pageIndex(4));
        assertEquals(5, FinancialPeriodPolicy.pageIndex(5));
        assertEquals(6, FinancialPeriodPolicy.pageIndex(6));
        assertEquals(7, FinancialPeriodPolicy.pageIndex(7));
        assertEquals(8, FinancialPeriodPolicy.pageIndex(8));
        assertEquals(9, FinancialPeriodPolicy.pageIndex(9));
        assertEquals(10, FinancialPeriodPolicy.pageIndex(10));
        assertEquals(11, FinancialPeriodPolicy.pageIndex(11));
        assertEquals(12, FinancialPeriodPolicy.pageIndex(12));
        assertEquals(13, FinancialPeriodPolicy.pageIndex(13));
        assertEquals(14, FinancialPeriodPolicy.pageIndex(14));
        assertEquals(15, FinancialPeriodPolicy.pageIndex(15));
        assertEquals(20, FinancialPeriodPolicy.pageIndex(20));
        assertEquals(50, FinancialPeriodPolicy.pageIndex(50));
        assertEquals(100, FinancialPeriodPolicy.pageIndex(100));
        assertEquals(1000, FinancialPeriodPolicy.pageIndex(1000));
        assertEquals(99999, FinancialPeriodPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", FinancialPeriodPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", FinancialPeriodPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", FinancialPeriodPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", FinancialPeriodPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", FinancialPeriodPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", FinancialPeriodPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", FinancialPeriodPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", FinancialPeriodPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", FinancialPeriodPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", FinancialPeriodPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", FinancialPeriodPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", FinancialPeriodPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", FinancialPeriodPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", FinancialPeriodPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", FinancialPeriodPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", FinancialPeriodPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", FinancialPeriodPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", FinancialPeriodPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", FinancialPeriodPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", FinancialPeriodPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", FinancialPeriodPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", FinancialPeriodPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", FinancialPeriodPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", FinancialPeriodPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", FinancialPeriodPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", FinancialPeriodPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", FinancialPeriodPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", FinancialPeriodPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", FinancialPeriodPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", FinancialPeriodPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", FinancialPeriodPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", FinancialPeriodPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", FinancialPeriodPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", FinancialPeriodPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", FinancialPeriodPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", FinancialPeriodPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", FinancialPeriodPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", FinancialPeriodPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", FinancialPeriodPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", FinancialPeriodPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", FinancialPeriodPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", FinancialPeriodPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", FinancialPeriodPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", FinancialPeriodPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", FinancialPeriodPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", FinancialPeriodPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", FinancialPeriodPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", FinancialPeriodPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", FinancialPeriodPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", FinancialPeriodPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", FinancialPeriodPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", FinancialPeriodPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", FinancialPeriodPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", FinancialPeriodPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", FinancialPeriodPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", FinancialPeriodPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", FinancialPeriodPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", FinancialPeriodPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", FinancialPeriodPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", FinancialPeriodPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", FinancialPeriodPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", FinancialPeriodPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", FinancialPeriodPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", FinancialPeriodPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", FinancialPeriodPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", FinancialPeriodPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", FinancialPeriodPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", FinancialPeriodPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", FinancialPeriodPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", FinancialPeriodPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", FinancialPeriodPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", FinancialPeriodPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", FinancialPeriodPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", FinancialPeriodPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", FinancialPeriodPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), FinancialPeriodPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, FinancialPeriodPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, FinancialPeriodPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, FinancialPeriodPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", FinancialPeriodPolicy.normalizePriority("critical"));
        assertEquals("URGENT", FinancialPeriodPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", FinancialPeriodPolicy.normalizePriority("high"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("normal"));
        assertEquals("LOW", FinancialPeriodPolicy.normalizePriority("low"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority(null));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority(""));
        assertEquals("CRITICAL", FinancialPeriodPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", FinancialPeriodPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", FinancialPeriodPolicy.normalizePriority("low "));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", FinancialPeriodPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("none"));
        assertEquals("NORMAL", FinancialPeriodPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, FinancialPeriodPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", FinancialPeriodPolicy.redactForLog(""));
        assertEquals("****", FinancialPeriodPolicy.redactForLog("a"));
        assertEquals("****", FinancialPeriodPolicy.redactForLog("ab"));
        assertEquals("****", FinancialPeriodPolicy.redactForLog("abc"));
        assertEquals("****", FinancialPeriodPolicy.redactForLog("abcd"));
        assertEquals("ab***de", FinancialPeriodPolicy.redactForLog("abcde"));
        assertEquals("pa***23", FinancialPeriodPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", FinancialPeriodPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", FinancialPeriodPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", FinancialPeriodPolicy.redactForLog("Rahul"));
        assertEquals("do***01", FinancialPeriodPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", FinancialPeriodPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", FinancialPeriodPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", FinancialPeriodPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", FinancialPeriodPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", FinancialPeriodPolicy.redactForLog("test-value"));
        assertEquals("se***ef", FinancialPeriodPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", FinancialPeriodPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", FinancialPeriodPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", FinancialPeriodPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", FinancialPeriodPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", FinancialPeriodPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", FinancialPeriodPolicy.redactForLog("case-2026"));
        assertEquals("re***88", FinancialPeriodPolicy.redactForLog("record-88"));
        assertEquals("ad***77", FinancialPeriodPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", FinancialPeriodPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", FinancialPeriodPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", FinancialPeriodPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", FinancialPeriodPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", FinancialPeriodPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", FinancialPeriodPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", FinancialPeriodPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", FinancialPeriodPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", FinancialPeriodPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", FinancialPeriodPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", FinancialPeriodPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", FinancialPeriodPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", FinancialPeriodPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", FinancialPeriodPolicy.truncate("Access Review", 5));
        assertEquals("War…", FinancialPeriodPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", FinancialPeriodPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", FinancialPeriodPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", FinancialPeriodPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", FinancialPeriodPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", FinancialPeriodPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", FinancialPeriodPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", FinancialPeriodPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", FinancialPeriodPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", FinancialPeriodPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", FinancialPeriodPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(FinancialPeriodPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(FinancialPeriodPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(null, null));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, FinancialPeriodPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, FinancialPeriodPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
