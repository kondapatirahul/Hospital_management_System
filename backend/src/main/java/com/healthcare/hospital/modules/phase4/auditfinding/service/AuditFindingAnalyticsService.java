package com.healthcare.hospital.modules.phase4.auditfinding.service;

import com.healthcare.hospital.modules.phase4.auditfinding.entity.AuditFinding;
import com.healthcare.hospital.modules.phase4.auditfinding.repository.AuditFindingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Explainable domain signal catalog and analytics for audit finding records. */
@Service
@Transactional(readOnly=true)
public class AuditFindingAnalyticsService {
    private final AuditFindingRepository repository;
    public AuditFindingAnalyticsService(AuditFindingRepository repository) { this.repository = repository; }

    public Map<String,Object> metrics() {
        List<AuditFinding> records = repository.findAll();
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
        for(AuditFinding record:repository.findAll()) {
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

    public List<AuditFinding> overdueItems(LocalDate asOf) {
        LocalDate date=asOf==null?LocalDate.now():asOf;
        return repository.findAll().stream().filter(r->r.getDueDate()!=null&&r.getDueDate().isBefore(date))
          .sorted(Comparator.comparing(AuditFinding::getDueDate)).collect(Collectors.toList());
    }

    public List<AuditFinding> missingEvidence() {
        return repository.findAll().stream().filter(r->r.getEvidenceReference()==null||r.getEvidenceReference().isBlank()).collect(Collectors.toList());
    }

    public List<AuditFinding> unassignedItems() {
        return repository.findAll().stream().filter(r->r.getOwner()==null||r.getOwner().isBlank()).collect(Collectors.toList());
    }

    public Map<String,Long> agingBuckets() {
        Map<String,Long> buckets=new LinkedHashMap<>(); buckets.put("0-7",0L); buckets.put("8-30",0L); buckets.put("31-60",0L); buckets.put("61+",0L); buckets.put("NO_DUE_DATE",0L);
        for(AuditFinding r:repository.findAll()) { if(r.getDueDate()==null) buckets.compute("NO_DUE_DATE",(k,v)->v+1); else { long age=java.time.temporal.ChronoUnit.DAYS.between(r.getDueDate(),LocalDate.now()); String key=age<=7?"0-7":age<=30?"8-30":age<=60?"31-60":"61+"; if(age<0)key="0-7"; buckets.compute(key,(k,v)->v+1); } }
        return buckets;
    }

    /** Evaluates the severity signal #1 for a audit finding record. */
    public boolean isRequiredSeverity1(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the remediation signal #2 for a audit finding record. */
    public boolean isRequiredRemediation2(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the evidence signal #3 for a audit finding record. */
    public boolean isRequiredEvidence3(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #4 for a audit finding record. */
    public boolean isRequiredRepeat4(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #5 for a audit finding record. */
    public boolean isRequiredControl5(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the closure signal #6 for a audit finding record. */
    public boolean isRequiredClosure6(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #7 for a audit finding record. */
    public boolean isRequiredVerification7(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the risk signal #8 for a audit finding record. */
    public boolean isRequiredRisk8(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #9 for a audit finding record. */
    public boolean isRequiredOwner9(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #10 for a audit finding record. */
    public boolean isRequiredDeadline10(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #11 for a audit finding record. */
    public boolean isReadySeverity11(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the remediation signal #12 for a audit finding record. */
    public boolean isReadyRemediation12(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the evidence signal #13 for a audit finding record. */
    public boolean isReadyEvidence13(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #14 for a audit finding record. */
    public boolean isReadyRepeat14(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #15 for a audit finding record. */
    public boolean isReadyControl15(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the closure signal #16 for a audit finding record. */
    public boolean isReadyClosure16(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #17 for a audit finding record. */
    public boolean isReadyVerification17(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the risk signal #18 for a audit finding record. */
    public boolean isReadyRisk18(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #19 for a audit finding record. */
    public boolean isReadyOwner19(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #20 for a audit finding record. */
    public boolean isReadyDeadline20(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #21 for a audit finding record. */
    public boolean needsReviewSeverity21(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the remediation signal #22 for a audit finding record. */
    public boolean needsReviewRemediation22(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the evidence signal #23 for a audit finding record. */
    public boolean needsReviewEvidence23(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #24 for a audit finding record. */
    public boolean needsReviewRepeat24(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #25 for a audit finding record. */
    public boolean needsReviewControl25(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the closure signal #26 for a audit finding record. */
    public boolean needsReviewClosure26(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #27 for a audit finding record. */
    public boolean needsReviewVerification27(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the risk signal #28 for a audit finding record. */
    public boolean needsReviewRisk28(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #29 for a audit finding record. */
    public boolean needsReviewOwner29(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #30 for a audit finding record. */
    public boolean needsReviewDeadline30(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #31 for a audit finding record. */
    public boolean isOverdueSeverity31(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the remediation signal #32 for a audit finding record. */
    public boolean isOverdueRemediation32(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the evidence signal #33 for a audit finding record. */
    public boolean isOverdueEvidence33(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #34 for a audit finding record. */
    public boolean isOverdueRepeat34(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #35 for a audit finding record. */
    public boolean isOverdueControl35(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the closure signal #36 for a audit finding record. */
    public boolean isOverdueClosure36(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #37 for a audit finding record. */
    public boolean isOverdueVerification37(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the risk signal #38 for a audit finding record. */
    public boolean isOverdueRisk38(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #39 for a audit finding record. */
    public boolean isOverdueOwner39(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #40 for a audit finding record. */
    public boolean isOverdueDeadline40(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #41 for a audit finding record. */
    public boolean isDueSoonSeverity41(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the remediation signal #42 for a audit finding record. */
    public boolean isDueSoonRemediation42(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the evidence signal #43 for a audit finding record. */
    public boolean isDueSoonEvidence43(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #44 for a audit finding record. */
    public boolean isDueSoonRepeat44(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #45 for a audit finding record. */
    public boolean isDueSoonControl45(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the closure signal #46 for a audit finding record. */
    public boolean isDueSoonClosure46(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #47 for a audit finding record. */
    public boolean isDueSoonVerification47(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the risk signal #48 for a audit finding record. */
    public boolean isDueSoonRisk48(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #49 for a audit finding record. */
    public boolean isDueSoonOwner49(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #50 for a audit finding record. */
    public boolean isDueSoonDeadline50(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #51 for a audit finding record. */
    public boolean isHighRiskSeverity51(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the remediation signal #52 for a audit finding record. */
    public boolean isHighRiskRemediation52(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the evidence signal #53 for a audit finding record. */
    public boolean isHighRiskEvidence53(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #54 for a audit finding record. */
    public boolean isHighRiskRepeat54(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #55 for a audit finding record. */
    public boolean isHighRiskControl55(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the closure signal #56 for a audit finding record. */
    public boolean isHighRiskClosure56(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #57 for a audit finding record. */
    public boolean isHighRiskVerification57(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the risk signal #58 for a audit finding record. */
    public boolean isHighRiskRisk58(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #59 for a audit finding record. */
    public boolean isHighRiskOwner59(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #60 for a audit finding record. */
    public boolean isHighRiskDeadline60(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #61 for a audit finding record. */
    public boolean isMaterialSeverity61(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the remediation signal #62 for a audit finding record. */
    public boolean isMaterialRemediation62(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the evidence signal #63 for a audit finding record. */
    public boolean isMaterialEvidence63(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #64 for a audit finding record. */
    public boolean isMaterialRepeat64(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #65 for a audit finding record. */
    public boolean isMaterialControl65(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the closure signal #66 for a audit finding record. */
    public boolean isMaterialClosure66(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #67 for a audit finding record. */
    public boolean isMaterialVerification67(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the risk signal #68 for a audit finding record. */
    public boolean isMaterialRisk68(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #69 for a audit finding record. */
    public boolean isMaterialOwner69(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #70 for a audit finding record. */
    public boolean isMaterialDeadline70(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #71 for a audit finding record. */
    public boolean hasEvidenceSeverity71(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the remediation signal #72 for a audit finding record. */
    public boolean hasEvidenceRemediation72(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the evidence signal #73 for a audit finding record. */
    public boolean hasEvidenceEvidence73(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #74 for a audit finding record. */
    public boolean hasEvidenceRepeat74(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #75 for a audit finding record. */
    public boolean hasEvidenceControl75(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the closure signal #76 for a audit finding record. */
    public boolean hasEvidenceClosure76(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #77 for a audit finding record. */
    public boolean hasEvidenceVerification77(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the risk signal #78 for a audit finding record. */
    public boolean hasEvidenceRisk78(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #79 for a audit finding record. */
    public boolean hasEvidenceOwner79(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #80 for a audit finding record. */
    public boolean hasEvidenceDeadline80(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #81 for a audit finding record. */
    public boolean isCompleteSeverity81(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the remediation signal #82 for a audit finding record. */
    public boolean isCompleteRemediation82(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the evidence signal #83 for a audit finding record. */
    public boolean isCompleteEvidence83(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #84 for a audit finding record. */
    public boolean isCompleteRepeat84(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #85 for a audit finding record. */
    public boolean isCompleteControl85(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the closure signal #86 for a audit finding record. */
    public boolean isCompleteClosure86(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #87 for a audit finding record. */
    public boolean isCompleteVerification87(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the risk signal #88 for a audit finding record. */
    public boolean isCompleteRisk88(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #89 for a audit finding record. */
    public boolean isCompleteOwner89(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #90 for a audit finding record. */
    public boolean isCompleteDeadline90(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the severity signal #91 for a audit finding record. */
    public boolean requiresEscalationSeverity91(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the remediation signal #92 for a audit finding record. */
    public boolean requiresEscalationRemediation92(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the evidence signal #93 for a audit finding record. */
    public boolean requiresEscalationEvidence93(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the repeat signal #94 for a audit finding record. */
    public boolean requiresEscalationRepeat94(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the control signal #95 for a audit finding record. */
    public boolean requiresEscalationControl95(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the closure signal #96 for a audit finding record. */
    public boolean requiresEscalationClosure96(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the verification signal #97 for a audit finding record. */
    public boolean requiresEscalationVerification97(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the risk signal #98 for a audit finding record. */
    public boolean requiresEscalationRisk98(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the owner signal #99 for a audit finding record. */
    public boolean requiresEscalationOwner99(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the deadline signal #100 for a audit finding record. */
    public boolean requiresEscalationDeadline100(AuditFinding record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }

}
