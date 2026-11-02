package com.healthcare.hospital.modules.workflows;

import com.healthcare.hospital.modules.legacy.entity.MedicineBatch;
import com.healthcare.hospital.modules.legacy.entity.Prescription;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;

/** Pharmacy batch safety, expiry surveillance and prescription fulfilment analytics. */
@Service
public class PharmacyWorkflowService {
    public List<MedicineBatch> searchBatches(Collection<MedicineBatch> batches,String query) {
        return WorkflowAnalytics.safe(batches).stream().filter(b->WorkflowAnalytics.contains(b.getBatchNumber(),query)||
            (b.getMedicine()!=null&&WorkflowAnalytics.contains(b.getMedicine().getName(),query)))
            .sorted(Comparator.comparing(MedicineBatch::getExpiryDate,Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    }
    public List<MedicineBatch> expired(Collection<MedicineBatch> batches,LocalDate date) {
        if(date==null)return List.of();
        return WorkflowAnalytics.safe(batches).stream().filter(b->b.getExpiryDate()!=null&&b.getExpiryDate().isBefore(date)).toList();
    }
    public List<MedicineBatch> expiringWithin(Collection<MedicineBatch> batches,LocalDate date,int days) {
        if(date==null||days<0)return List.of();
        LocalDate end=date.plusDays(days);
        return WorkflowAnalytics.safe(batches).stream().filter(b->b.getExpiryDate()!=null&&!b.getExpiryDate().isBefore(date)&&!b.getExpiryDate().isAfter(end))
            .filter(b->b.getCurrentQuantity()!=null&&b.getCurrentQuantity()>0).toList();
    }
    public List<MedicineBatch> lowStock(Collection<MedicineBatch> batches,int threshold) {
        if(threshold<0)throw new IllegalArgumentException("Threshold cannot be negative");
        return WorkflowAnalytics.safe(batches).stream().filter(b->b.getCurrentQuantity()!=null&&b.getCurrentQuantity()<=threshold)
            .filter(b->b.getCurrentQuantity()>0).toList();
    }
    public List<String> validateBatch(MedicineBatch batch) {
        if(batch==null)return List.of("Batch is required");
        List<String> errors=new ArrayList<>();
        if(batch.getBatchNumber()==null||batch.getBatchNumber().isBlank())errors.add("Batch number is required");
        if(batch.getMedicine()==null)errors.add("Medicine is required");
        if(batch.getCurrentQuantity()==null||batch.getCurrentQuantity()<0)errors.add("Current quantity cannot be negative");
        if(batch.getInitialQuantity()==null||batch.getInitialQuantity()<0)errors.add("Initial quantity cannot be negative");
        if(batch.getCurrentQuantity()!=null&&batch.getInitialQuantity()!=null&&batch.getCurrentQuantity()>batch.getInitialQuantity())errors.add("Current quantity exceeds received quantity");
        if(batch.getExpiryDate()==null)errors.add("Expiry date is required");
        if(batch.getPurchasePrice()==null||batch.getPurchasePrice().compareTo(BigDecimal.ZERO)<0)errors.add("Purchase price is invalid");
        return List.copyOf(errors);
    }
    public Map<String,Long> batchStatusCounts(Collection<MedicineBatch> batches) {
        return WorkflowAnalytics.count(WorkflowAnalytics.safe(batches),b->String.valueOf(b.getStatus()));
    }
    public long totalUnits(Collection<MedicineBatch> batches) {
        return WorkflowAnalytics.safe(batches).stream().map(MedicineBatch::getCurrentQuantity).filter(Objects::nonNull).mapToLong(Integer::longValue).sum();
    }
    public BigDecimal inventoryValue(Collection<MedicineBatch> batches) {
        return WorkflowAnalytics.safe(batches).stream().filter(b->b.getCurrentQuantity()!=null&&b.getUnitPrice()!=null)
            .map(b->b.getUnitPrice().multiply(BigDecimal.valueOf(b.getCurrentQuantity())))
            .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
    public List<Prescription> partiallyDispensed(Collection<Prescription> prescriptions) {
        return WorkflowAnalytics.safe(prescriptions).stream().filter(p->"PARTIALLY_DISPENSED".equalsIgnoreCase(p.getDispenseStatus())).toList();
    }
    public List<Prescription> pendingDispensing(Collection<Prescription> prescriptions) {
        return WorkflowAnalytics.safe(prescriptions).stream()
        .filter(p -> p.getRemainingQuantity() > 0)
            .filter(p->!"Completed".equalsIgnoreCase(p.getStatus())).toList();
    }
    public List<String> validatePrescription(Prescription prescription) {
        if(prescription==null)return List.of("Prescription is required");
        List<String> errors=new ArrayList<>();
        if(prescription.getPatient()==null)errors.add("Patient is required");
        if(prescription.getDoctor()==null)errors.add("Prescribing doctor is required");
        if(prescription.getMedicationName()==null||prescription.getMedicationName().isBlank())errors.add("Medication is required");
        if(prescription.getDosage()==null||prescription.getDosage().isBlank())errors.add("Dosage is required");
        if(prescription.getFrequency()==null||prescription.getFrequency().isBlank())errors.add("Frequency is required");
        if(prescription.getPrescribedQuantity()==null||prescription.getPrescribedQuantity()<1)errors.add("Prescribed quantity must be positive");
        if(prescription.getDispensedQuantity()!=null&&prescription.getPrescribedQuantity()!=null&&prescription.getDispensedQuantity()>prescription.getPrescribedQuantity())errors.add("Dispensed quantity exceeds prescribed quantity");
        return List.copyOf(errors);
    }
    public WorkflowSnapshot snapshot(Collection<MedicineBatch> batches,LocalDate today) {
        List<MedicineBatch> list=WorkflowAnalytics.safe(batches);
        long attention=expired(list,today).size()+expiringWithin(list,today,30).size()+lowStock(list,10).size();
        return WorkflowAnalytics.snapshot("PHARMACY",list.stream().map(b->String.valueOf(b.getStatus())).toList(),list.size(),
            list.stream().filter(b->b.getCurrentQuantity()!=null&&b.getCurrentQuantity()>0).count(),attention,List.of());
    }
    public Map<String,Object> inventoryBoard(Collection<MedicineBatch> batches,LocalDate today) {
        List<MedicineBatch> list=WorkflowAnalytics.safe(batches);
        return Map.of("batchCount",list.size(),"units",totalUnits(list),"inventoryValue",inventoryValue(list),
            "expired",expired(list,today).size(),"expiring30Days",expiringWithin(list,today,30).size(),
            "lowStock",lowStock(list,10).size(),"statuses",batchStatusCounts(list));
    }
}
