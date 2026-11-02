package com.healthcare.hospital.modules.phase4.payercontract.service;

import com.healthcare.hospital.modules.phase4.payercontract.entity.PayerContract;
import com.healthcare.hospital.modules.phase4.payercontract.repository.PayerContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Domain rules and lifecycle operations for payer contract management. */
@Service
@Transactional
public class PayerContractService {
    private final PayerContractRepository repository;
    private static final Set<String> STATUSES = Set.of("DRAFT", "NEGOTIATION", "ACTIVE", "EXPIRED", "TERMINATED");
    private static final Map<String,Set<String>> TRANSITIONS = transitions();
    public PayerContractService(PayerContractRepository repository) { this.repository=repository; }
    private static Map<String,Set<String>> transitions() {
        Map<String,Set<String>> map=new LinkedHashMap<>();
        map.put("DRAFT", Set.of("NEGOTIATION", "ACTIVE", "EXPIRED", "TERMINATED"));
        for(String status: STATUSES) map.putIfAbsent(status, new LinkedHashSet<>(STATUSES));
        map.put("TERMINATED", Set.of());
        return Collections.unmodifiableMap(map);
    }
    @Transactional(readOnly=true) public List<PayerContract> list() { return repository.findAll(); }
    @Transactional(readOnly=true) public Optional<PayerContract> find(long id) { return repository.findById(id); }
    public PayerContract create(PayerContract record,String actor) {
        validateForCreate(record); record.setStatus("DRAFT"); record.setCreatedBy(normalizeActor(actor));
        record.setCreatedAt(LocalDateTime.now()); record.setUpdatedAt(LocalDateTime.now());
        validateReferenceUnique(record); return repository.save(record);
    }
    public PayerContract update(long id,PayerContract incoming,String actor) {
        PayerContract current=required(id); validateEditable(incoming); validateReferenceUniqueForUpdate(incoming,id);
        current.setTitle(incoming.getTitle()); current.setDescription(incoming.getDescription());
        current.setReferenceCode(incoming.getReferenceCode()); current.setOwner(incoming.getOwner());
        current.setDepartment(incoming.getDepartment()); current.setCounterparty(incoming.getCounterparty());
        current.setAmount(incoming.getAmount()); current.setDueDate(incoming.getDueDate());
        current.setEvidenceReference(incoming.getEvidenceReference()); current.setUpdatedBy(normalizeActor(actor));
        current.setUpdatedAt(LocalDateTime.now()); return repository.save(current);
    }
    public PayerContract transition(long id,String next,String note,String actor) {
        PayerContract record=required(id); String target=normalizeStatus(next); validateStatus(target);
        Set<String> allowed=TRANSITIONS.getOrDefault(record.getStatus(),Set.of());
        if(!allowed.contains(target) && !record.getStatus().equals(target)) throw new IllegalStateException("Transition not allowed: "+record.getStatus()+" -> "+target);
        validateTransitionEvidence(record,target,note); record.setStatus(target); record.setDecisionNote(cleanNote(note));
        record.setUpdatedBy(normalizeActor(actor)); record.setUpdatedAt(LocalDateTime.now()); return repository.save(record);
    }
    @Transactional(readOnly=true) public List<PayerContract> search(String query) {
        if(!StringUtils.hasText(query)) return list(); String q=query.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream().filter(r->contains(r.getTitle(),q)||contains(r.getReferenceCode(),q)||contains(r.getOwner(),q)||contains(r.getDepartment(),q)||contains(r.getCounterparty(),q)).collect(Collectors.toList());
    }
    @Transactional(readOnly=true) public List<PayerContract> byStatus(String status) { validateStatus(normalizeStatus(status)); return repository.findByStatusIgnoreCaseOrderByUpdatedAtDesc(status); }
    @Transactional(readOnly=true) public List<PayerContract> overdue(LocalDate asOf) { LocalDate date=asOf==null?LocalDate.now():asOf; return repository.findByDueDateBeforeAndStatusNot(date,"TERMINATED"); }
    @Transactional(readOnly=true) public Map<String,Object> dashboard() {
        List<PayerContract> all=repository.findAll(); Map<String,Object> out=new LinkedHashMap<>();
        out.put("total",all.size()); out.put("statuses",statusCounts(all)); out.put("overdue",overdue(LocalDate.now()).size());
        out.put("totalAmount",all.stream().map(r->safeAmount(r.getAmount())).reduce(BigDecimal.ZERO,BigDecimal::add));
        out.put("openAmount",all.stream().filter(r->!"TERMINATED".equals(r.getStatus())).map(r->safeAmount(r.getAmount())).reduce(BigDecimal.ZERO,BigDecimal::add));
        out.put("dueSoon",all.stream().filter(r->r.getDueDate()!=null&&!r.getDueDate().isBefore(LocalDate.now())&&!r.getDueDate().isAfter(LocalDate.now().plusDays(7))).count());
        return out;
    }
    @Transactional(readOnly=true) public Map<String,Long> statusCounts(List<PayerContract> records) {
        Map<String,Long> counts=new LinkedHashMap<>(); for(String s:STATUSES) counts.put(s,0L);
        records.stream().filter(Objects::nonNull).forEach(r->counts.compute(r.getStatus(),(k,v)->v==null?1L:v+1L)); return counts;
    }
    @Transactional(readOnly=true) public List<PayerContract> dueBetween(LocalDate start,LocalDate end) {
        if(start==null||end==null||end.isBefore(start)) throw new IllegalArgumentException("A valid date range is required");
        return repository.findAll().stream().filter(r->r.getDueDate()!=null&&!r.getDueDate().isBefore(start)&&!r.getDueDate().isAfter(end)).sorted(Comparator.comparing(PayerContract::getDueDate)).collect(Collectors.toList());
    }
    public PayerContract addEvidence(long id,String evidence,String actor) {
        if(!StringUtils.hasText(evidence)) throw new IllegalArgumentException("Evidence reference is required");
        PayerContract r=required(id); r.setEvidenceReference(evidence.trim()); r.setUpdatedBy(normalizeActor(actor)); r.setUpdatedAt(LocalDateTime.now()); return repository.save(r);
    }
    public PayerContract assign(long id,String owner,String department,String actor) {
        if(!StringUtils.hasText(owner)) throw new IllegalArgumentException("An owner is required");
        PayerContract r=required(id); r.setOwner(owner.trim()); if(StringUtils.hasText(department))r.setDepartment(department.trim());
        r.setUpdatedBy(normalizeActor(actor)); r.setUpdatedAt(LocalDateTime.now()); return repository.save(r);
    }
    public PayerContract setDueDate(long id,LocalDate dueDate,String actor) {
        PayerContract r=required(id); if(dueDate!=null&&dueDate.isBefore(r.getCreatedAt().toLocalDate())) throw new IllegalArgumentException("Due date cannot precede creation date");
        r.setDueDate(dueDate); r.setUpdatedBy(normalizeActor(actor)); r.setUpdatedAt(LocalDateTime.now()); return repository.save(r);
    }
    @Transactional(readOnly=true) public String exportCsv() {
        StringBuilder csv=new StringBuilder("id,title,status,reference,owner,department,counterparty,amount,dueDate,createdAt\n");
        for(PayerContract r:repository.findAll()) csv.append(r.getId()).append(',').append(csvCell(r.getTitle())).append(',').append(csvCell(r.getStatus())).append(',').append(csvCell(r.getReferenceCode())).append(',').append(csvCell(r.getOwner())).append(',').append(csvCell(r.getDepartment())).append(',').append(csvCell(r.getCounterparty())).append(',').append(safeAmount(r.getAmount())).append(',').append(r.getDueDate()==null?"":r.getDueDate()).append(',').append(r.getCreatedAt()).append('\n');
        return csv.toString();
    }
    private PayerContract required(long id) { return repository.findById(id).orElseThrow(()->new NoSuchElementException("Record not found: "+id)); }
    private void validateForCreate(PayerContract r) { if(r==null)throw new IllegalArgumentException("Record is required"); validateEditable(r); validateStatus("DRAFT"); }
    private void validateEditable(PayerContract r) {
        if(r==null||!StringUtils.hasText(r.getTitle())) throw new IllegalArgumentException("Title is required");
        if(r.getTitle().trim().length()>180) throw new IllegalArgumentException("Title must be at most 180 characters");
        if(r.getAmount()!=null&&r.getAmount().compareTo(BigDecimal.ZERO)<0) throw new IllegalArgumentException("Amount cannot be negative");
        if(r.getDueDate()!=null&&r.getDueDate().isBefore(LocalDate.of(2000,1,1))) throw new IllegalArgumentException("Due date is outside the supported range");
        if(r.getDescription()!=null&&r.getDescription().length()>4000) throw new IllegalArgumentException("Description is too long");
    }
    private void validateReferenceUnique(PayerContract r) { if(StringUtils.hasText(r.getReferenceCode())&&!repository.findByReferenceCodeIgnoreCase(r.getReferenceCode().trim()).isEmpty()) throw new IllegalArgumentException("Reference code already exists"); }
    private void validateReferenceUniqueForUpdate(PayerContract r,long id) { if(StringUtils.hasText(r.getReferenceCode())) for(PayerContract found:repository.findByReferenceCodeIgnoreCase(r.getReferenceCode().trim())) if(!found.getId().equals(id)) throw new IllegalArgumentException("Reference code already exists"); }
    private void validateTransitionEvidence(PayerContract r,String target,String note) {
        if(Set.of("APPROVED","POSTED","PAID","ACCEPTED","CLOSED","VERIFIED").contains(target)&&!StringUtils.hasText(note)&&!StringUtils.hasText(r.getEvidenceReference())) throw new IllegalArgumentException("A decision note or evidence reference is required for this transition");
    }
    private void validateStatus(String status) { if(!STATUSES.contains(status)) throw new IllegalArgumentException("Unsupported status: "+status); }
    private static String normalizeStatus(String s) { return s==null?"":s.trim().toUpperCase(Locale.ROOT); }
    private static String normalizeActor(String s) { return StringUtils.hasText(s)?s.trim():"system"; }
    private static String cleanNote(String s) { if(s==null)return null; String v=s.trim(); return v.length()>1000?v.substring(0,1000):v; }
    private static boolean contains(String value,String q) { return value!=null&&value.toLowerCase(Locale.ROOT).contains(q); }
    private static BigDecimal safeAmount(BigDecimal amount) { return amount==null?BigDecimal.ZERO:amount; }
    private static String csvCell(String raw) { if(raw==null)return "\"\""; String s=raw.replace("\"","\"\""); if(s.startsWith("=")||s.startsWith("+")||s.startsWith("-")||s.startsWith("@"))s="'"+s; return "\""+s+"\""; }
}
