package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.PaymentReconciliationPolicy;

class PaymentReconciliationPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", PaymentReconciliationPolicy.normalizeStatus(null));
        assertEquals("NEW", PaymentReconciliationPolicy.normalizeStatus(""));
        assertEquals("NEW", PaymentReconciliationPolicy.normalizeStatus(" "));
        assertEquals("NEW", PaymentReconciliationPolicy.normalizeStatus("new"));
        assertEquals("NEW", PaymentReconciliationPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", PaymentReconciliationPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", PaymentReconciliationPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", PaymentReconciliationPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", PaymentReconciliationPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", PaymentReconciliationPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", PaymentReconciliationPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", PaymentReconciliationPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", PaymentReconciliationPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", PaymentReconciliationPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", PaymentReconciliationPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", PaymentReconciliationPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", PaymentReconciliationPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", PaymentReconciliationPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", PaymentReconciliationPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", PaymentReconciliationPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", PaymentReconciliationPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", PaymentReconciliationPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", PaymentReconciliationPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", PaymentReconciliationPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", PaymentReconciliationPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, PaymentReconciliationPolicy.pageSize(-100));
        assertEquals(25, PaymentReconciliationPolicy.pageSize(-1));
        assertEquals(25, PaymentReconciliationPolicy.pageSize(0));
        assertEquals(1, PaymentReconciliationPolicy.pageSize(1));
        assertEquals(2, PaymentReconciliationPolicy.pageSize(2));
        assertEquals(5, PaymentReconciliationPolicy.pageSize(5));
        assertEquals(10, PaymentReconciliationPolicy.pageSize(10));
        assertEquals(20, PaymentReconciliationPolicy.pageSize(20));
        assertEquals(24, PaymentReconciliationPolicy.pageSize(24));
        assertEquals(25, PaymentReconciliationPolicy.pageSize(25));
        assertEquals(26, PaymentReconciliationPolicy.pageSize(26));
        assertEquals(49, PaymentReconciliationPolicy.pageSize(49));
        assertEquals(50, PaymentReconciliationPolicy.pageSize(50));
        assertEquals(75, PaymentReconciliationPolicy.pageSize(75));
        assertEquals(99, PaymentReconciliationPolicy.pageSize(99));
        assertEquals(100, PaymentReconciliationPolicy.pageSize(100));
        assertEquals(101, PaymentReconciliationPolicy.pageSize(101));
        assertEquals(150, PaymentReconciliationPolicy.pageSize(150));
        assertEquals(199, PaymentReconciliationPolicy.pageSize(199));
        assertEquals(200, PaymentReconciliationPolicy.pageSize(200));
        assertEquals(200, PaymentReconciliationPolicy.pageSize(201));
        assertEquals(200, PaymentReconciliationPolicy.pageSize(250));
        assertEquals(200, PaymentReconciliationPolicy.pageSize(500));
        assertEquals(200, PaymentReconciliationPolicy.pageSize(1000));
        assertEquals(200, PaymentReconciliationPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, PaymentReconciliationPolicy.pageIndex(-100));
        assertEquals(0, PaymentReconciliationPolicy.pageIndex(-10));
        assertEquals(0, PaymentReconciliationPolicy.pageIndex(-2));
        assertEquals(0, PaymentReconciliationPolicy.pageIndex(-1));
        assertEquals(0, PaymentReconciliationPolicy.pageIndex(0));
        assertEquals(1, PaymentReconciliationPolicy.pageIndex(1));
        assertEquals(2, PaymentReconciliationPolicy.pageIndex(2));
        assertEquals(3, PaymentReconciliationPolicy.pageIndex(3));
        assertEquals(4, PaymentReconciliationPolicy.pageIndex(4));
        assertEquals(5, PaymentReconciliationPolicy.pageIndex(5));
        assertEquals(6, PaymentReconciliationPolicy.pageIndex(6));
        assertEquals(7, PaymentReconciliationPolicy.pageIndex(7));
        assertEquals(8, PaymentReconciliationPolicy.pageIndex(8));
        assertEquals(9, PaymentReconciliationPolicy.pageIndex(9));
        assertEquals(10, PaymentReconciliationPolicy.pageIndex(10));
        assertEquals(11, PaymentReconciliationPolicy.pageIndex(11));
        assertEquals(12, PaymentReconciliationPolicy.pageIndex(12));
        assertEquals(13, PaymentReconciliationPolicy.pageIndex(13));
        assertEquals(14, PaymentReconciliationPolicy.pageIndex(14));
        assertEquals(15, PaymentReconciliationPolicy.pageIndex(15));
        assertEquals(20, PaymentReconciliationPolicy.pageIndex(20));
        assertEquals(50, PaymentReconciliationPolicy.pageIndex(50));
        assertEquals(100, PaymentReconciliationPolicy.pageIndex(100));
        assertEquals(1000, PaymentReconciliationPolicy.pageIndex(1000));
        assertEquals(99999, PaymentReconciliationPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", PaymentReconciliationPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PaymentReconciliationPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", PaymentReconciliationPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", PaymentReconciliationPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", PaymentReconciliationPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", PaymentReconciliationPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PaymentReconciliationPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PaymentReconciliationPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", PaymentReconciliationPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", PaymentReconciliationPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PaymentReconciliationPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", PaymentReconciliationPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", PaymentReconciliationPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PaymentReconciliationPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PaymentReconciliationPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PaymentReconciliationPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", PaymentReconciliationPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PaymentReconciliationPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", PaymentReconciliationPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", PaymentReconciliationPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", PaymentReconciliationPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PaymentReconciliationPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PaymentReconciliationPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PaymentReconciliationPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", PaymentReconciliationPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", PaymentReconciliationPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", PaymentReconciliationPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", PaymentReconciliationPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", PaymentReconciliationPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", PaymentReconciliationPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", PaymentReconciliationPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", PaymentReconciliationPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", PaymentReconciliationPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", PaymentReconciliationPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", PaymentReconciliationPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", PaymentReconciliationPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", PaymentReconciliationPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", PaymentReconciliationPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", PaymentReconciliationPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", PaymentReconciliationPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", PaymentReconciliationPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", PaymentReconciliationPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", PaymentReconciliationPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", PaymentReconciliationPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", PaymentReconciliationPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", PaymentReconciliationPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", PaymentReconciliationPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", PaymentReconciliationPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", PaymentReconciliationPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", PaymentReconciliationPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", PaymentReconciliationPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", PaymentReconciliationPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", PaymentReconciliationPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", PaymentReconciliationPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", PaymentReconciliationPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", PaymentReconciliationPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", PaymentReconciliationPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", PaymentReconciliationPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", PaymentReconciliationPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", PaymentReconciliationPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", PaymentReconciliationPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", PaymentReconciliationPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", PaymentReconciliationPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", PaymentReconciliationPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", PaymentReconciliationPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", PaymentReconciliationPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", PaymentReconciliationPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", PaymentReconciliationPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", PaymentReconciliationPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", PaymentReconciliationPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", PaymentReconciliationPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", PaymentReconciliationPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", PaymentReconciliationPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", PaymentReconciliationPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", PaymentReconciliationPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), PaymentReconciliationPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, PaymentReconciliationPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, PaymentReconciliationPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, PaymentReconciliationPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", PaymentReconciliationPolicy.normalizePriority("critical"));
        assertEquals("URGENT", PaymentReconciliationPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", PaymentReconciliationPolicy.normalizePriority("high"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("normal"));
        assertEquals("LOW", PaymentReconciliationPolicy.normalizePriority("low"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority(null));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority(""));
        assertEquals("CRITICAL", PaymentReconciliationPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", PaymentReconciliationPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", PaymentReconciliationPolicy.normalizePriority("low "));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", PaymentReconciliationPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("none"));
        assertEquals("NORMAL", PaymentReconciliationPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, PaymentReconciliationPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", PaymentReconciliationPolicy.redactForLog(""));
        assertEquals("****", PaymentReconciliationPolicy.redactForLog("a"));
        assertEquals("****", PaymentReconciliationPolicy.redactForLog("ab"));
        assertEquals("****", PaymentReconciliationPolicy.redactForLog("abc"));
        assertEquals("****", PaymentReconciliationPolicy.redactForLog("abcd"));
        assertEquals("ab***de", PaymentReconciliationPolicy.redactForLog("abcde"));
        assertEquals("pa***23", PaymentReconciliationPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", PaymentReconciliationPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", PaymentReconciliationPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", PaymentReconciliationPolicy.redactForLog("Rahul"));
        assertEquals("do***01", PaymentReconciliationPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", PaymentReconciliationPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", PaymentReconciliationPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", PaymentReconciliationPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", PaymentReconciliationPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", PaymentReconciliationPolicy.redactForLog("test-value"));
        assertEquals("se***ef", PaymentReconciliationPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", PaymentReconciliationPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", PaymentReconciliationPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", PaymentReconciliationPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", PaymentReconciliationPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", PaymentReconciliationPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", PaymentReconciliationPolicy.redactForLog("case-2026"));
        assertEquals("re***88", PaymentReconciliationPolicy.redactForLog("record-88"));
        assertEquals("ad***77", PaymentReconciliationPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", PaymentReconciliationPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", PaymentReconciliationPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", PaymentReconciliationPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", PaymentReconciliationPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", PaymentReconciliationPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", PaymentReconciliationPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", PaymentReconciliationPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", PaymentReconciliationPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", PaymentReconciliationPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", PaymentReconciliationPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", PaymentReconciliationPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", PaymentReconciliationPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", PaymentReconciliationPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", PaymentReconciliationPolicy.truncate("Access Review", 5));
        assertEquals("War…", PaymentReconciliationPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", PaymentReconciliationPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", PaymentReconciliationPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", PaymentReconciliationPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", PaymentReconciliationPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", PaymentReconciliationPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", PaymentReconciliationPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", PaymentReconciliationPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", PaymentReconciliationPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", PaymentReconciliationPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", PaymentReconciliationPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(PaymentReconciliationPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(PaymentReconciliationPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(null, null));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, PaymentReconciliationPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
