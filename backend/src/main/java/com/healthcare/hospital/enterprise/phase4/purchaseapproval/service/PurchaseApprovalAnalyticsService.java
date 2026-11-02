package com.healthcare.hospital.enterprise.phase4.purchaseapproval.service;

import com.healthcare.hospital.enterprise.phase4.purchaseapproval.entity.PurchaseApproval;
import com.healthcare.hospital.enterprise.phase4.purchaseapproval.repository.PurchaseApprovalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Explainable domain signal catalog and analytics for purchase approval records. */
@Service
@Transactional(readOnly=true)
public class PurchaseApprovalAnalyticsService {
    private final PurchaseApprovalRepository repository;
    public PurchaseApprovalAnalyticsService(PurchaseApprovalRepository repository) { this.repository = repository; }

    public Map<String,Object> metrics() {
        List<PurchaseApproval> records = repository.findAll();
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
        for(PurchaseApproval record:repository.findAll()) {
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

    public List<PurchaseApproval> overdueItems(LocalDate asOf) {
        LocalDate date=asOf==null?LocalDate.now():asOf;
        return repository.findAll().stream().filter(r->r.getDueDate()!=null&&r.getDueDate().isBefore(date))
          .sorted(Comparator.comparing(PurchaseApproval::getDueDate)).collect(Collectors.toList());
    }

    public List<PurchaseApproval> missingEvidence() {
        return repository.findAll().stream().filter(r->r.getEvidenceReference()==null||r.getEvidenceReference().isBlank()).collect(Collectors.toList());
    }

    public List<PurchaseApproval> unassignedItems() {
        return repository.findAll().stream().filter(r->r.getOwner()==null||r.getOwner().isBlank()).collect(Collectors.toList());
    }

    public Map<String,Long> agingBuckets() {
        Map<String,Long> buckets=new LinkedHashMap<>(); buckets.put("0-7",0L); buckets.put("8-30",0L); buckets.put("31-60",0L); buckets.put("61+",0L); buckets.put("NO_DUE_DATE",0L);
        for(PurchaseApproval r:repository.findAll()) { if(r.getDueDate()==null) buckets.compute("NO_DUE_DATE",(k,v)->v+1); else { long age=java.time.temporal.ChronoUnit.DAYS.between(r.getDueDate(),LocalDate.now()); String key=age<=7?"0-7":age<=30?"8-30":age<=60?"31-60":"61+"; if(age<0)key="0-7"; buckets.compute(key,(k,v)->v+1); } }
        return buckets;
    }

    /** Evaluates the supplier signal #1 for a purchase approval record. */
    public boolean isRequiredSupplier1(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the quote signal #2 for a purchase approval record. */
    public boolean isRequiredQuote2(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the delivery signal #3 for a purchase approval record. */
    public boolean isRequiredDelivery3(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #4 for a purchase approval record. */
    public boolean isRequiredReceiving4(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #5 for a purchase approval record. */
    public boolean isRequiredCapital5(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the purchase signal #6 for a purchase approval record. */
    public boolean isRequiredPurchase6(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #7 for a purchase approval record. */
    public boolean isRequiredInvoice7(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the leadtime signal #8 for a purchase approval record. */
    public boolean isRequiredLeadtime8(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #9 for a purchase approval record. */
    public boolean isRequiredQuality9(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #10 for a purchase approval record. */
    public boolean isRequiredContract10(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #11 for a purchase approval record. */
    public boolean isReadySupplier11(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the quote signal #12 for a purchase approval record. */
    public boolean isReadyQuote12(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the delivery signal #13 for a purchase approval record. */
    public boolean isReadyDelivery13(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #14 for a purchase approval record. */
    public boolean isReadyReceiving14(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #15 for a purchase approval record. */
    public boolean isReadyCapital15(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the purchase signal #16 for a purchase approval record. */
    public boolean isReadyPurchase16(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #17 for a purchase approval record. */
    public boolean isReadyInvoice17(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the leadtime signal #18 for a purchase approval record. */
    public boolean isReadyLeadtime18(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #19 for a purchase approval record. */
    public boolean isReadyQuality19(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #20 for a purchase approval record. */
    public boolean isReadyContract20(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #21 for a purchase approval record. */
    public boolean needsReviewSupplier21(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the quote signal #22 for a purchase approval record. */
    public boolean needsReviewQuote22(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the delivery signal #23 for a purchase approval record. */
    public boolean needsReviewDelivery23(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #24 for a purchase approval record. */
    public boolean needsReviewReceiving24(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #25 for a purchase approval record. */
    public boolean needsReviewCapital25(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the purchase signal #26 for a purchase approval record. */
    public boolean needsReviewPurchase26(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #27 for a purchase approval record. */
    public boolean needsReviewInvoice27(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the leadtime signal #28 for a purchase approval record. */
    public boolean needsReviewLeadtime28(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #29 for a purchase approval record. */
    public boolean needsReviewQuality29(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #30 for a purchase approval record. */
    public boolean needsReviewContract30(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #31 for a purchase approval record. */
    public boolean isOverdueSupplier31(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the quote signal #32 for a purchase approval record. */
    public boolean isOverdueQuote32(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the delivery signal #33 for a purchase approval record. */
    public boolean isOverdueDelivery33(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #34 for a purchase approval record. */
    public boolean isOverdueReceiving34(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #35 for a purchase approval record. */
    public boolean isOverdueCapital35(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the purchase signal #36 for a purchase approval record. */
    public boolean isOverduePurchase36(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #37 for a purchase approval record. */
    public boolean isOverdueInvoice37(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the leadtime signal #38 for a purchase approval record. */
    public boolean isOverdueLeadtime38(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #39 for a purchase approval record. */
    public boolean isOverdueQuality39(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #40 for a purchase approval record. */
    public boolean isOverdueContract40(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #41 for a purchase approval record. */
    public boolean isDueSoonSupplier41(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the quote signal #42 for a purchase approval record. */
    public boolean isDueSoonQuote42(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the delivery signal #43 for a purchase approval record. */
    public boolean isDueSoonDelivery43(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #44 for a purchase approval record. */
    public boolean isDueSoonReceiving44(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #45 for a purchase approval record. */
    public boolean isDueSoonCapital45(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the purchase signal #46 for a purchase approval record. */
    public boolean isDueSoonPurchase46(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #47 for a purchase approval record. */
    public boolean isDueSoonInvoice47(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the leadtime signal #48 for a purchase approval record. */
    public boolean isDueSoonLeadtime48(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #49 for a purchase approval record. */
    public boolean isDueSoonQuality49(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #50 for a purchase approval record. */
    public boolean isDueSoonContract50(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #51 for a purchase approval record. */
    public boolean isHighRiskSupplier51(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the quote signal #52 for a purchase approval record. */
    public boolean isHighRiskQuote52(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the delivery signal #53 for a purchase approval record. */
    public boolean isHighRiskDelivery53(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #54 for a purchase approval record. */
    public boolean isHighRiskReceiving54(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #55 for a purchase approval record. */
    public boolean isHighRiskCapital55(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the purchase signal #56 for a purchase approval record. */
    public boolean isHighRiskPurchase56(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #57 for a purchase approval record. */
    public boolean isHighRiskInvoice57(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the leadtime signal #58 for a purchase approval record. */
    public boolean isHighRiskLeadtime58(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #59 for a purchase approval record. */
    public boolean isHighRiskQuality59(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #60 for a purchase approval record. */
    public boolean isHighRiskContract60(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #61 for a purchase approval record. */
    public boolean isMaterialSupplier61(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the quote signal #62 for a purchase approval record. */
    public boolean isMaterialQuote62(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the delivery signal #63 for a purchase approval record. */
    public boolean isMaterialDelivery63(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #64 for a purchase approval record. */
    public boolean isMaterialReceiving64(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #65 for a purchase approval record. */
    public boolean isMaterialCapital65(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the purchase signal #66 for a purchase approval record. */
    public boolean isMaterialPurchase66(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #67 for a purchase approval record. */
    public boolean isMaterialInvoice67(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the leadtime signal #68 for a purchase approval record. */
    public boolean isMaterialLeadtime68(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #69 for a purchase approval record. */
    public boolean isMaterialQuality69(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #70 for a purchase approval record. */
    public boolean isMaterialContract70(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #71 for a purchase approval record. */
    public boolean hasEvidenceSupplier71(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("50000")) >= 0;
    }
    /** Evaluates the quote signal #72 for a purchase approval record. */
    public boolean hasEvidenceQuote72(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(60));
    }
    /** Evaluates the delivery signal #73 for a purchase approval record. */
    public boolean hasEvidenceDelivery73(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #74 for a purchase approval record. */
    public boolean hasEvidenceReceiving74(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #75 for a purchase approval record. */
    public boolean hasEvidenceCapital75(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("APPROVED");
    }
    /** Evaluates the purchase signal #76 for a purchase approval record. */
    public boolean hasEvidencePurchase76(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #77 for a purchase approval record. */
    public boolean hasEvidenceInvoice77(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));
    }
    /** Evaluates the leadtime signal #78 for a purchase approval record. */
    public boolean hasEvidenceLeadtime78(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #79 for a purchase approval record. */
    public boolean hasEvidenceQuality79(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #80 for a purchase approval record. */
    public boolean hasEvidenceContract80(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #81 for a purchase approval record. */
    public boolean isCompleteSupplier81(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("10000")) >= 0;
    }
    /** Evaluates the quote signal #82 for a purchase approval record. */
    public boolean isCompleteQuote82(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(14));
    }
    /** Evaluates the delivery signal #83 for a purchase approval record. */
    public boolean isCompleteDelivery83(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #84 for a purchase approval record. */
    public boolean isCompleteReceiving84(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #85 for a purchase approval record. */
    public boolean isCompleteCapital85(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("OPEN");
    }
    /** Evaluates the purchase signal #86 for a purchase approval record. */
    public boolean isCompletePurchase86(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #87 for a purchase approval record. */
    public boolean isCompleteInvoice87(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7));
    }
    /** Evaluates the leadtime signal #88 for a purchase approval record. */
    public boolean isCompleteLeadtime88(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #89 for a purchase approval record. */
    public boolean isCompleteQuality89(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #90 for a purchase approval record. */
    public boolean isCompleteContract90(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }
    /** Evaluates the supplier signal #91 for a purchase approval record. */
    public boolean requiresEscalationSupplier91(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() != null && record.getAmount().compareTo(new BigDecimal("2500")) >= 0;
    }
    /** Evaluates the quote signal #92 for a purchase approval record. */
    public boolean requiresEscalationQuote92(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && !record.getDueDate().isAfter(LocalDate.now().plusDays(3));
    }
    /** Evaluates the delivery signal #93 for a purchase approval record. */
    public boolean requiresEscalationDelivery93(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getOwner() == null || record.getOwner().isBlank();
    }
    /** Evaluates the receiving signal #94 for a purchase approval record. */
    public boolean requiresEscalationReceiving94(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getEvidenceReference() != null && !record.getEvidenceReference().isBlank();
    }
    /** Evaluates the capital signal #95 for a purchase approval record. */
    public boolean requiresEscalationCapital95(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getStatus() != null && record.getStatus().equalsIgnoreCase("EXPIRED");
    }
    /** Evaluates the purchase signal #96 for a purchase approval record. */
    public boolean requiresEscalationPurchase96(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDepartment() != null && record.getDepartment().toLowerCase(Locale.ROOT).contains("finance");
    }
    /** Evaluates the invoice signal #97 for a purchase approval record. */
    public boolean requiresEscalationInvoice97(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getCreatedAt() != null && record.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
    }
    /** Evaluates the leadtime signal #98 for a purchase approval record. */
    public boolean requiresEscalationLeadtime98(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getReferenceCode() == null || record.getReferenceCode().isBlank();
    }
    /** Evaluates the quality signal #99 for a purchase approval record. */
    public boolean requiresEscalationQuality99(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    /** Evaluates the contract signal #100 for a purchase approval record. */
    public boolean requiresEscalationContract100(PurchaseApproval record) {
        if (record == null) {
            return false;
        }
        return record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now());
    }

}
