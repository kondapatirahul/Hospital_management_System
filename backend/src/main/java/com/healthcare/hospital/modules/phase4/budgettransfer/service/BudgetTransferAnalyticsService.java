package com.healthcare.hospital.modules.phase4.budgettransfer.service;

import com.healthcare.hospital.modules.phase4.budgettransfer.entity.BudgetTransfer;
import com.healthcare.hospital.modules.phase4.budgettransfer.repository.BudgetTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Explainable domain signal catalog and analytics for budget transfer records. */
@Service
@Transactional(readOnly=true)
public class BudgetTransferAnalyticsService {
    private final BudgetTransferRepository repository;
    public BudgetTransferAnalyticsService(BudgetTransferRepository repository) { this.repository = repository; }

    public Map<String,Object> metrics() {
        List<BudgetTransfer> records = repository.findAll();
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("totalRecords", records.size());
        result.put("openRecords", records.stream().filter(r -> r.getStatus()!=null && !r.getStatus().equalsIgnoreCase("CLOSED")).count());
        result.put("overdueRecords", records.stream().filter(r -> r.getDueDate()!=null && r.getDueDate().isBefore(LocalDate.now())).count());
        result.put("missingOwners", records.stream().filter(r -> r.getOwner()==null || r.getOwner().isBlank()).count());
        result.put("missingEvidence", records.stream().filter(r -> r.getEvidenceReference()==null || r.getEvidenceReference().isBlank()).count());
        result.put("totalValue", records.stream().map(r -> r.getAmount()==null?BigDecimal.ZERO:r.getAmount()).reduce(BigDecimal.ZERO,BigDecimal::add));
        result.put("statusDistribution", records.stream().collect(Collectors.groupingBy(r -> r.getStatus()==null?"UNSET":r.getStatus(),TreeMap::new,Collectors.counting())));
        result.put("departmentDistribution", records.stream().collect(Collectors.groupingBy(r -> r.getDepartment()==null?"UNASSIGNED":r.getDepartment(),TreeMap::new,Collectors.counting())));
        return Collections.unmodifiableMap(result);
    }

    public List<Map<String,Object>> riskRegister() {
        List<Map<String,Object>> rows=new ArrayList<>();
        for(BudgetTransfer record:repository.findAll()) {
            int score=0;
            if(record.getDueDate()!=null && record.getDueDate().isBefore(LocalDate.now())) score+=30;
            if(record.getOwner()==null || record.getOwner().isBlank()) score+=15;
            if(record.getEvidenceReference()==null || record.getEvidenceReference().isBlank()) score+=20;
            if(record.getAmount()!=null && record.getAmount().compareTo(new BigDecimal("25000"))>=0) score+=15;
            if(record.getStatus()==null || record.getStatus().isBlank()) score+=20;
            Map<String,Object> row=new LinkedHashMap<>(); row.put("id",record.getId()); row.put("title",record.getTitle()); row.put("score",Math.min(100,score)); row.put("band",score>=60?"HIGH":score>=30?"MEDIUM":"LOW"); rows.add(row);
        }
        rows.sort(Comparator.comparing((Map<String,Object> row)->(Integer)row.get("score")).reversed());
        return rows;
    }

    public List<BudgetTransfer> overdueItems(LocalDate asOf) {
        LocalDate date=asOf==null?LocalDate.now():asOf;
        return repository.findAll().stream().filter(r->r.getDueDate()!=null&&r.getDueDate().isBefore(date))
          .sorted(Comparator.comparing(BudgetTransfer::getDueDate)).collect(Collectors.toList());
    }

    public List<BudgetTransfer> missingEvidence() {
        return repository.findAll().stream().filter(r->r.getEvidenceReference()==null||r.getEvidenceReference().isBlank()).collect(Collectors.toList());
    }

    public List<BudgetTransfer> unassignedItems() {
        return repository.findAll().stream().filter(r->r.getOwner()==null||r.getOwner().isBlank()).collect(Collectors.toList());
    }

    public Map<String,Long> agingBuckets() {
        Map<String,Long> buckets=new LinkedHashMap<>(); buckets.put("0-7",0L); buckets.put("8-30",0L); buckets.put("31-60",0L); buckets.put("61+",0L); buckets.put("NO_DUE_DATE",0L);
        for(BudgetTransfer r:repository.findAll()) { if(r.getDueDate()==null) buckets.compute("NO_DUE_DATE",(k,v)->v+1); else { long age=java.time.temporal.ChronoUnit.DAYS.between(r.getDueDate(),LocalDate.now()); String key=age<=7?"0-7":age<=30?"8-30":age<=60?"31-60":"61+"; if(age<0)key="0-7"; buckets.compute(key,(k,v)->v+1); } }
        return buckets;
    }

    /** Evaluates the source signal #1 for a budget transfer record. */
    public boolean isRequiredSource1(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the destination signal #2 for a budget transfer record. */
    public boolean isRequiredDestination2(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the approval signal #3 for a budget transfer record. */
    public boolean isRequiredApproval3(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #4 for a budget transfer record. */
    public boolean isRequiredPosting4(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #5 for a budget transfer record. */
    public boolean isRequiredSegregation5(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the period signal #6 for a budget transfer record. */
    public boolean isRequiredPeriod6(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #7 for a budget transfer record. */
    public boolean isRequiredLimit7(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the variance signal #8 for a budget transfer record. */
    public boolean isRequiredVariance8(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #9 for a budget transfer record. */
    public boolean isRequiredDepartment9(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #10 for a budget transfer record. */
    public boolean isRequiredReconciliation10(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #11 for a budget transfer record. */
    public boolean isReadySource11(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the destination signal #12 for a budget transfer record. */
    public boolean isReadyDestination12(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the approval signal #13 for a budget transfer record. */
    public boolean isReadyApproval13(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #14 for a budget transfer record. */
    public boolean isReadyPosting14(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #15 for a budget transfer record. */
    public boolean isReadySegregation15(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the period signal #16 for a budget transfer record. */
    public boolean isReadyPeriod16(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #17 for a budget transfer record. */
    public boolean isReadyLimit17(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the variance signal #18 for a budget transfer record. */
    public boolean isReadyVariance18(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #19 for a budget transfer record. */
    public boolean isReadyDepartment19(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #20 for a budget transfer record. */
    public boolean isReadyReconciliation20(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #21 for a budget transfer record. */
    public boolean needsReviewSource21(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the destination signal #22 for a budget transfer record. */
    public boolean needsReviewDestination22(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the approval signal #23 for a budget transfer record. */
    public boolean needsReviewApproval23(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #24 for a budget transfer record. */
    public boolean needsReviewPosting24(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #25 for a budget transfer record. */
    public boolean needsReviewSegregation25(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the period signal #26 for a budget transfer record. */
    public boolean needsReviewPeriod26(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #27 for a budget transfer record. */
    public boolean needsReviewLimit27(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the variance signal #28 for a budget transfer record. */
    public boolean needsReviewVariance28(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #29 for a budget transfer record. */
    public boolean needsReviewDepartment29(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #30 for a budget transfer record. */
    public boolean needsReviewReconciliation30(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #31 for a budget transfer record. */
    public boolean isOverdueSource31(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the destination signal #32 for a budget transfer record. */
    public boolean isOverdueDestination32(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the approval signal #33 for a budget transfer record. */
    public boolean isOverdueApproval33(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #34 for a budget transfer record. */
    public boolean isOverduePosting34(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #35 for a budget transfer record. */
    public boolean isOverdueSegregation35(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the period signal #36 for a budget transfer record. */
    public boolean isOverduePeriod36(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #37 for a budget transfer record. */
    public boolean isOverdueLimit37(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the variance signal #38 for a budget transfer record. */
    public boolean isOverdueVariance38(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #39 for a budget transfer record. */
    public boolean isOverdueDepartment39(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #40 for a budget transfer record. */
    public boolean isOverdueReconciliation40(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #41 for a budget transfer record. */
    public boolean isDueSoonSource41(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the destination signal #42 for a budget transfer record. */
    public boolean isDueSoonDestination42(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the approval signal #43 for a budget transfer record. */
    public boolean isDueSoonApproval43(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #44 for a budget transfer record. */
    public boolean isDueSoonPosting44(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #45 for a budget transfer record. */
    public boolean isDueSoonSegregation45(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the period signal #46 for a budget transfer record. */
    public boolean isDueSoonPeriod46(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #47 for a budget transfer record. */
    public boolean isDueSoonLimit47(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the variance signal #48 for a budget transfer record. */
    public boolean isDueSoonVariance48(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #49 for a budget transfer record. */
    public boolean isDueSoonDepartment49(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #50 for a budget transfer record. */
    public boolean isDueSoonReconciliation50(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #51 for a budget transfer record. */
    public boolean isHighRiskSource51(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the destination signal #52 for a budget transfer record. */
    public boolean isHighRiskDestination52(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the approval signal #53 for a budget transfer record. */
    public boolean isHighRiskApproval53(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #54 for a budget transfer record. */
    public boolean isHighRiskPosting54(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #55 for a budget transfer record. */
    public boolean isHighRiskSegregation55(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the period signal #56 for a budget transfer record. */
    public boolean isHighRiskPeriod56(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #57 for a budget transfer record. */
    public boolean isHighRiskLimit57(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the variance signal #58 for a budget transfer record. */
    public boolean isHighRiskVariance58(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #59 for a budget transfer record. */
    public boolean isHighRiskDepartment59(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #60 for a budget transfer record. */
    public boolean isHighRiskReconciliation60(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #61 for a budget transfer record. */
    public boolean isMaterialSource61(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the destination signal #62 for a budget transfer record. */
    public boolean isMaterialDestination62(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the approval signal #63 for a budget transfer record. */
    public boolean isMaterialApproval63(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #64 for a budget transfer record. */
    public boolean isMaterialPosting64(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #65 for a budget transfer record. */
    public boolean isMaterialSegregation65(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the period signal #66 for a budget transfer record. */
    public boolean isMaterialPeriod66(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #67 for a budget transfer record. */
    public boolean isMaterialLimit67(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the variance signal #68 for a budget transfer record. */
    public boolean isMaterialVariance68(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #69 for a budget transfer record. */
    public boolean isMaterialDepartment69(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #70 for a budget transfer record. */
    public boolean isMaterialReconciliation70(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #71 for a budget transfer record. */
    public boolean hasEvidenceSource71(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the destination signal #72 for a budget transfer record. */
    public boolean hasEvidenceDestination72(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the approval signal #73 for a budget transfer record. */
    public boolean hasEvidenceApproval73(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #74 for a budget transfer record. */
    public boolean hasEvidencePosting74(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #75 for a budget transfer record. */
    public boolean hasEvidenceSegregation75(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the period signal #76 for a budget transfer record. */
    public boolean hasEvidencePeriod76(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #77 for a budget transfer record. */
    public boolean hasEvidenceLimit77(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the variance signal #78 for a budget transfer record. */
    public boolean hasEvidenceVariance78(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #79 for a budget transfer record. */
    public boolean hasEvidenceDepartment79(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #80 for a budget transfer record. */
    public boolean hasEvidenceReconciliation80(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #81 for a budget transfer record. */
    public boolean isCompleteSource81(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the destination signal #82 for a budget transfer record. */
    public boolean isCompleteDestination82(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the approval signal #83 for a budget transfer record. */
    public boolean isCompleteApproval83(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #84 for a budget transfer record. */
    public boolean isCompletePosting84(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #85 for a budget transfer record. */
    public boolean isCompleteSegregation85(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the period signal #86 for a budget transfer record. */
    public boolean isCompletePeriod86(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #87 for a budget transfer record. */
    public boolean isCompleteLimit87(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the variance signal #88 for a budget transfer record. */
    public boolean isCompleteVariance88(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #89 for a budget transfer record. */
    public boolean isCompleteDepartment89(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #90 for a budget transfer record. */
    public boolean isCompleteReconciliation90(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the source signal #91 for a budget transfer record. */
    public boolean requiresEscalationSource91(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the destination signal #92 for a budget transfer record. */
    public boolean requiresEscalationDestination92(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the approval signal #93 for a budget transfer record. */
    public boolean requiresEscalationApproval93(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the posting signal #94 for a budget transfer record. */
    public boolean requiresEscalationPosting94(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the segregation signal #95 for a budget transfer record. */
    public boolean requiresEscalationSegregation95(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the period signal #96 for a budget transfer record. */
    public boolean requiresEscalationPeriod96(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the limit signal #97 for a budget transfer record. */
    public boolean requiresEscalationLimit97(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the variance signal #98 for a budget transfer record. */
    public boolean requiresEscalationVariance98(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the department signal #99 for a budget transfer record. */
    public boolean requiresEscalationDepartment99(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the reconciliation signal #100 for a budget transfer record. */
    public boolean requiresEscalationReconciliation100(BudgetTransfer record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }

}
