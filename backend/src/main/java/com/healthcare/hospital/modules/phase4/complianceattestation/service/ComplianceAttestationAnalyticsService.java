package com.healthcare.hospital.modules.phase4.complianceattestation.service;

import com.healthcare.hospital.modules.phase4.complianceattestation.entity.ComplianceAttestation;
import com.healthcare.hospital.modules.phase4.complianceattestation.repository.ComplianceAttestationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Explainable domain signal catalog and analytics for compliance attestation records. */
@Service
@Transactional(readOnly=true)
public class ComplianceAttestationAnalyticsService {
    private final ComplianceAttestationRepository repository;
    public ComplianceAttestationAnalyticsService(ComplianceAttestationRepository repository) { this.repository = repository; }

    public Map<String,Object> metrics() {
        List<ComplianceAttestation> records = repository.findAll();
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
        for(ComplianceAttestation record:repository.findAll()) {
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

    public List<ComplianceAttestation> overdueItems(LocalDate asOf) {
        LocalDate date=asOf==null?LocalDate.now():asOf;
        return repository.findAll().stream().filter(r->r.getDueDate()!=null&&r.getDueDate().isBefore(date))
          .sorted(Comparator.comparing(ComplianceAttestation::getDueDate)).collect(Collectors.toList());
    }

    public List<ComplianceAttestation> missingEvidence() {
        return repository.findAll().stream().filter(r->r.getEvidenceReference()==null||r.getEvidenceReference().isBlank()).collect(Collectors.toList());
    }

    public List<ComplianceAttestation> unassignedItems() {
        return repository.findAll().stream().filter(r->r.getOwner()==null||r.getOwner().isBlank()).collect(Collectors.toList());
    }

    public Map<String,Long> agingBuckets() {
        Map<String,Long> buckets=new LinkedHashMap<>(); buckets.put("0-7",0L); buckets.put("8-30",0L); buckets.put("31-60",0L); buckets.put("61+",0L); buckets.put("NO_DUE_DATE",0L);
        for(ComplianceAttestation r:repository.findAll()) { if(r.getDueDate()==null) buckets.compute("NO_DUE_DATE",(k,v)->v+1); else { long age=java.time.temporal.ChronoUnit.DAYS.between(r.getDueDate(),LocalDate.now()); String key=age<=7?"0-7":age<=30?"8-30":age<=60?"31-60":"61+"; if(age<0)key="0-7"; buckets.compute(key,(k,v)->v+1); } }
        return buckets;
    }

    /** Evaluates the policy signal #1 for a compliance attestation record. */
    public boolean isRequiredPolicy1(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the evidence signal #2 for a compliance attestation record. */
    public boolean isRequiredEvidence2(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the submission signal #3 for a compliance attestation record. */
    public boolean isRequiredSubmission3(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #4 for a compliance attestation record. */
    public boolean isRequiredReview4(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #5 for a compliance attestation record. */
    public boolean isRequiredAcceptance5(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the renewal signal #6 for a compliance attestation record. */
    public boolean isRequiredRenewal6(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #7 for a compliance attestation record. */
    public boolean isRequiredControl7(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the exception signal #8 for a compliance attestation record. */
    public boolean isRequiredException8(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #9 for a compliance attestation record. */
    public boolean isRequiredRisk9(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #10 for a compliance attestation record. */
    public boolean isRequiredFollowup10(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #11 for a compliance attestation record. */
    public boolean isReadyPolicy11(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the evidence signal #12 for a compliance attestation record. */
    public boolean isReadyEvidence12(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the submission signal #13 for a compliance attestation record. */
    public boolean isReadySubmission13(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #14 for a compliance attestation record. */
    public boolean isReadyReview14(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #15 for a compliance attestation record. */
    public boolean isReadyAcceptance15(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the renewal signal #16 for a compliance attestation record. */
    public boolean isReadyRenewal16(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #17 for a compliance attestation record. */
    public boolean isReadyControl17(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the exception signal #18 for a compliance attestation record. */
    public boolean isReadyException18(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #19 for a compliance attestation record. */
    public boolean isReadyRisk19(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #20 for a compliance attestation record. */
    public boolean isReadyFollowup20(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #21 for a compliance attestation record. */
    public boolean needsReviewPolicy21(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the evidence signal #22 for a compliance attestation record. */
    public boolean needsReviewEvidence22(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the submission signal #23 for a compliance attestation record. */
    public boolean needsReviewSubmission23(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #24 for a compliance attestation record. */
    public boolean needsReviewReview24(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #25 for a compliance attestation record. */
    public boolean needsReviewAcceptance25(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the renewal signal #26 for a compliance attestation record. */
    public boolean needsReviewRenewal26(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #27 for a compliance attestation record. */
    public boolean needsReviewControl27(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the exception signal #28 for a compliance attestation record. */
    public boolean needsReviewException28(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #29 for a compliance attestation record. */
    public boolean needsReviewRisk29(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #30 for a compliance attestation record. */
    public boolean needsReviewFollowup30(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #31 for a compliance attestation record. */
    public boolean isOverduePolicy31(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the evidence signal #32 for a compliance attestation record. */
    public boolean isOverdueEvidence32(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the submission signal #33 for a compliance attestation record. */
    public boolean isOverdueSubmission33(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #34 for a compliance attestation record. */
    public boolean isOverdueReview34(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #35 for a compliance attestation record. */
    public boolean isOverdueAcceptance35(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the renewal signal #36 for a compliance attestation record. */
    public boolean isOverdueRenewal36(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #37 for a compliance attestation record. */
    public boolean isOverdueControl37(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the exception signal #38 for a compliance attestation record. */
    public boolean isOverdueException38(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #39 for a compliance attestation record. */
    public boolean isOverdueRisk39(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #40 for a compliance attestation record. */
    public boolean isOverdueFollowup40(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #41 for a compliance attestation record. */
    public boolean isDueSoonPolicy41(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the evidence signal #42 for a compliance attestation record. */
    public boolean isDueSoonEvidence42(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the submission signal #43 for a compliance attestation record. */
    public boolean isDueSoonSubmission43(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #44 for a compliance attestation record. */
    public boolean isDueSoonReview44(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #45 for a compliance attestation record. */
    public boolean isDueSoonAcceptance45(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the renewal signal #46 for a compliance attestation record. */
    public boolean isDueSoonRenewal46(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #47 for a compliance attestation record. */
    public boolean isDueSoonControl47(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the exception signal #48 for a compliance attestation record. */
    public boolean isDueSoonException48(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #49 for a compliance attestation record. */
    public boolean isDueSoonRisk49(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #50 for a compliance attestation record. */
    public boolean isDueSoonFollowup50(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #51 for a compliance attestation record. */
    public boolean isHighRiskPolicy51(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the evidence signal #52 for a compliance attestation record. */
    public boolean isHighRiskEvidence52(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the submission signal #53 for a compliance attestation record. */
    public boolean isHighRiskSubmission53(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #54 for a compliance attestation record. */
    public boolean isHighRiskReview54(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #55 for a compliance attestation record. */
    public boolean isHighRiskAcceptance55(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the renewal signal #56 for a compliance attestation record. */
    public boolean isHighRiskRenewal56(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #57 for a compliance attestation record. */
    public boolean isHighRiskControl57(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the exception signal #58 for a compliance attestation record. */
    public boolean isHighRiskException58(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #59 for a compliance attestation record. */
    public boolean isHighRiskRisk59(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #60 for a compliance attestation record. */
    public boolean isHighRiskFollowup60(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #61 for a compliance attestation record. */
    public boolean isMaterialPolicy61(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the evidence signal #62 for a compliance attestation record. */
    public boolean isMaterialEvidence62(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the submission signal #63 for a compliance attestation record. */
    public boolean isMaterialSubmission63(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #64 for a compliance attestation record. */
    public boolean isMaterialReview64(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #65 for a compliance attestation record. */
    public boolean isMaterialAcceptance65(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the renewal signal #66 for a compliance attestation record. */
    public boolean isMaterialRenewal66(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #67 for a compliance attestation record. */
    public boolean isMaterialControl67(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the exception signal #68 for a compliance attestation record. */
    public boolean isMaterialException68(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #69 for a compliance attestation record. */
    public boolean isMaterialRisk69(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #70 for a compliance attestation record. */
    public boolean isMaterialFollowup70(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #71 for a compliance attestation record. */
    public boolean hasEvidencePolicy71(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the evidence signal #72 for a compliance attestation record. */
    public boolean hasEvidenceEvidence72(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the submission signal #73 for a compliance attestation record. */
    public boolean hasEvidenceSubmission73(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #74 for a compliance attestation record. */
    public boolean hasEvidenceReview74(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #75 for a compliance attestation record. */
    public boolean hasEvidenceAcceptance75(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the renewal signal #76 for a compliance attestation record. */
    public boolean hasEvidenceRenewal76(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #77 for a compliance attestation record. */
    public boolean hasEvidenceControl77(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the exception signal #78 for a compliance attestation record. */
    public boolean hasEvidenceException78(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #79 for a compliance attestation record. */
    public boolean hasEvidenceRisk79(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #80 for a compliance attestation record. */
    public boolean hasEvidenceFollowup80(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #81 for a compliance attestation record. */
    public boolean isCompletePolicy81(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the evidence signal #82 for a compliance attestation record. */
    public boolean isCompleteEvidence82(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the submission signal #83 for a compliance attestation record. */
    public boolean isCompleteSubmission83(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #84 for a compliance attestation record. */
    public boolean isCompleteReview84(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #85 for a compliance attestation record. */
    public boolean isCompleteAcceptance85(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the renewal signal #86 for a compliance attestation record. */
    public boolean isCompleteRenewal86(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #87 for a compliance attestation record. */
    public boolean isCompleteControl87(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the exception signal #88 for a compliance attestation record. */
    public boolean isCompleteException88(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #89 for a compliance attestation record. */
    public boolean isCompleteRisk89(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #90 for a compliance attestation record. */
    public boolean isCompleteFollowup90(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the policy signal #91 for a compliance attestation record. */
    public boolean requiresEscalationPolicy91(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the evidence signal #92 for a compliance attestation record. */
    public boolean requiresEscalationEvidence92(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the submission signal #93 for a compliance attestation record. */
    public boolean requiresEscalationSubmission93(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the review signal #94 for a compliance attestation record. */
    public boolean requiresEscalationReview94(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the acceptance signal #95 for a compliance attestation record. */
    public boolean requiresEscalationAcceptance95(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the renewal signal #96 for a compliance attestation record. */
    public boolean requiresEscalationRenewal96(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the control signal #97 for a compliance attestation record. */
    public boolean requiresEscalationControl97(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the exception signal #98 for a compliance attestation record. */
    public boolean requiresEscalationException98(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the risk signal #99 for a compliance attestation record. */
    public boolean requiresEscalationRisk99(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the followup signal #100 for a compliance attestation record. */
    public boolean requiresEscalationFollowup100(ComplianceAttestation record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }

}
