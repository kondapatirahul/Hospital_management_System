package com.healthcare.hospital.enterprise.phase4.budgettransfer.service;

import com.healthcare.hospital.enterprise.phase4.budgettransfer.entity.BudgetTransfer;
import com.healthcare.hospital.enterprise.phase4.budgettransfer.repository.BudgetTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Domain rules and lifecycle operations for budget transfers. */
@Service
@Transactional
public class BudgetTransferService {
    private final BudgetTransferRepository repository;
    private static final Set<String> STATUSES = Set.of("REQUESTED", "REVIEW", "APPROVED", "POSTED", "REJECTED", "CANCELLED");
    private static final Map<String,Set<String>> TRANSITIONS = transitions();
    public BudgetTransferService(BudgetTransferRepository repository) { this.repository=repository; }
    private static Map<String,Set<String>> transitions() {
        Map<String,Set<String>> map=new LinkedHashMap<>();
        map.put("REQUESTED", Set.of("REVIEW", "APPROVED", "POSTED", "REJECTED", "CANCELLED"));
        for(String status: STATUSES) map.putIfAbsent(status, new LinkedHashSet<>(STATUSES));
        map.put("CANCELLED", Set.of());
        return Collections.unmodifiableMap(map);
    }
    @Transactional(readOnly=true) public List<BudgetTransfer> list() { return repository.findAll(); }
    @Transactional(readOnly=true) public Optional<BudgetTransfer> find(long id) { return repository.findById(id); }
    public BudgetTransfer create(BudgetTransfer record,String actor) {
        validateForCreate(record); record.setStatus("REQUESTED"); record.setCreatedBy(normalizeActor(actor));
        record.setCreatedAt(LocalDateTime.now()); record.setUpdatedAt(LocalDateTime.now());
        validateReferenceUnique(record); return repository.save(record);
    }
    public BudgetTransfer update(long id,BudgetTransfer incoming,String actor) {
        BudgetTransfer current=required(id); validateEditable(incoming); validateReferenceUniqueForUpdate(incoming,id);
        current.setTitle(incoming.getTitle()); current.setDescription(incoming.getDescription());
        current.setReferenceCode(incoming.getReferenceCode()); current.setOwner(incoming.getOwner());
        current.setDepartment(incoming.getDepartment()); current.setCounterparty(incoming.getCounterparty());
        current.setAmount(incoming.getAmount()); current.setDueDate(incoming.getDueDate());
        current.setEvidenceReference(incoming.getEvidenceReference()); current.setUpdatedBy(normalizeActor(actor));
        current.setUpdatedAt(LocalDateTime.now()); return repository.save(current);
    }
    public BudgetTransfer transition(long id,String next,String note,String actor) {
        BudgetTransfer record=required(id); String target=normalizeStatus(next); validateStatus(target);
        Set<String> allowed=TRANSITIONS.getOrDefault(record.getStatus(),Set.of());
        if(!allowed.contains(target) && !record.getStatus().equals(target)) throw new IllegalStateException("Transition not allowed: "+record.getStatus()+" -> "+target);
        validateTransitionEvidence(record,target,note); record.setStatus(target); record.setDecisionNote(cleanNote(note));
        record.setUpdatedBy(normalizeActor(actor)); record.setUpdatedAt(LocalDateTime.now()); return repository.save(record);
    }
    @Transactional(readOnly=true) public List<BudgetTransfer> search(String query) {
        if(!StringUtils.hasText(query)) return list(); String q=query.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream().filter(r->contains(r.getTitle(),q)||contains(r.getReferenceCode(),q)||contains(r.getOwner(),q)||contains(r.getDepartment(),q)||contains(r.getCounterparty(),q)).collect(Collectors.toList());
    }
    @Transactional(readOnly=true) public List<BudgetTransfer> byStatus(String status) { validateStatus(normalizeStatus(status)); return repository.findByStatusIgnoreCaseOrderByUpdatedAtDesc(status); }
    @Transactional(readOnly=true) public List<BudgetTransfer> overdue(LocalDate asOf) { LocalDate date=asOf==null?LocalDate.now():asOf; return repository.findByDueDateBeforeAndStatusNot(date,"CANCELLED"); }
    @Transactional(readOnly=true) public Map<String,Object> dashboard() {
        List<BudgetTransfer> all=repository.findAll(); Map<String,Object> out=new LinkedHashMap<>();
        out.put("total",all.size()); out.put("statuses",statusCounts(all)); out.put("overdue",overdue(LocalDate.now()).size());
        out.put("totalAmount",all.stream().map(r->safeAmount(r.getAmount())).reduce(BigDecimal.ZERO,BigDecimal::add));
        out.put("openAmount",all.stream().filter(r->!"CANCELLED".equals(r.getStatus())).map(r->safeAmount(r.getAmount())).reduce(BigDecimal.ZERO,BigDecimal::add));
        out.put("dueSoon",all.stream().filter(r->r.getDueDate()!=null&&!r.getDueDate().isBefore(LocalDate.now())&&!r.getDueDate().isAfter(LocalDate.now().plusDays(7))).count());
        return out;
    }
    @Transactional(readOnly=true) public Map<String,Long> statusCounts(List<BudgetTransfer> records) {
        Map<String,Long> counts=new LinkedHashMap<>(); for(String s:STATUSES) counts.put(s,0L);
        records.stream().filter(Objects::nonNull).forEach(r->counts.compute(r.getStatus(),(k,v)->v==null?1L:v+1L)); return counts;
    }
    @Transactional(readOnly=true) public List<BudgetTransfer> dueBetween(LocalDate start,LocalDate end) {
        if(start==null||end==null||end.isBefore(start)) throw new IllegalArgumentException("A valid date range is required");
        return repository.findAll().stream().filter(r->r.getDueDate()!=null&&!r.getDueDate().isBefore(start)&&!r.getDueDate().isAfter(end)).sorted(Comparator.comparing(BudgetTransfer::getDueDate)).collect(Collectors.toList());
    }
    public BudgetTransfer addEvidence(long id,String evidence,String actor) {
        if(!StringUtils.hasText(evidence)) throw new IllegalArgumentException("Evidence reference is required");
        BudgetTransfer r=required(id); r.setEvidenceReference(evidence.trim()); r.setUpdatedBy(normalizeActor(actor)); r.setUpdatedAt(LocalDateTime.now()); return repository.save(r);
    }
    public BudgetTransfer assign(long id,String owner,String department,String actor) {
        if(!StringUtils.hasText(owner)) throw new IllegalArgumentException("An owner is required");
        BudgetTransfer r=required(id); r.setOwner(owner.trim()); if(StringUtils.hasText(department))r.setDepartment(department.trim());
        r.setUpdatedBy(normalizeActor(actor)); r.setUpdatedAt(LocalDateTime.now()); return repository.save(r);
    }
    public BudgetTransfer setDueDate(long id,LocalDate dueDate,String actor) {
        BudgetTransfer r=required(id); if(dueDate!=null&&dueDate.isBefore(r.getCreatedAt().toLocalDate())) throw new IllegalArgumentException("Due date cannot precede creation date");
        r.setDueDate(dueDate); r.setUpdatedBy(normalizeActor(actor)); r.setUpdatedAt(LocalDateTime.now()); return repository.save(r);
    }
    @Transactional(readOnly=true) public String exportCsv() {
        StringBuilder csv=new StringBuilder("id,title,status,reference,owner,department,counterparty,amount,dueDate,createdAt\n");
        for(BudgetTransfer r:repository.findAll()) csv.append(r.getId()).append(',').append(csvCell(r.getTitle())).append(',').append(csvCell(r.getStatus())).append(',').append(csvCell(r.getReferenceCode())).append(',').append(csvCell(r.getOwner())).append(',').append(csvCell(r.getDepartment())).append(',').append(csvCell(r.getCounterparty())).append(',').append(safeAmount(r.getAmount())).append(',').append(r.getDueDate()==null?"":r.getDueDate()).append(',').append(r.getCreatedAt()).append('\n');
        return csv.toString();
    }
    private BudgetTransfer required(long id) { return repository.findById(id).orElseThrow(()->new NoSuchElementException("Record not found: "+id)); }
    private void validateForCreate(BudgetTransfer r) { if(r==null)throw new IllegalArgumentException("Record is required"); validateEditable(r); validateStatus("REQUESTED"); }
    private void validateEditable(BudgetTransfer r) {
        if(r==null||!StringUtils.hasText(r.getTitle())) throw new IllegalArgumentException("Title is required");
        if(r.getTitle().trim().length()>180) throw new IllegalArgumentException("Title must be at most 180 characters");
        if(r.getAmount()!=null&&r.getAmount().compareTo(BigDecimal.ZERO)<0) throw new IllegalArgumentException("Amount cannot be negative");
        if(r.getDueDate()!=null&&r.getDueDate().isBefore(LocalDate.of(2000,1,1))) throw new IllegalArgumentException("Due date is outside the supported range");
        if(r.getDescription()!=null&&r.getDescription().length()>4000) throw new IllegalArgumentException("Description is too long");
    }
    private void validateReferenceUnique(BudgetTransfer r) { if(StringUtils.hasText(r.getReferenceCode())&&!repository.findByReferenceCodeIgnoreCase(r.getReferenceCode().trim()).isEmpty()) throw new IllegalArgumentException("Reference code already exists"); }
    private void validateReferenceUniqueForUpdate(BudgetTransfer r,long id) { if(StringUtils.hasText(r.getReferenceCode())) for(BudgetTransfer found:repository.findByReferenceCodeIgnoreCase(r.getReferenceCode().trim())) if(!found.getId().equals(id)) throw new IllegalArgumentException("Reference code already exists"); }
    private void validateTransitionEvidence(BudgetTransfer r,String target,String note) {
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
