package com.healthcare.hospital.modules.policy.increment01;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.healthcare.hospital.modules.policy.ServiceRequestPolicy;

class ServiceRequestPolicyIncrement01Test {
    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");
    private static final LocalDate TODAY = LocalDate.parse("2026-06-15");

    @Test
    void normalizes_status_variants() {
        assertEquals("NEW", ServiceRequestPolicy.normalizeStatus(null));
        assertEquals("NEW", ServiceRequestPolicy.normalizeStatus(""));
        assertEquals("NEW", ServiceRequestPolicy.normalizeStatus(" "));
        assertEquals("NEW", ServiceRequestPolicy.normalizeStatus("new"));
        assertEquals("NEW", ServiceRequestPolicy.normalizeStatus(" New "));
        assertEquals("IN_PROGRESS", ServiceRequestPolicy.normalizeStatus("in progress"));
        assertEquals("IN_PROGRESS", ServiceRequestPolicy.normalizeStatus("IN PROGRESS"));
        assertEquals("IN_PROGRESS", ServiceRequestPolicy.normalizeStatus("in-progress"));
        assertEquals("UNDER_REVIEW", ServiceRequestPolicy.normalizeStatus("under review"));
        assertEquals("UNDER_REVIEW", ServiceRequestPolicy.normalizeStatus("under-review"));
        assertEquals("ON_HOLD", ServiceRequestPolicy.normalizeStatus("on hold"));
        assertEquals("ON_HOLD", ServiceRequestPolicy.normalizeStatus("on-hold"));
        assertEquals("CANCELLED", ServiceRequestPolicy.normalizeStatus("cancelled"));
        assertEquals("COMPLETED", ServiceRequestPolicy.normalizeStatus("completed"));
        assertEquals("ARCHIVED", ServiceRequestPolicy.normalizeStatus("archived"));
        assertEquals("ASSIGNED", ServiceRequestPolicy.normalizeStatus("assigned"));
        assertEquals("REJECTED", ServiceRequestPolicy.normalizeStatus("rejected"));
        assertEquals("APPROVED", ServiceRequestPolicy.normalizeStatus("approved"));
        assertEquals("PENDING", ServiceRequestPolicy.normalizeStatus("pending"));
        assertEquals("OPEN", ServiceRequestPolicy.normalizeStatus("open"));
        assertEquals("UNKNOWN_STATE", ServiceRequestPolicy.normalizeStatus("unknown state"));
        assertEquals("MIXED_CASE", ServiceRequestPolicy.normalizeStatus("mixed Case"));
        assertEquals("HIGH_PRIORITY", ServiceRequestPolicy.normalizeStatus("high-priority"));
        assertEquals("NEEDS_REVIEW", ServiceRequestPolicy.normalizeStatus("needs_review"));
        assertEquals("URGENT", ServiceRequestPolicy.normalizeStatus("  urgent  "));
    }

    @Test
    void clamps_page_size_boundaries() {
        assertEquals(25, ServiceRequestPolicy.pageSize(-100));
        assertEquals(25, ServiceRequestPolicy.pageSize(-1));
        assertEquals(25, ServiceRequestPolicy.pageSize(0));
        assertEquals(1, ServiceRequestPolicy.pageSize(1));
        assertEquals(2, ServiceRequestPolicy.pageSize(2));
        assertEquals(5, ServiceRequestPolicy.pageSize(5));
        assertEquals(10, ServiceRequestPolicy.pageSize(10));
        assertEquals(20, ServiceRequestPolicy.pageSize(20));
        assertEquals(24, ServiceRequestPolicy.pageSize(24));
        assertEquals(25, ServiceRequestPolicy.pageSize(25));
        assertEquals(26, ServiceRequestPolicy.pageSize(26));
        assertEquals(49, ServiceRequestPolicy.pageSize(49));
        assertEquals(50, ServiceRequestPolicy.pageSize(50));
        assertEquals(75, ServiceRequestPolicy.pageSize(75));
        assertEquals(99, ServiceRequestPolicy.pageSize(99));
        assertEquals(100, ServiceRequestPolicy.pageSize(100));
        assertEquals(101, ServiceRequestPolicy.pageSize(101));
        assertEquals(150, ServiceRequestPolicy.pageSize(150));
        assertEquals(199, ServiceRequestPolicy.pageSize(199));
        assertEquals(200, ServiceRequestPolicy.pageSize(200));
        assertEquals(200, ServiceRequestPolicy.pageSize(201));
        assertEquals(200, ServiceRequestPolicy.pageSize(250));
        assertEquals(200, ServiceRequestPolicy.pageSize(500));
        assertEquals(200, ServiceRequestPolicy.pageSize(1000));
        assertEquals(200, ServiceRequestPolicy.pageSize(99999));
    }

    @Test
    void clamps_page_index_boundaries() {
        assertEquals(0, ServiceRequestPolicy.pageIndex(-100));
        assertEquals(0, ServiceRequestPolicy.pageIndex(-10));
        assertEquals(0, ServiceRequestPolicy.pageIndex(-2));
        assertEquals(0, ServiceRequestPolicy.pageIndex(-1));
        assertEquals(0, ServiceRequestPolicy.pageIndex(0));
        assertEquals(1, ServiceRequestPolicy.pageIndex(1));
        assertEquals(2, ServiceRequestPolicy.pageIndex(2));
        assertEquals(3, ServiceRequestPolicy.pageIndex(3));
        assertEquals(4, ServiceRequestPolicy.pageIndex(4));
        assertEquals(5, ServiceRequestPolicy.pageIndex(5));
        assertEquals(6, ServiceRequestPolicy.pageIndex(6));
        assertEquals(7, ServiceRequestPolicy.pageIndex(7));
        assertEquals(8, ServiceRequestPolicy.pageIndex(8));
        assertEquals(9, ServiceRequestPolicy.pageIndex(9));
        assertEquals(10, ServiceRequestPolicy.pageIndex(10));
        assertEquals(11, ServiceRequestPolicy.pageIndex(11));
        assertEquals(12, ServiceRequestPolicy.pageIndex(12));
        assertEquals(13, ServiceRequestPolicy.pageIndex(13));
        assertEquals(14, ServiceRequestPolicy.pageIndex(14));
        assertEquals(15, ServiceRequestPolicy.pageIndex(15));
        assertEquals(20, ServiceRequestPolicy.pageIndex(20));
        assertEquals(50, ServiceRequestPolicy.pageIndex(50));
        assertEquals(100, ServiceRequestPolicy.pageIndex(100));
        assertEquals(1000, ServiceRequestPolicy.pageIndex(1000));
        assertEquals(99999, ServiceRequestPolicy.pageIndex(99999));
    }

    @Test
    void cleans_text_inputs() {
        assertEquals("Alpha", ServiceRequestPolicy.cleanText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ServiceRequestPolicy.cleanText("Patient intake", "field", 80));
        assertEquals("Ward round", ServiceRequestPolicy.cleanText("Ward round", "field", 80));
        assertEquals("Lab result", ServiceRequestPolicy.cleanText("Lab result", "field", 80));
        assertEquals("Medication batch", ServiceRequestPolicy.cleanText("Medication batch", "field", 80));
        assertEquals("Invoice review", ServiceRequestPolicy.cleanText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ServiceRequestPolicy.cleanText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ServiceRequestPolicy.cleanText("Staff credential", "field", 80));
        assertEquals("Access review", ServiceRequestPolicy.cleanText("Access review", "field", 80));
        assertEquals("Discharge checklist", ServiceRequestPolicy.cleanText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ServiceRequestPolicy.cleanText("Incident report", "field", 80));
        assertEquals("Referral note", ServiceRequestPolicy.cleanText("Referral note", "field", 80));
        assertEquals("Supplier contract", ServiceRequestPolicy.cleanText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ServiceRequestPolicy.cleanText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ServiceRequestPolicy.cleanText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ServiceRequestPolicy.cleanText("Consent record", "field", 80));
        assertEquals("Nursing observation", ServiceRequestPolicy.cleanText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ServiceRequestPolicy.cleanText("Patient transfer", "field", 80));
        assertEquals("Document registry", ServiceRequestPolicy.cleanText("Document registry", "field", 80));
        assertEquals("Financial period", ServiceRequestPolicy.cleanText("Financial period", "field", 80));
        assertEquals("Quality control", ServiceRequestPolicy.cleanText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ServiceRequestPolicy.cleanText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ServiceRequestPolicy.cleanText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ServiceRequestPolicy.cleanText("Refund request", "field", 80));
        assertEquals("Service request", ServiceRequestPolicy.cleanText("Service request", "field", 80));
    }

    @Test
    void requires_non_blank_text() {
        assertEquals("Alpha", ServiceRequestPolicy.requireText("  Alpha  ", "field", 80));
        assertEquals("Patient intake", ServiceRequestPolicy.requireText("Patient intake", "field", 80));
        assertEquals("Ward round", ServiceRequestPolicy.requireText("Ward round", "field", 80));
        assertEquals("Lab result", ServiceRequestPolicy.requireText("Lab result", "field", 80));
        assertEquals("Medication batch", ServiceRequestPolicy.requireText("Medication batch", "field", 80));
        assertEquals("Invoice review", ServiceRequestPolicy.requireText("Invoice review", "field", 80));
        assertEquals("Payment receipt", ServiceRequestPolicy.requireText("Payment receipt", "field", 80));
        assertEquals("Staff credential", ServiceRequestPolicy.requireText("Staff credential", "field", 80));
        assertEquals("Access review", ServiceRequestPolicy.requireText("Access review", "field", 80));
        assertEquals("Discharge checklist", ServiceRequestPolicy.requireText("Discharge checklist", "field", 80));
        assertEquals("Incident report", ServiceRequestPolicy.requireText("Incident report", "field", 80));
        assertEquals("Referral note", ServiceRequestPolicy.requireText("Referral note", "field", 80));
        assertEquals("Supplier contract", ServiceRequestPolicy.requireText("Supplier contract", "field", 80));
        assertEquals("Stock reconciliation", ServiceRequestPolicy.requireText("Stock reconciliation", "field", 80));
        assertEquals("Appointment waitlist", ServiceRequestPolicy.requireText("Appointment waitlist", "field", 80));
        assertEquals("Consent record", ServiceRequestPolicy.requireText("Consent record", "field", 80));
        assertEquals("Nursing observation", ServiceRequestPolicy.requireText("Nursing observation", "field", 80));
        assertEquals("Patient transfer", ServiceRequestPolicy.requireText("Patient transfer", "field", 80));
        assertEquals("Document registry", ServiceRequestPolicy.requireText("Document registry", "field", 80));
        assertEquals("Financial period", ServiceRequestPolicy.requireText("Financial period", "field", 80));
        assertEquals("Quality control", ServiceRequestPolicy.requireText("Quality control", "field", 80));
        assertEquals("Reagent inventory", ServiceRequestPolicy.requireText("Reagent inventory", "field", 80));
        assertEquals("Instrument calibration", ServiceRequestPolicy.requireText("Instrument calibration", "field", 80));
        assertEquals("Refund request", ServiceRequestPolicy.requireText("Refund request", "field", 80));
        assertEquals("Service request", ServiceRequestPolicy.requireText("Service request", "field", 80));
    }

    @Test
    void accepts_safe_reference_codes() {
        assertEquals("CF-0001", ServiceRequestPolicy.validateReference("CF-0001"));
        assertEquals("CF-0002", ServiceRequestPolicy.validateReference("CF-0002"));
        assertEquals("CF-0003", ServiceRequestPolicy.validateReference("CF-0003"));
        assertEquals("CF-0004", ServiceRequestPolicy.validateReference("CF-0004"));
        assertEquals("CF-0005", ServiceRequestPolicy.validateReference("CF-0005"));
        assertEquals("CF-0006", ServiceRequestPolicy.validateReference("CF-0006"));
        assertEquals("CF-0007", ServiceRequestPolicy.validateReference("CF-0007"));
        assertEquals("CF-0008", ServiceRequestPolicy.validateReference("CF-0008"));
        assertEquals("CF-0009", ServiceRequestPolicy.validateReference("CF-0009"));
        assertEquals("CF-0010", ServiceRequestPolicy.validateReference("CF-0010"));
        assertEquals("CF-0011", ServiceRequestPolicy.validateReference("CF-0011"));
        assertEquals("CF-0012", ServiceRequestPolicy.validateReference("CF-0012"));
        assertEquals("CF-0013", ServiceRequestPolicy.validateReference("CF-0013"));
        assertEquals("CF-0014", ServiceRequestPolicy.validateReference("CF-0014"));
        assertEquals("CF-0015", ServiceRequestPolicy.validateReference("CF-0015"));
        assertEquals("CF-0016", ServiceRequestPolicy.validateReference("CF-0016"));
        assertEquals("CF-0017", ServiceRequestPolicy.validateReference("CF-0017"));
        assertEquals("CF-0018", ServiceRequestPolicy.validateReference("CF-0018"));
        assertEquals("CF-0019", ServiceRequestPolicy.validateReference("CF-0019"));
        assertEquals("CF-0020", ServiceRequestPolicy.validateReference("CF-0020"));
        assertEquals("CF-0021", ServiceRequestPolicy.validateReference("CF-0021"));
        assertEquals("CF-0022", ServiceRequestPolicy.validateReference("CF-0022"));
        assertEquals("CF-0023", ServiceRequestPolicy.validateReference("CF-0023"));
        assertEquals("CF-0024", ServiceRequestPolicy.validateReference("CF-0024"));
        assertEquals("CF-0025", ServiceRequestPolicy.validateReference("CF-0025"));
    }

    @Test
    void normalizes_amount_scale() {
        assertEquals(new BigDecimal("0.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("0"), "amount"));
        assertEquals(new BigDecimal("0.10"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("0.1"), "amount"));
        assertEquals(new BigDecimal("0.50"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("0.5"), "amount"));
        assertEquals(new BigDecimal("1.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("1"), "amount"));
        assertEquals(new BigDecimal("1.25"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("1.25"), "amount"));
        assertEquals(new BigDecimal("2.50"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("2.5"), "amount"));
        assertEquals(new BigDecimal("5.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("5"), "amount"));
        assertEquals(new BigDecimal("10.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("10"), "amount"));
        assertEquals(new BigDecimal("12.34"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("12.34"), "amount"));
        assertEquals(new BigDecimal("20.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("20"), "amount"));
        assertEquals(new BigDecimal("25.55"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("25.55"), "amount"));
        assertEquals(new BigDecimal("50.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("50"), "amount"));
        assertEquals(new BigDecimal("75.75"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("75.75"), "amount"));
        assertEquals(new BigDecimal("99.99"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("99.99"), "amount"));
        assertEquals(new BigDecimal("100.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("100"), "amount"));
        assertEquals(new BigDecimal("125.50"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("125.5"), "amount"));
        assertEquals(new BigDecimal("250.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("250"), "amount"));
        assertEquals(new BigDecimal("500.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("500"), "amount"));
        assertEquals(new BigDecimal("999.99"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("999.99"), "amount"));
        assertEquals(new BigDecimal("1000.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("1000"), "amount"));
        assertEquals(new BigDecimal("1250.25"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("1250.25"), "amount"));
        assertEquals(new BigDecimal("5000.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("5000"), "amount"));
        assertEquals(new BigDecimal("10000.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("10000"), "amount"));
        assertEquals(new BigDecimal("99999.99"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("99999.99"), "amount"));
        assertEquals(new BigDecimal("100000.00"), ServiceRequestPolicy.nonNegativeAmount(new BigDecimal("100000"), "amount"));
    }

    @Test
    void matches_search_case_insensitively() {
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Patient Registration"), "patient"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Patient Registration"), "PATIENT"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Patient Registration"), "registration"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Appointment Queue"), "queue"));
        assertEquals(false, ServiceRequestPolicy.matchesSearch(List.of("Laboratory Result"), "lab"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Lab Instrument"), "lab"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Laboratory Result"), "result"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Pharmacy Inventory"), "PHARMACY"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Billing Invoice"), "bill"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Doctor Schedule"), "schedule"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Nursing Observation"), "nursing"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Ward Transfer"), "transfer"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Consent Record"), "consent"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Discharge Checklist"), "check"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Incident Report"), "incident"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Quality Review"), "quality"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Supplier Contract"), "supplier"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Refund Request"), "refund"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Payment Reconciliation"), "payment"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Access Review"), "access"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Clinical Handover"), "handover"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Staff Credential"), "credential"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Lab Instrument"), "instrument"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Stock Take"), "stock"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Patient Referral"), "referral"));
        assertEquals(true, ServiceRequestPolicy.matchesSearch(List.of("Document Registry"), "document"));
    }

    @Test
    void checks_due_soon_window() {
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-01"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-02"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-03"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-04"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-05"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-06"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-07"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-08"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-09"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-10"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-11"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-12"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-13"), TODAY, 10));
        assertEquals(false, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-14"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-15"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-16"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-17"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-18"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-19"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-20"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-21"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-22"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-23"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-24"), TODAY, 10));
        assertEquals(true, ServiceRequestPolicy.isDueSoon(LocalDate.parse("2026-06-25"), TODAY, 10));
    }
    @Test
    void normalizes_priority_values() {
        assertEquals("CRITICAL", ServiceRequestPolicy.normalizePriority("critical"));
        assertEquals("URGENT", ServiceRequestPolicy.normalizePriority("urgent"));
        assertEquals("HIGH", ServiceRequestPolicy.normalizePriority("high"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("normal"));
        assertEquals("LOW", ServiceRequestPolicy.normalizePriority("low"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("invalid"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority(null));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority(""));
        assertEquals("CRITICAL", ServiceRequestPolicy.normalizePriority(" Critical "));
        assertEquals("HIGH", ServiceRequestPolicy.normalizePriority("HIGH "));
        assertEquals("LOW", ServiceRequestPolicy.normalizePriority("low "));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("P1"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("P2"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("P3"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("immediate"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("routine"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("medium"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("High Priority"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("URGENT!"));
        assertEquals("CRITICAL", ServiceRequestPolicy.normalizePriority("CRITICAL "));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority(" normal"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("LOWEST"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("nil"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("none"));
        assertEquals("NORMAL", ServiceRequestPolicy.normalizePriority("unknown"));
    }

    @Test
    void calculates_elapsed_hours() {
        assertEquals(0L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(0L * 3600L), NOW));
        assertEquals(1L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(1L * 3600L), NOW));
        assertEquals(2L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(2L * 3600L), NOW));
        assertEquals(3L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(3L * 3600L), NOW));
        assertEquals(4L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(4L * 3600L), NOW));
        assertEquals(5L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(5L * 3600L), NOW));
        assertEquals(6L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(6L * 3600L), NOW));
        assertEquals(7L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(7L * 3600L), NOW));
        assertEquals(8L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(8L * 3600L), NOW));
        assertEquals(9L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(9L * 3600L), NOW));
        assertEquals(10L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(10L * 3600L), NOW));
        assertEquals(12L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(12L * 3600L), NOW));
        assertEquals(18L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(18L * 3600L), NOW));
        assertEquals(24L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(24L * 3600L), NOW));
        assertEquals(36L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(36L * 3600L), NOW));
        assertEquals(48L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(48L * 3600L), NOW));
        assertEquals(72L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(72L * 3600L), NOW));
        assertEquals(96L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(96L * 3600L), NOW));
        assertEquals(120L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(120L * 3600L), NOW));
        assertEquals(168L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(168L * 3600L), NOW));
        assertEquals(240L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(240L * 3600L), NOW));
        assertEquals(336L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(336L * 3600L), NOW));
        assertEquals(504L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(504L * 3600L), NOW));
        assertEquals(720L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(720L * 3600L), NOW));
        assertEquals(1000L, ServiceRequestPolicy.ageInHours(NOW.minusSeconds(1000L * 3600L), NOW));
    }

    @Test
    void redacts_short_and_long_identifiers() {
        assertEquals("", ServiceRequestPolicy.redactForLog(""));
        assertEquals("****", ServiceRequestPolicy.redactForLog("a"));
        assertEquals("****", ServiceRequestPolicy.redactForLog("ab"));
        assertEquals("****", ServiceRequestPolicy.redactForLog("abc"));
        assertEquals("****", ServiceRequestPolicy.redactForLog("abcd"));
        assertEquals("ab***de", ServiceRequestPolicy.redactForLog("abcde"));
        assertEquals("pa***23", ServiceRequestPolicy.redactForLog("patient-123"));
        assertEquals("IN***01", ServiceRequestPolicy.redactForLog("INV-2026-001"));
        assertEquals("98***10", ServiceRequestPolicy.redactForLog("9876543210"));
        assertEquals("Ra***ul", ServiceRequestPolicy.redactForLog("Rahul"));
        assertEquals("do***01", ServiceRequestPolicy.redactForLog("doctor-01"));
        assertEquals("LA***01", ServiceRequestPolicy.redactForLog("LAB/2026/01"));
        assertEquals("WA***12", ServiceRequestPolicy.redactForLog("WARD-A-12"));
        assertEquals("CF***01", ServiceRequestPolicy.redactForLog("CF-0001"));
        assertEquals("re***00", ServiceRequestPolicy.redactForLog("receipt-900"));
        assertEquals("te***ue", ServiceRequestPolicy.redactForLog("test-value"));
        assertEquals("se***ef", ServiceRequestPolicy.redactForLog("secure-ref"));
        assertEquals("AB***34", ServiceRequestPolicy.redactForLog("ABCD-1234"));
        assertEquals("ME***01", ServiceRequestPolicy.redactForLog("MED-001"));
        assertEquals("ST***22", ServiceRequestPolicy.redactForLog("STAFF-22"));
        assertEquals("pa***01", ServiceRequestPolicy.redactForLog("patient-001"));
        assertEquals("bi***55", ServiceRequestPolicy.redactForLog("billing-55"));
        assertEquals("ca***26", ServiceRequestPolicy.redactForLog("case-2026"));
        assertEquals("re***88", ServiceRequestPolicy.redactForLog("record-88"));
        assertEquals("ad***77", ServiceRequestPolicy.redactForLog("admin-777"));
    }

    @Test
    void truncates_labels_at_requested_length() {
        assertEquals("Pati…", ServiceRequestPolicy.truncate("Patient Registration", 5));
        assertEquals("App…", ServiceRequestPolicy.truncate("Appointment", 4));
        assertEquals("Labor…", ServiceRequestPolicy.truncate("Laboratory", 6));
        assertEquals("Ph…", ServiceRequestPolicy.truncate("Pharmacy", 3));
        assertEquals("Billin…", ServiceRequestPolicy.truncate("Billing Invoice", 7));
        assertEquals("Doctor …", ServiceRequestPolicy.truncate("Doctor Schedule", 8));
        assertEquals("Nursing", ServiceRequestPolicy.truncate("Nursing", 20));
        assertEquals("Discharge…", ServiceRequestPolicy.truncate("Discharge Checklist", 10));
        assertEquals("Incident…", ServiceRequestPolicy.truncate("Incident Report", 9));
        assertEquals("Qualit…", ServiceRequestPolicy.truncate("Quality Review", 7));
        assertEquals("Supplie…", ServiceRequestPolicy.truncate("Supplier Contract", 8));
        assertEquals("Refun…", ServiceRequestPolicy.truncate("Refund Request", 6));
        assertEquals("Payment R…", ServiceRequestPolicy.truncate("Payment Reconciliation", 10));
        assertEquals("Acce…", ServiceRequestPolicy.truncate("Access Review", 5));
        assertEquals("War…", ServiceRequestPolicy.truncate("Ward Round", 4));
        assertEquals("Stoc…", ServiceRequestPolicy.truncate("Stock Take", 5));
        assertEquals("Lab I…", ServiceRequestPolicy.truncate("Lab Instrument", 6));
        assertEquals("Staff …", ServiceRequestPolicy.truncate("Staff Credential", 7));
        assertEquals("Patient…", ServiceRequestPolicy.truncate("Patient Transfer", 8));
        assertEquals("Document…", ServiceRequestPolicy.truncate("Document Registry", 9));
        assertEquals("Clinica…", ServiceRequestPolicy.truncate("Clinical Handover", 8));
        assertEquals("Conse…", ServiceRequestPolicy.truncate("Consent Record", 6));
        assertEquals("Financ…", ServiceRequestPolicy.truncate("Financial Period", 7));
        assertEquals("Servi…", ServiceRequestPolicy.truncate("Service Request", 6));
        assertEquals("Medicat…", ServiceRequestPolicy.truncate("Medication Batch", 8));
    }
    @Test
    void compares_priority_order() {
        assertTrue(ServiceRequestPolicy.comparePriority("CRITICAL", "URGENT") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("URGENT", "HIGH") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("HIGH", "NORMAL") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("NORMAL", "LOW") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("CRITICAL", "LOW") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("LOW", "CRITICAL") > 0);
        assertTrue(ServiceRequestPolicy.comparePriority("LOW", "NORMAL") > 0);
        assertTrue(ServiceRequestPolicy.comparePriority("NORMAL", "HIGH") > 0);
        assertTrue(ServiceRequestPolicy.comparePriority("HIGH", "URGENT") > 0);
        assertTrue(ServiceRequestPolicy.comparePriority("URGENT", "CRITICAL") > 0);
        assertTrue(ServiceRequestPolicy.comparePriority("NORMAL", "NORMAL") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("HIGH", "HIGH") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("LOW", "LOW") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("CRITICAL", "CRITICAL") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("URGENT", "URGENT") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("unknown", "NORMAL") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("NORMAL", "unknown") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority(null, "NORMAL") == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("NORMAL", null) == 0);
        assertTrue(ServiceRequestPolicy.comparePriority("CRITICAL", "HIGH") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("HIGH", "LOW") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("URGENT", "LOW") < 0);
        assertTrue(ServiceRequestPolicy.comparePriority("LOW", "HIGH") > 0);
        assertTrue(ServiceRequestPolicy.comparePriority("NORMAL", "CRITICAL") > 0);
        assertTrue(ServiceRequestPolicy.comparePriority("HIGH", "CRITICAL") > 0);
    }

    @Test
    void evaluates_approval_threshold() {
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("100.00")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("99.99"), new BigDecimal("100.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("100.01"), new BigDecimal("100.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("500.00"), new BigDecimal("100.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("9.99"), new BigDecimal("10.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("1000.00"), new BigDecimal("999.99")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("999.98"), new BigDecimal("999.99")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(null, new BigDecimal("1.00")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("1.00"), null));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(null, null));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("250.00"), new BigDecimal("250.00")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("249.99"), new BigDecimal("250.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("0.01"), new BigDecimal("0.01")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.01")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("7500.00"), new BigDecimal("5000.00")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("4999.99"), new BigDecimal("5000.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("150.00"), new BigDecimal("100.00")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("200.00"), new BigDecimal("200.00")));
        assertEquals(false, ServiceRequestPolicy.requiresApproval(new BigDecimal("199.99"), new BigDecimal("200.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("1.00"), new BigDecimal("0.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertEquals(true, ServiceRequestPolicy.requiresApproval(new BigDecimal("100000.00"), new BigDecimal("99999.99")));
    }

}
