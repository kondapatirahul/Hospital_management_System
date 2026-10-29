package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.ShiftHandoverForm;
import com.healthcare.hospital.entity.ShiftHandover;
import com.healthcare.hospital.repository.ShiftHandoverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Enforces handover submission and receiver-acknowledgement rules. */
@Service
@Transactional
public class ShiftHandoverService {
    private static final Set<String> STATUSES=Set.of("DRAFT","SUBMITTED","ACKNOWLEDGED","RETURNED");
    private final ShiftHandoverRepository repository;
    public ShiftHandoverService(ShiftHandoverRepository repository){this.repository=repository;}

    @Transactional(readOnly=true) public List<ShiftHandover> all(){return repository.findAllByOrderByShiftDateDescCreatedAtDesc();}
    @Transactional(readOnly=true) public Optional<ShiftHandover> find(long id){return repository.findById(id);}
    @Transactional(readOnly=true) public List<ShiftHandover> search(String status,String department,String term){
        return repository.search(cleanStatus(status),blank(department),blank(term));
    }
    public ShiftHandover create(ShiftHandoverForm form){
        Objects.requireNonNull(form,"Handover form is required");
        ShiftHandover h=new ShiftHandover();
        h.setReferenceCode(nextReference()); h.setDepartment(required(form.getDepartment(),"Department",100));
        h.setShiftName(required(form.getShiftName(),"Shift",30)); h.setShiftDate(form.getShiftDate()==null?LocalDateTime.now():form.getShiftDate());
        h.setOutgoingStaff(required(form.getOutgoingStaff(),"Outgoing staff",100));
        h.setIncomingStaff(required(form.getIncomingStaff(),"Incoming staff",100));
        h.setSummary(required(form.getSummary(),"Handover summary",3000));
        h.setOutstandingTasks(limit(form.getOutstandingTasks(),3000)); h.setPatientWatchlist(limit(form.getPatientWatchlist(),3000));
        h.setEquipmentOrSupplyIssues(limit(form.getEquipmentOrSupplyIssues(),2000)); h.setEscalationNotes(limit(form.getEscalationNotes(),2000));
        h.setUrgent(form.isUrgent()); h.setMedicationCountVerified(form.isMedicationCountVerified());
        h.setControlledItemsVerified(form.isControlledItemsVerified()); h.setPatientBoardReviewed(form.isPatientBoardReviewed());
        h.setSafetyChecksComplete(form.isSafetyChecksComplete()); h.setStatus("DRAFT"); h.setCreatedAt(LocalDateTime.now());
        return repository.save(h);
    }
    public ShiftHandover submit(long id){
        ShiftHandover h=require(id);
        if(!"DRAFT".equals(h.getStatus()) && !"RETURNED".equals(h.getStatus())) throw new IllegalStateException("Only draft or returned handovers can be submitted.");
        if(h.getSummary()==null || h.getSummary().isBlank()) throw new IllegalStateException("A summary is required before submission.");
        h.setStatus("SUBMITTED"); h.setSubmittedAt(LocalDateTime.now()); return repository.save(h);
    }
    public ShiftHandover acknowledge(long id,String actor,String comment){
        ShiftHandover h=require(id);
        if(!"SUBMITTED".equals(h.getStatus())) throw new IllegalStateException("Only submitted handovers can be acknowledged.");
        if(actor==null||actor.isBlank()) throw new IllegalArgumentException("Receiver identity is required.");
        if(actor.equalsIgnoreCase(h.getOutgoingStaff())) throw new IllegalStateException("The outgoing staff member cannot acknowledge their own handover.");
        h.setStatus("ACKNOWLEDGED"); h.setAcknowledgedBy(actor); h.setAcknowledgedAt(LocalDateTime.now()); h.setReceiverComment(limit(comment,1000));
        return repository.save(h);
    }
    public ShiftHandover returnForCorrection(long id,String actor,String reason){
        ShiftHandover h=require(id);
        if(!"SUBMITTED".equals(h.getStatus())) throw new IllegalStateException("Only submitted handovers can be returned.");
        if(reason==null||reason.isBlank()) throw new IllegalArgumentException("Explain what needs correction.");
        h.setStatus("RETURNED"); h.setAcknowledgedBy(actor); h.setAcknowledgedAt(LocalDateTime.now()); h.setReceiverComment(limit(reason,1000));
        return repository.save(h);
    }
    @Transactional(readOnly=true) public Map<String,Object> metrics(){
        Map<String,Object> m=new LinkedHashMap<>(); LocalDate today=LocalDate.now();
        m.put("total",repository.count()); m.put("drafts",repository.countByStatus("DRAFT"));
        m.put("awaiting",repository.countByStatus("SUBMITTED")); m.put("acknowledged",repository.countByStatus("ACKNOWLEDGED"));
        m.put("returned",repository.countByStatus("RETURNED")); m.put("urgent",repository.countByUrgentTrueAndStatusNot("ACKNOWLEDGED"));
        m.put("today",repository.countByShiftDateBetween(today.atStartOfDay(),today.plusDays(1).atStartOfDay())); return m;
    }
    private ShiftHandover require(long id){return repository.findById(id).orElseThrow(()->new NoSuchElementException("Handover not found: "+id));}
    private String nextReference(){String prefix="HAND-"+LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+"-";String value;int n=0;do{value=prefix+String.format("%04d",(System.nanoTime()+n++)%10000);}while(repository.existsByReferenceCode(value));return value;}
    private static String cleanStatus(String value){String v=blank(value);return v==null?null:(STATUSES.contains(v)?v:null);}
    private static String blank(String v){return v==null||v.isBlank()?null:v.trim();}
    private static String required(String v,String label,int max){String x=blank(v);if(x==null)throw new IllegalArgumentException(label+" is required.");if(x.length()>max)throw new IllegalArgumentException(label+" exceeds "+max+" characters.");return x;}
    private static String limit(String v,int max){String x=blank(v);if(x!=null&&x.length()>max)throw new IllegalArgumentException("Text exceeds "+max+" characters.");return x;}
}
