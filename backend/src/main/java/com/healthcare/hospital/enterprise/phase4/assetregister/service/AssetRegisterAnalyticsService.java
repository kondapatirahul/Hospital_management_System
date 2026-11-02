package com.healthcare.hospital.enterprise.phase4.assetregister.service;

import com.healthcare.hospital.enterprise.phase4.assetregister.entity.AssetRegister;
import com.healthcare.hospital.enterprise.phase4.assetregister.repository.AssetRegisterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Explainable domain signal catalog and analytics for asset register records. */
@Service
@Transactional(readOnly=true)
public class AssetRegisterAnalyticsService {
    private final AssetRegisterRepository repository;
    public AssetRegisterAnalyticsService(AssetRegisterRepository repository) { this.repository = repository; }

    public Map<String,Object> metrics() {
        List<AssetRegister> records = repository.findAll();
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
        for(AssetRegister record:repository.findAll()) {
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

    public List<AssetRegister> overdueItems(LocalDate asOf) {
        LocalDate date=asOf==null?LocalDate.now():asOf;
        return repository.findAll().stream().filter(r->r.getDueDate()!=null&&r.getDueDate().isBefore(date))
          .sorted(Comparator.comparing(AssetRegister::getDueDate)).collect(Collectors.toList());
    }

    public List<AssetRegister> missingEvidence() {
        return repository.findAll().stream().filter(r->r.getEvidenceReference()==null||r.getEvidenceReference().isBlank()).collect(Collectors.toList());
    }

    public List<AssetRegister> unassignedItems() {
        return repository.findAll().stream().filter(r->r.getOwner()==null||r.getOwner().isBlank()).collect(Collectors.toList());
    }

    public Map<String,Long> agingBuckets() {
        Map<String,Long> buckets=new LinkedHashMap<>(); buckets.put("0-7",0L); buckets.put("8-30",0L); buckets.put("31-60",0L); buckets.put("61+",0L); buckets.put("NO_DUE_DATE",0L);
        for(AssetRegister r:repository.findAll()) { if(r.getDueDate()==null) buckets.compute("NO_DUE_DATE",(k,v)->v+1); else { long age=java.time.temporal.ChronoUnit.DAYS.between(r.getDueDate(),LocalDate.now()); String key=age<=7?"0-7":age<=30?"8-30":age<=60?"31-60":"61+"; if(age<0)key="0-7"; buckets.compute(key,(k,v)->v+1); } }
        return buckets;
    }

    /** Evaluates the warranty signal #1 for a asset register record. */
    public boolean isRequiredWarranty1(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the depreciation signal #2 for a asset register record. */
    public boolean isRequiredDepreciation2(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the maintenance signal #3 for a asset register record. */
    public boolean isRequiredMaintenance3(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #4 for a asset register record. */
    public boolean isRequiredCustody4(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #5 for a asset register record. */
    public boolean isRequiredReplacement5(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the location signal #6 for a asset register record. */
    public boolean isRequiredLocation6(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #7 for a asset register record. */
    public boolean isRequiredAssignment7(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the lifecycle signal #8 for a asset register record. */
    public boolean isRequiredLifecycle8(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #9 for a asset register record. */
    public boolean isRequiredInventory9(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #10 for a asset register record. */
    public boolean isRequiredDisposal10(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #11 for a asset register record. */
    public boolean isReadyWarranty11(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the depreciation signal #12 for a asset register record. */
    public boolean isReadyDepreciation12(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the maintenance signal #13 for a asset register record. */
    public boolean isReadyMaintenance13(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #14 for a asset register record. */
    public boolean isReadyCustody14(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #15 for a asset register record. */
    public boolean isReadyReplacement15(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the location signal #16 for a asset register record. */
    public boolean isReadyLocation16(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #17 for a asset register record. */
    public boolean isReadyAssignment17(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the lifecycle signal #18 for a asset register record. */
    public boolean isReadyLifecycle18(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #19 for a asset register record. */
    public boolean isReadyInventory19(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #20 for a asset register record. */
    public boolean isReadyDisposal20(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #21 for a asset register record. */
    public boolean needsReviewWarranty21(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the depreciation signal #22 for a asset register record. */
    public boolean needsReviewDepreciation22(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the maintenance signal #23 for a asset register record. */
    public boolean needsReviewMaintenance23(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #24 for a asset register record. */
    public boolean needsReviewCustody24(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #25 for a asset register record. */
    public boolean needsReviewReplacement25(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the location signal #26 for a asset register record. */
    public boolean needsReviewLocation26(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #27 for a asset register record. */
    public boolean needsReviewAssignment27(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the lifecycle signal #28 for a asset register record. */
    public boolean needsReviewLifecycle28(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #29 for a asset register record. */
    public boolean needsReviewInventory29(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #30 for a asset register record. */
    public boolean needsReviewDisposal30(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #31 for a asset register record. */
    public boolean isOverdueWarranty31(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the depreciation signal #32 for a asset register record. */
    public boolean isOverdueDepreciation32(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the maintenance signal #33 for a asset register record. */
    public boolean isOverdueMaintenance33(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #34 for a asset register record. */
    public boolean isOverdueCustody34(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #35 for a asset register record. */
    public boolean isOverdueReplacement35(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the location signal #36 for a asset register record. */
    public boolean isOverdueLocation36(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #37 for a asset register record. */
    public boolean isOverdueAssignment37(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the lifecycle signal #38 for a asset register record. */
    public boolean isOverdueLifecycle38(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #39 for a asset register record. */
    public boolean isOverdueInventory39(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #40 for a asset register record. */
    public boolean isOverdueDisposal40(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #41 for a asset register record. */
    public boolean isDueSoonWarranty41(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the depreciation signal #42 for a asset register record. */
    public boolean isDueSoonDepreciation42(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the maintenance signal #43 for a asset register record. */
    public boolean isDueSoonMaintenance43(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #44 for a asset register record. */
    public boolean isDueSoonCustody44(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #45 for a asset register record. */
    public boolean isDueSoonReplacement45(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the location signal #46 for a asset register record. */
    public boolean isDueSoonLocation46(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #47 for a asset register record. */
    public boolean isDueSoonAssignment47(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the lifecycle signal #48 for a asset register record. */
    public boolean isDueSoonLifecycle48(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #49 for a asset register record. */
    public boolean isDueSoonInventory49(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #50 for a asset register record. */
    public boolean isDueSoonDisposal50(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #51 for a asset register record. */
    public boolean isHighRiskWarranty51(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the depreciation signal #52 for a asset register record. */
    public boolean isHighRiskDepreciation52(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the maintenance signal #53 for a asset register record. */
    public boolean isHighRiskMaintenance53(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #54 for a asset register record. */
    public boolean isHighRiskCustody54(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #55 for a asset register record. */
    public boolean isHighRiskReplacement55(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the location signal #56 for a asset register record. */
    public boolean isHighRiskLocation56(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #57 for a asset register record. */
    public boolean isHighRiskAssignment57(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the lifecycle signal #58 for a asset register record. */
    public boolean isHighRiskLifecycle58(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #59 for a asset register record. */
    public boolean isHighRiskInventory59(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #60 for a asset register record. */
    public boolean isHighRiskDisposal60(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #61 for a asset register record. */
    public boolean isMaterialWarranty61(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the depreciation signal #62 for a asset register record. */
    public boolean isMaterialDepreciation62(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the maintenance signal #63 for a asset register record. */
    public boolean isMaterialMaintenance63(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #64 for a asset register record. */
    public boolean isMaterialCustody64(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #65 for a asset register record. */
    public boolean isMaterialReplacement65(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the location signal #66 for a asset register record. */
    public boolean isMaterialLocation66(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #67 for a asset register record. */
    public boolean isMaterialAssignment67(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the lifecycle signal #68 for a asset register record. */
    public boolean isMaterialLifecycle68(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #69 for a asset register record. */
    public boolean isMaterialInventory69(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #70 for a asset register record. */
    public boolean isMaterialDisposal70(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #71 for a asset register record. */
    public boolean hasEvidenceWarranty71(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the depreciation signal #72 for a asset register record. */
    public boolean hasEvidenceDepreciation72(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the maintenance signal #73 for a asset register record. */
    public boolean hasEvidenceMaintenance73(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #74 for a asset register record. */
    public boolean hasEvidenceCustody74(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #75 for a asset register record. */
    public boolean hasEvidenceReplacement75(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the location signal #76 for a asset register record. */
    public boolean hasEvidenceLocation76(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #77 for a asset register record. */
    public boolean hasEvidenceAssignment77(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the lifecycle signal #78 for a asset register record. */
    public boolean hasEvidenceLifecycle78(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #79 for a asset register record. */
    public boolean hasEvidenceInventory79(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #80 for a asset register record. */
    public boolean hasEvidenceDisposal80(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #81 for a asset register record. */
    public boolean isCompleteWarranty81(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the depreciation signal #82 for a asset register record. */
    public boolean isCompleteDepreciation82(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the maintenance signal #83 for a asset register record. */
    public boolean isCompleteMaintenance83(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #84 for a asset register record. */
    public boolean isCompleteCustody84(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #85 for a asset register record. */
    public boolean isCompleteReplacement85(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the location signal #86 for a asset register record. */
    public boolean isCompleteLocation86(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #87 for a asset register record. */
    public boolean isCompleteAssignment87(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the lifecycle signal #88 for a asset register record. */
    public boolean isCompleteLifecycle88(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #89 for a asset register record. */
    public boolean isCompleteInventory89(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #90 for a asset register record. */
    public boolean isCompleteDisposal90(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the warranty signal #91 for a asset register record. */
    public boolean requiresEscalationWarranty91(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the depreciation signal #92 for a asset register record. */
    public boolean requiresEscalationDepreciation92(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the maintenance signal #93 for a asset register record. */
    public boolean requiresEscalationMaintenance93(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the custody signal #94 for a asset register record. */
    public boolean requiresEscalationCustody94(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the replacement signal #95 for a asset register record. */
    public boolean requiresEscalationReplacement95(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the location signal #96 for a asset register record. */
    public boolean requiresEscalationLocation96(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the assignment signal #97 for a asset register record. */
    public boolean requiresEscalationAssignment97(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the lifecycle signal #98 for a asset register record. */
    public boolean requiresEscalationLifecycle98(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the inventory signal #99 for a asset register record. */
    public boolean requiresEscalationInventory99(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the disposal signal #100 for a asset register record. */
    public boolean requiresEscalationDisposal100(AssetRegister record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }

}
