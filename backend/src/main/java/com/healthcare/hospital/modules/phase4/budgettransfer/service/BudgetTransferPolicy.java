package com.healthcare.hospital.modules.phase4.budgettransfer.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Pure domain policy rules for budget transfers; kept separate for deterministic tests. */
public final class BudgetTransferPolicy {
 private BudgetTransferPolicy() {}
 public static final List<String> STATUS_ORDER=List.of("REQUESTED", "REVIEW", "APPROVED", "POSTED", "REJECTED", "CANCELLED");
 public static boolean isKnownStatus(String status) { return status!=null&&STATUS_ORDER.contains(status.trim().toUpperCase(Locale.ROOT)); }
 public static boolean isTerminal(String status) { return status!=null&&Set.of("CANCELLED").contains(status.trim().toUpperCase(Locale.ROOT)); }
 public static long daysUntil(LocalDate date) { return date==null?Long.MAX_VALUE:ChronoUnit.DAYS.between(LocalDate.now(),date); }
 public static String urgency(LocalDate date) { long days=daysUntil(date); if(days<0)return "OVERDUE"; if(days<=2)return "CRITICAL"; if(days<=7)return "HIGH"; if(days<=30)return "NORMAL"; return "LOW"; }
 public static BigDecimal safeAmount(BigDecimal amount) { return amount==null?BigDecimal.ZERO:amount.max(BigDecimal.ZERO); }
 public static BigDecimal percentage(BigDecimal numerator,BigDecimal denominator) { if(denominator==null||denominator.signum()==0)return BigDecimal.ZERO; return safeAmount(numerator).multiply(new BigDecimal("100")).divide(denominator,2,java.math.RoundingMode.HALF_UP); }
 public static List<String> validateRequired(String title,String owner,String reference) { List<String> errors=new ArrayList<>(); if(title==null||title.isBlank())errors.add("Title is required"); if(owner==null||owner.isBlank())errors.add("Owner is required"); if(reference!=null&&reference.length()>100)errors.add("Reference is too long"); return errors; }
 public static Map<String,Object> classify(BigDecimal amount,LocalDate dueDate,String status,boolean evidence) { Map<String,Object> result=new LinkedHashMap<>(); result.put("amount",safeAmount(amount)); result.put("dueInDays",daysUntil(dueDate)); result.put("urgency",urgency(dueDate)); result.put("terminal",isTerminal(status)); result.put("evidencePresent",evidence); result.put("statusKnown",isKnownStatus(status)); return result; }
    /** Policy check: validateSourceBalance for budget transfers. */
    public static boolean validateSourceBalance(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("5000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return large || !evidencePresent;
    }
    /** Policy check: validateDestination for budget transfers. */
    public static boolean validateDestination(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("10000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return due && !terminal;
    }
    /** Policy check: transferRiskBand for budget transfers. */
    public static boolean transferRiskBand(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("25000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return large && !approved;
    }
    /** Policy check: requiresDualApproval for budget transfers. */
    public static boolean requiresDualApproval(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("50000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return evidencePresent && !terminal;
    }
    /** Policy check: segregationOfDuties for budget transfers. */
    public static boolean segregationOfDuties(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("100000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return !terminal && (due || large);
    }
    /** Policy check: isCrossDepartment for budget transfers. */
    public static boolean isCrossDepartment(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("5000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return approved && evidencePresent;
    }
    /** Policy check: postingReadiness for budget transfers. */
    public static boolean postingReadiness(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("10000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return !terminal && evidencePresent;
    }
    /** Policy check: periodOpen for budget transfers. */
    public static boolean periodOpen(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("25000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return large || due;
    }
    /** Policy check: approvalCompleteness for budget transfers. */
    public static boolean approvalCompleteness(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("50000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return !terminal && approved;
    }
    /** Policy check: transferLimitExceeded for budget transfers. */
    public static boolean transferLimitExceeded(BigDecimal amount, LocalDate dueDate, String status, boolean evidencePresent, boolean approved) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        LocalDate today = LocalDate.now();
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        boolean due = dueDate != null && !dueDate.isAfter(today);
        boolean large = safe.compareTo(new BigDecimal("100000")) >= 0;
        boolean terminal = Set.of("CLOSED", "PAID", "RETIRED", "EXPIRED", "REJECTED", "CANCELLED", "ACCEPTED").contains(normalized);
        return due && !evidencePresent;
    }

}
