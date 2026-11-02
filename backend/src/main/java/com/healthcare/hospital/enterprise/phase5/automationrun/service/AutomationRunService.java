package com.healthcare.hospital.enterprise.phase5.automationrun.service;

import com.healthcare.hospital.enterprise.phase5.automationrun.entity.AutomationRun;
import com.healthcare.hospital.enterprise.phase5.automationrun.repository.AutomationRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/** Application service for safe tracked automation execution records. All mutations are transactional and revision-tracked. */
@Service
public class AutomationRunService {
    private static final Set<String> TERMINAL = Set.of("SUCCEEDED", "CANCELLED", "REJECTED", "ARCHIVED");
    private static final Set<String> PRIORITIES = Set.of("LOW", "NORMAL", "HIGH", "URGENT");
    private final AutomationRunRepository repository;
    public AutomationRunService(AutomationRunRepository repository) { this.repository = repository; }

    @Transactional(readOnly=true) public List<AutomationRun> list() { return repository.findTop100ByOrderByCreatedAtDesc(); }
    @Transactional(readOnly=true) public Optional<AutomationRun> find(long id) { return repository.findById(id); }
    @Transactional(readOnly=true) public List<AutomationRun> byStatus(String status) { return repository.findByStatusIgnoreCaseOrderByCreatedAtDesc(clean(status)); }
    @Transactional(readOnly=true) public List<AutomationRun> byOwner(String owner) { return repository.findByOwnerIgnoreCaseOrderByDueDateAsc(clean(owner)); }
    @Transactional(readOnly=true) public List<AutomationRun> byDepartment(String department) { return repository.findByDepartmentIgnoreCaseOrderByCreatedAtDesc(clean(department)); }
    @Transactional(readOnly=true) public List<AutomationRun> overdue() { return repository.findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate.now(), "SUCCEEDED"); }
    @Transactional(readOnly=true) public long countStatus(String status) { return repository.countByStatusIgnoreCase(clean(status)); }
    @Transactional(readOnly=true) public long overdueCount() { return repository.countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate.now(), "SUCCEEDED"); }

    @Transactional public AutomationRun create(AutomationRun input, String actor) {
        Objects.requireNonNull(input, "A record is required");
        validateTitle(input.getTitle());
        input.setStatus(normalizeStatus(input.getStatus(), "QUEUED"));
        input.setPriority(normalizePriority(input.getPriority()));
        input.setCreatedAt(LocalDateTime.now()); input.setUpdatedAt(LocalDateTime.now());
        input.setCreatedBy(actorName(actor)); input.setUpdatedBy(actorName(actor)); input.setRevisionCount(1);
        validateDate(input.getDueDate()); validateReference(input.getReferenceCode());
        if (input.getReferenceCode()!=null && repository.findByReferenceCodeIgnoreCase(input.getReferenceCode()).isPresent()) throw new IllegalArgumentException("Reference code already exists");
        return repository.save(input);
    }

    @Transactional public AutomationRun update(long id, AutomationRun input, String actor) {
        AutomationRun current = required(id); ensureMutable(current);
        validateTitle(input.getTitle()); validateDate(input.getDueDate());
        current.setTitle(input.getTitle()); current.setDescription(input.getDescription()); current.setOwner(input.getOwner());
        current.setDepartment(input.getDepartment()); current.setCategory(input.getCategory()); current.setPriority(normalizePriority(input.getPriority()));
        current.setDueDate(input.getDueDate()); current.setEvidenceReference(input.getEvidenceReference());
        if (input.getReferenceCode()!=null && !input.getReferenceCode().equalsIgnoreCase(current.getReferenceCode())) {
            validateReference(input.getReferenceCode());
            if (repository.findByReferenceCodeIgnoreCase(input.getReferenceCode()).filter(r -> !r.getId().equals(id)).isPresent()) throw new IllegalArgumentException("Reference code already exists");
            current.setReferenceCode(input.getReferenceCode());
        }
        touch(current, actor); return repository.save(current);
    }

    @Transactional public AutomationRun transition(long id, String targetStatus, String note, String actor) {
        AutomationRun current = required(id); String target=normalizeStatus(targetStatus, null);
        if (TERMINAL.contains(current.getStatus())) throw new IllegalStateException("A terminal record cannot be changed");
        if (!isAllowedTransition(current.getStatus(), target)) throw new IllegalArgumentException("Unsupported transition: " + current.getStatus() + " -> " + target);
        current.setStatus(target); if (note!=null && !note.isBlank()) current.setDecisionNote(appendNote(current.getDecisionNote(), note, actor));
        touch(current, actor); return repository.save(current);
    }

    @Transactional public AutomationRun assign(long id, String owner, String department, String actor) {
        AutomationRun current=required(id); ensureMutable(current);
        if (owner==null || owner.isBlank()) throw new IllegalArgumentException("An owner is required");
        current.setOwner(owner.trim()); if (department!=null && !department.isBlank()) current.setDepartment(department.trim());
        touch(current, actor); return repository.save(current);
    }

    @Transactional public AutomationRun addEvidence(long id, String evidenceReference, String note, String actor) {
        AutomationRun current=required(id); ensureMutable(current);
        if (evidenceReference==null || evidenceReference.isBlank()) throw new IllegalArgumentException("Evidence reference is required");
        if (evidenceReference.length()>1000) throw new IllegalArgumentException("Evidence reference is too long");
        current.setEvidenceReference(evidenceReference.trim());
        if (note!=null && !note.isBlank()) current.setDecisionNote(appendNote(current.getDecisionNote(), note, actor));
        touch(current, actor); return repository.save(current);
    }

    @Transactional public AutomationRun addNote(long id, String note, String actor) {
        AutomationRun current=required(id); if (note==null || note.isBlank()) throw new IllegalArgumentException("A note is required");
        if (note.length()>1500) throw new IllegalArgumentException("Note must be 1500 characters or fewer");
        current.setDecisionNote(appendNote(current.getDecisionNote(), note, actor)); touch(current, actor); return repository.save(current);
    }

    @Transactional public AutomationRun reschedule(long id, LocalDate dueDate, String reason, String actor) {
        AutomationRun current=required(id); ensureMutable(current); validateDate(dueDate);
        if (reason==null || reason.isBlank()) throw new IllegalArgumentException("A reschedule reason is required");
        current.setDueDate(dueDate); current.setDecisionNote(appendNote(current.getDecisionNote(), "Rescheduled to " + dueDate + ": " + reason, actor));
        touch(current, actor); return repository.save(current);
    }

    @Transactional public AutomationRun changePriority(long id, String priority, String reason, String actor) {
        AutomationRun current=required(id); ensureMutable(current); String normalized=normalizePriority(priority);
        if (reason==null || reason.isBlank()) throw new IllegalArgumentException("A priority-change reason is required");
        current.setPriority(normalized); current.setDecisionNote(appendNote(current.getDecisionNote(), "Priority changed to " + normalized + ": " + reason, actor));
        touch(current, actor); return repository.save(current);
    }

    @Transactional public AutomationRun close(long id, String outcome, String actor) {
        if (outcome==null || outcome.isBlank()) throw new IllegalArgumentException("Closure outcome is required");
        return transition(id, "SUCCEEDED", outcome, actor);
    }

    @Transactional public AutomationRun cancel(long id, String reason, String actor) {
        if (reason==null || reason.isBlank()) throw new IllegalArgumentException("Cancellation reason is required");
        return transition(id, "CANCELLED", reason, actor);
    }

    @Transactional public AutomationRun reopen(long id, String reason, String actor) {
        AutomationRun current=required(id);
        if (!TERMINAL.contains(current.getStatus())) throw new IllegalStateException("Only terminal records can be reopened");
        if (reason==null || reason.isBlank()) throw new IllegalArgumentException("A reopening reason is required");
        current.setStatus("QUEUED"); current.setDecisionNote(appendNote(current.getDecisionNote(), "Reopened: " + reason, actor));
        touch(current, actor); return repository.save(current);
    }

    @Transactional(readOnly=true) public Map<String,Object> dashboard() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("total", repository.count()); result.put("open", countStatus("QUEUED")); result.put("completed", countStatus("SUCCEEDED"));
        result.put("overdue", overdueCount()); result.put("highPriority", list().stream().filter(x -> "HIGH".equals(x.getPriority()) || "URGENT".equals(x.getPriority())).count());
        result.put("dueSoon", dueSoonCount()); result.put("recent", list().stream().limit(8).collect(Collectors.toList()));
        result.put("statusDistribution", statusDistribution()); result.put("departmentDistribution", departmentDistribution());
        result.put("governance", governanceReport()); result.put("riskRegisterCount", riskRegister().size());
        return result;
    }

    @Transactional(readOnly=true) public long dueSoonCount() {
        LocalDate edge=LocalDate.now().plusDays(7);
        return list().stream().filter(x -> x.getDueDate()!=null && !x.getDueDate().isBefore(LocalDate.now()) && !x.getDueDate().isAfter(edge) && !TERMINAL.contains(x.getStatus())).count();
    }
    @Transactional(readOnly=true) public Map<String,Long> statusDistribution() {
        return list().stream().collect(Collectors.groupingBy(x -> safeLabel(x.getStatus()), TreeMap::new, Collectors.counting()));
    }
    @Transactional(readOnly=true) public Map<String,Long> departmentDistribution() {
        return list().stream().collect(Collectors.groupingBy(x -> safeLabel(x.getDepartment()), TreeMap::new, Collectors.counting()));
    }
    @Transactional(readOnly=true) public List<AutomationRun> search(String query) {
        String q=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return list();
        return list().stream().filter(x -> contains(x.getTitle(),q) || contains(x.getDescription(),q) || contains(x.getReferenceCode(),q) || contains(x.getOwner(),q) || contains(x.getDepartment(),q) || contains(x.getCategory(),q)).collect(Collectors.toList());
    }
    @Transactional(readOnly=true) public List<AutomationRun> filter(String status, String priority, String department, LocalDate dueBefore) {
        return list().stream().filter(x -> status==null || status.isBlank() || status.equalsIgnoreCase(x.getStatus()))
            .filter(x -> priority==null || priority.isBlank() || priority.equalsIgnoreCase(x.getPriority()))
            .filter(x -> department==null || department.isBlank() || department.equalsIgnoreCase(x.getDepartment()))
            .filter(x -> dueBefore==null || (x.getDueDate()!=null && !x.getDueDate().isAfter(dueBefore))).collect(Collectors.toList());
    }
    @Transactional(readOnly=true) public String exportCsv() {
        StringBuilder out=new StringBuilder("id,title,status,priority,owner,department,dueDate,referenceCode,createdAt\n");
        for (AutomationRun x:list()) out.append(x.getId()).append(',').append(csv(x.getTitle())).append(',').append(csv(x.getStatus())).append(',').append(csv(x.getPriority())).append(',').append(csv(x.getOwner())).append(',').append(csv(x.getDepartment())).append(',').append(csv(String.valueOf(x.getDueDate()))).append(',').append(csv(x.getReferenceCode())).append(',').append(csv(String.valueOf(x.getCreatedAt()))).append('\n');
        return out.toString();
    }
    @Transactional(readOnly=true) public Map<String,Object> operationalSummary() {
        List<AutomationRun> all=list(); Map<String,Object> summary=new LinkedHashMap<>();
        summary.put("total", all.size()); summary.put("overdue", overdueCount()); summary.put("dueSoon", dueSoonCount());
        summary.put("terminal", all.stream().filter(x -> TERMINAL.contains(x.getStatus())).count());
        summary.put("unassigned", all.stream().filter(x -> x.getOwner()==null || x.getOwner().isBlank()).count());
        summary.put("missingEvidence", all.stream().filter(x -> x.getEvidenceReference()==null || x.getEvidenceReference().isBlank()).count());
        summary.put("urgent", all.stream().filter(x -> "URGENT".equals(x.getPriority())).count()); return summary;
    }

    private AutomationRun required(long id) { return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Record not found: " + id)); }
    private void ensureMutable(AutomationRun item) { if (TERMINAL.contains(item.getStatus())) throw new IllegalStateException("A terminal record cannot be edited"); }
    private void touch(AutomationRun item, String actor) { item.setUpdatedAt(LocalDateTime.now()); item.setUpdatedBy(actorName(actor)); item.setRevisionCount(item.getRevisionCount()+1); }
    private static void validateTitle(String title) { if (title==null || title.isBlank()) throw new IllegalArgumentException("Title is required"); if (title.trim().length()>180) throw new IllegalArgumentException("Title is too long"); }
    private static void validateDate(LocalDate date) { if (date!=null && date.isBefore(LocalDate.of(2000,1,1))) throw new IllegalArgumentException("Date is outside the supported range"); }
    private static void validateReference(String ref) { if (ref!=null && !ref.matches("[A-Za-z0-9._/-]{1,100}")) throw new IllegalArgumentException("Reference code contains unsupported characters"); }
    private static String normalizePriority(String p) { String n=(p==null || p.isBlank())?"NORMAL":p.trim().toUpperCase(Locale.ROOT); if (!PRIORITIES.contains(n)) throw new IllegalArgumentException("Unsupported priority: " + n); return n; }
    private static String normalizeStatus(String status, String fallback) { String n=(status==null || status.isBlank())?fallback:status.trim().toUpperCase(Locale.ROOT); if (n==null || !n.matches("[A-Z][A-Z0-9_ -]{0,79}")) throw new IllegalArgumentException("Invalid status"); return n; }
    private static String actorName(String actor) { return actor==null || actor.isBlank()?"system":actor.trim().substring(0,Math.min(120,actor.trim().length())); }
    private static String clean(String value) { return value==null?"":value.trim(); }
    private static String safeLabel(String value) { return value==null || value.isBlank()?"Unspecified":value.trim(); }
    private static boolean contains(String value, String q) { return value!=null && value.toLowerCase(Locale.ROOT).contains(q); }
    private static String appendNote(String existing, String note, String actor) {
        String line="["+LocalDateTime.now()+" | "+actorName(actor)+"] "+note.trim();
        String combined=(existing==null || existing.isBlank())?line:existing+"\n"+line;
        return combined.length()>2000?combined.substring(combined.length()-2000):combined;
    }
    private static String csv(String value) {
        if (value==null || "null".equals(value)) return "\"\"";
        String safe=value.replace("\"","\"\"");
        if (!safe.isEmpty() && "=+-@\t\r".indexOf(safe.charAt(0))>=0) safe="'"+safe;
        return "\""+safe+"\"";
    }
    private static boolean isAllowedTransition(String from, String to) {
        if (from==null || to==null || from.equals(to)) return false;
        if ("CANCELLED".equals(to)) return !TERMINAL.contains(from);
        if ("REJECTED".equals(to)) return !TERMINAL.contains(from);
        return switch (from) {
            case "DRAFT" -> Set.of("OPEN","PENDING","QUEUED","PLANNED","ACTIVE","IN_REVIEW","CANCELLED").contains(to);
            case "OPEN" -> Set.of("IN_PROGRESS","IN_REVIEW","PENDING","ACTIVE","RESOLVED","CLOSED","SUCCEEDED").contains(to);
            case "PENDING" -> Set.of("IN_PROGRESS","IN_REVIEW","ACTIVE","APPROVED","REJECTED","CANCELLED","SUCCEEDED").contains(to);
            case "QUEUED" -> Set.of("RUNNING","IN_PROGRESS","FAILED","CANCELLED","SUCCEEDED").contains(to);
            case "PLANNED" -> Set.of("ACTIVE","IN_PROGRESS","COMPLETED","CANCELLED").contains(to);
            case "ACTIVE" -> Set.of("PAUSED","IN_PROGRESS","COMPLETED","RESOLVED","CLOSED","SUCCEEDED").contains(to);
            case "IN_PROGRESS" -> Set.of("IN_REVIEW","PENDING","COMPLETED","RESOLVED","CLOSED","SUCCEEDED").contains(to);
            case "IN_REVIEW" -> Set.of("IN_PROGRESS","APPROVED","REJECTED","COMPLETED","RESOLVED","CLOSED","SUCCEEDED").contains(to);
            case "FAILED" -> Set.of("QUEUED","RETRYING","CANCELLED").contains(to);
            case "RETRYING" -> Set.of("QUEUED","RUNNING","FAILED","CANCELLED").contains(to);
            case "PAUSED" -> Set.of("ACTIVE","CANCELLED").contains(to);
            default -> false;
        };
    }

    /** Operational rule: Unassigned. */
    @Transactional(readOnly=true) public boolean isUnassigned(AutomationRun item) {
        if (item == null) return false;
        return item.getOwner() == null || item.getOwner().isBlank();
    }

    /** Operational rule: MsingReference. */
    @Transactional(readOnly=true) public boolean isMissingReference(AutomationRun item) {
        if (item == null) return false;
        return item.getReferenceCode() == null || item.getReferenceCode().isBlank();
    }

    /** Operational rule: MsingEvidence. */
    @Transactional(readOnly=true) public boolean isMissingEvidence(AutomationRun item) {
        if (item == null) return false;
        return item.getEvidenceReference() == null || item.getEvidenceReference().isBlank();
    }

    /** Operational rule: MsingDescription. */
    @Transactional(readOnly=true) public boolean isMissingDescription(AutomationRun item) {
        if (item == null) return false;
        return item.getDescription() == null || item.getDescription().isBlank();
    }

    /** Operational rule: Terminal. */
    @Transactional(readOnly=true) public boolean isTerminal(AutomationRun item) {
        if (item == null) return false;
        return TERMINAL.contains(item.getStatus());
    }

    /** Operational rule: Urgent. */
    @Transactional(readOnly=true) public boolean isUrgent(AutomationRun item) {
        if (item == null) return false;
        return "URGENT".equalsIgnoreCase(item.getPriority());
    }

    /** Operational rule: HighPriority. */
    @Transactional(readOnly=true) public boolean isHighPriority(AutomationRun item) {
        if (item == null) return false;
        return "HIGH".equalsIgnoreCase(item.getPriority()) || "URGENT".equalsIgnoreCase(item.getPriority());
    }

    /** Operational rule: DueToday. */
    @Transactional(readOnly=true) public boolean isDueToday(AutomationRun item) {
        if (item == null) return false;
        return item.getDueDate() != null && item.getDueDate().equals(LocalDate.now()) && !TERMINAL.contains(item.getStatus());
    }

    /** Operational rule: DueThWeek. */
    @Transactional(readOnly=true) public boolean isDueThisWeek(AutomationRun item) {
        if (item == null) return false;
        return item.getDueDate() != null && !item.getDueDate().isBefore(LocalDate.now()) && !item.getDueDate().isAfter(LocalDate.now().plusDays(7)) && !TERMINAL.contains(item.getStatus());
    }

    /** Operational rule: Overdue. */
    @Transactional(readOnly=true) public boolean isOverdue(AutomationRun item) {
        if (item == null) return false;
        return item.getDueDate() != null && item.getDueDate().isBefore(LocalDate.now()) && !TERMINAL.contains(item.getStatus());
    }

    /** Operational rule: RecentlyUpdated. */
    @Transactional(readOnly=true) public boolean isRecentlyUpdated(AutomationRun item) {
        if (item == null) return false;
        return item.getUpdatedAt() != null && item.getUpdatedAt().isAfter(LocalDateTime.now().minusDays(7));
    }

    /** Operational rule: Stale. */
    @Transactional(readOnly=true) public boolean isStale(AutomationRun item) {
        if (item == null) return false;
        LocalDateTime last = item.getUpdatedAt() == null ? item.getCreatedAt() : item.getUpdatedAt();
        return last != null && last.isBefore(LocalDateTime.now().minusDays(30)) && !TERMINAL.contains(item.getStatus());
    }

    /** Operational rule: AwaitingOwner. */
    @Transactional(readOnly=true) public boolean isAwaitingOwner(AutomationRun item) {
        if (item == null) return false;
        return isUnassigned(item) && !TERMINAL.contains(item.getStatus());
    }

    /** Operational rule: AwaitingEvidence. */
    @Transactional(readOnly=true) public boolean isAwaitingEvidence(AutomationRun item) {
        if (item == null) return false;
        return isMissingEvidence(item) && !TERMINAL.contains(item.getStatus());
    }

    /** Operational rule: ReadyForReview. */
    @Transactional(readOnly=true) public boolean isReadyForReview(AutomationRun item) {
        if (item == null) return false;
        return !TERMINAL.contains(item.getStatus()) && !isMissingDescription(item) && !isMissingEvidence(item) && !isUnassigned(item);
    }

    /** Operational rule: ReadyToClose. */
    @Transactional(readOnly=true) public boolean isReadyToClose(AutomationRun item) {
        if (item == null) return false;
        return !TERMINAL.contains(item.getStatus()) && !isMissingDescription(item) && !isMissingEvidence(item) && !isUnassigned(item);
    }

    /** Operational rule: daysUntilDue. */
    @Transactional(readOnly=true) public long daysUntilDue(AutomationRun item) {
        if (item == null) return 0;
        return item.getDueDate() == null ? 0L : java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), item.getDueDate());
    }

    /** Operational rule: ageInDays. */
    @Transactional(readOnly=true) public long ageInDays(AutomationRun item) {
        if (item == null) return 0;
        return item.getCreatedAt() == null ? 0L : java.time.temporal.ChronoUnit.DAYS.between(item.getCreatedAt().toLocalDate(), LocalDate.now());
    }

    /** Operational rule: revionAgeInDays. */
    @Transactional(readOnly=true) public long revisionAgeInDays(AutomationRun item) {
        if (item == null) return 0;
        LocalDateTime last = item.getUpdatedAt() == null ? item.getCreatedAt() : item.getUpdatedAt();
        return last == null ? 0L : java.time.temporal.ChronoUnit.DAYS.between(last.toLocalDate(), LocalDate.now());
    }

    /** Operational rule: rkScore. */
    @Transactional(readOnly=true) public long riskScore(AutomationRun item) {
        if (item == null) return 0;
        int score = 0;
        if (isOverdue(item)) score += 35;
        if (isHighPriority(item)) score += 20;
        if (isMissingEvidence(item)) score += 15;
        if (isUnassigned(item)) score += 15;
        if (isStale(item)) score += 15;
        return Math.min(100, score);
    }

    /** Operational rule: rkBand. */
    @Transactional(readOnly=true) public String riskBand(AutomationRun item) {
        if (item == null) return "";
        long score = riskScore(item);
        return score >= 70 ? "CRITICAL" : score >= 45 ? "HIGH" : score >= 20 ? "MEDIUM" : "LOW";
    }

    /** Operational rule: recommendedNextAction. */
    @Transactional(readOnly=true) public String recommendedNextAction(AutomationRun item) {
        if (item == null) return "";
        if (isTerminal(item)) return "No action required; record is terminal";
        if (isOverdue(item)) return "Review overdue commitment and record an updated due date";
        if (isUnassigned(item)) return "Assign a named owner and responsible department";
        if (isMissingEvidence(item)) return "Attach an evidence reference before approval";
        if (isStale(item)) return "Review stale record and update progress";
        if (isReadyToClose(item)) return "Review completion evidence and decide whether to close";
        return "Continue the current workflow and keep the record updated";
    }

    /** Operational rule: slaState. */
    @Transactional(readOnly=true) public String slaState(AutomationRun item) {
        if (item == null) return "";
        if (isTerminal(item)) return "STOPPED";
        if (isOverdue(item)) return "BREACHED";
        if (isDueToday(item)) return "DUE_TODAY";
        if (isDueThisWeek(item)) return "AT_RISK";
        return "ON_TRACK";
    }

    /** Operational rule: recordCompletenessPercent. */
    @Transactional(readOnly=true) public long recordCompletenessPercent(AutomationRun item) {
        if (item == null) return 0;
        int total = 6; int complete = 0;
        if (item.getTitle() != null && !item.getTitle().isBlank()) complete++;
        if (!isMissingDescription(item)) complete++;
        if (!isMissingReference(item)) complete++;
        if (!isUnassigned(item)) complete++;
        if (item.getDueDate() != null) complete++;
        if (!isMissingEvidence(item)) complete++;
        return Math.round((complete * 100.0) / total);
    }

    /** Operational rule: lifecycleLabel. */
    @Transactional(readOnly=true) public String lifecycleLabel(AutomationRun item) {
        if (item == null) return "";
        if (isTerminal(item)) return "Complete";
        if ("DRAFT".equals(item.getStatus())) return "Preparation";
        if ("IN_REVIEW".equals(item.getStatus())) return "Under review";
        if ("IN_PROGRESS".equals(item.getStatus()) || "RUNNING".equals(item.getStatus())) return "In progress";
        if ("PENDING".equals(item.getStatus()) || "QUEUED".equals(item.getStatus())) return "Waiting";
        return "Active";
    }

    /** Operational rule: ownershipLabel. */
    @Transactional(readOnly=true) public String ownershipLabel(AutomationRun item) {
        if (item == null) return "";
        String owner = item.getOwner();
        if (owner == null || owner.isBlank()) return "Unassigned";
        String department = item.getDepartment();
        return department == null || department.isBlank() ? owner.trim() : owner.trim() + " · " + department.trim();
    }

    /** Operational rule: safeReferenceLabel. */
    @Transactional(readOnly=true) public String safeReferenceLabel(AutomationRun item) {
        if (item == null) return "";
        String ref = item.getReferenceCode();
        if (ref == null || ref.isBlank()) return "Not assigned";
        return ref.trim().replaceAll("[^A-Za-z0-9._/-]", "");
    }

    /** Operational rule: ageBand. */
    @Transactional(readOnly=true) public String ageBand(AutomationRun item) {
        if (item == null) return "";
        long age = ageInDays(item);
        return age < 1 ? "TODAY" : age <= 7 ? "THIS_WEEK" : age <= 30 ? "THIS_MONTH" : "OLDER";
    }

    /** Operational rule: priorityRank. */
    @Transactional(readOnly=true) public long priorityRank(AutomationRun item) {
        if (item == null) return 0;
        return switch (normalizePriority(item.getPriority())) {
            case "URGENT" -> 4L; case "HIGH" -> 3L; case "NORMAL" -> 2L; default -> 1L;
        };
    }

    /** Operational rule: StatusValid. */
    @Transactional(readOnly=true) public boolean isStatusValid(AutomationRun item) {
        if (item == null) return false;
        return item.getStatus() != null && item.getStatus().matches("[A-Z][A-Z0-9_ -]{0,79}");
    }

    /** Operational rule: TitleValid. */
    @Transactional(readOnly=true) public boolean isTitleValid(AutomationRun item) {
        if (item == null) return false;
        return item.getTitle() != null && !item.getTitle().isBlank() && item.getTitle().trim().length() <= 180;
    }

    /** Operational rule: DueDatePlausible. */
    @Transactional(readOnly=true) public boolean isDueDatePlausible(AutomationRun item) {
        if (item == null) return false;
        return item.getDueDate() == null || !item.getDueDate().isBefore(LocalDate.of(2000, 1, 1));
    }

    /** Operational rule: ReferenceValid. */
    @Transactional(readOnly=true) public boolean isReferenceValid(AutomationRun item) {
        if (item == null) return false;
        return item.getReferenceCode() == null || item.getReferenceCode().matches("[A-Za-z0-9._/-]{1,100}");
    }

    /** Operational rule: OwnerLabelValid. */
    @Transactional(readOnly=true) public boolean isOwnerLabelValid(AutomationRun item) {
        if (item == null) return false;
        return item.getOwner() == null || item.getOwner().trim().length() <= 160;
    }

    /** Operational rule: DepartmentLabelValid. */
    @Transactional(readOnly=true) public boolean isDepartmentLabelValid(AutomationRun item) {
        if (item == null) return false;
        return item.getDepartment() == null || item.getDepartment().trim().length() <= 160;
    }

    /** Operational rule: EvidenceReferenceValid. */
    @Transactional(readOnly=true) public boolean isEvidenceReferenceValid(AutomationRun item) {
        if (item == null) return false;
        return item.getEvidenceReference() == null || item.getEvidenceReference().trim().length() <= 1000;
    }

    /** Operational rule: DescriptionValid. */
    @Transactional(readOnly=true) public boolean isDescriptionValid(AutomationRun item) {
        if (item == null) return false;
        return item.getDescription() == null || item.getDescription().length() <= 4000;
    }

    /** Operational rule: DecionNoteValid. */
    @Transactional(readOnly=true) public boolean isDecisionNoteValid(AutomationRun item) {
        if (item == null) return false;
        return item.getDecisionNote() == null || item.getDecisionNote().length() <= 2000;
    }

    /** Operational rule: RevionTracked. */
    @Transactional(readOnly=true) public boolean isRevisionTracked(AutomationRun item) {
        if (item == null) return false;
        return item.getRevisionCount() > 0 && item.getCreatedAt() != null;
    }

    /** Operational rule: AuditMetadataPresent. */
    @Transactional(readOnly=true) public boolean isAuditMetadataPresent(AutomationRun item) {
        if (item == null) return false;
        return item.getCreatedAt() != null && item.getCreatedBy() != null && !item.getCreatedBy().isBlank();
    }

    /** Operational rule: RecentlyCreated. */
    @Transactional(readOnly=true) public boolean isRecentlyCreated(AutomationRun item) {
        if (item == null) return false;
        return item.getCreatedAt() != null && item.getCreatedAt().isAfter(LocalDateTime.now().minusHours(24));
    }

    /** Operational rule: HighRk. */
    @Transactional(readOnly=true) public boolean isHighRisk(AutomationRun item) {
        if (item == null) return false;
        return riskScore(item) >= 70;
    }

    /** Operational rule: MediumRk. */
    @Transactional(readOnly=true) public boolean isMediumRisk(AutomationRun item) {
        if (item == null) return false;
        long score = riskScore(item); return score >= 45 && score < 70;
    }

    /** Operational rule: LowRk. */
    @Transactional(readOnly=true) public boolean isLowRisk(AutomationRun item) {
        if (item == null) return false;
        return riskScore(item) < 20;
    }

    /** Operational rule: needsManagerAttention. */
    @Transactional(readOnly=true) public boolean needsManagerAttention(AutomationRun item) {
        if (item == null) return false;
        return isOverdue(item) || isHighRisk(item) || (isUrgent(item) && isStale(item));
    }

    /** Operational rule: needsEvidenceReview. */
    @Transactional(readOnly=true) public boolean needsEvidenceReview(AutomationRun item) {
        if (item == null) return false;
        return !isMissingEvidence(item) && !isTerminal(item);
    }

    /** Operational rule: needsOwnershipReview. */
    @Transactional(readOnly=true) public boolean needsOwnershipReview(AutomationRun item) {
        if (item == null) return false;
        return isUnassigned(item) || isStale(item);
    }

    /** Operational rule: Actionable. */
    @Transactional(readOnly=true) public boolean isActionable(AutomationRun item) {
        if (item == null) return false;
        return !isTerminal(item) && isTitleValid(item) && isStatusValid(item);
    }

    /** Operational rule: EscalationCandidate. */
    @Transactional(readOnly=true) public boolean isEscalationCandidate(AutomationRun item) {
        if (item == null) return false;
        return !isTerminal(item) && (isOverdue(item) || isUrgent(item) || isHighRisk(item));
    }

    /** Operational rule: DueWithinOneDay. */
    @Transactional(readOnly=true) public boolean isDueWithinOneDay(AutomationRun item) {
        if (item == null) return false;
        return item.getDueDate() != null && !item.getDueDate().isBefore(LocalDate.now()) && !item.getDueDate().isAfter(LocalDate.now().plusDays(1)) && !isTerminal(item);
    }

    /** Operational rule: DueWithinThirtyDays. */
    @Transactional(readOnly=true) public boolean isDueWithinThirtyDays(AutomationRun item) {
        if (item == null) return false;
        return item.getDueDate() != null && !item.getDueDate().isBefore(LocalDate.now()) && !item.getDueDate().isAfter(LocalDate.now().plusDays(30)) && !isTerminal(item);
    }

    /** Operational rule: statusForReport. */
    @Transactional(readOnly=true) public String statusForReport(AutomationRun item) {
        if (item == null) return "";
        if (item.getStatus() == null) return "UNKNOWN";
        String status = item.getStatus().trim().toUpperCase(Locale.ROOT);
        if (TERMINAL.contains(status)) return "TERMINAL";
        if (Set.of("DRAFT", "PENDING", "QUEUED", "PLANNED").contains(status)) return "WAITING";
        if (Set.of("IN_PROGRESS", "RUNNING", "ACTIVE", "IN_REVIEW", "RETRYING").contains(status)) return "ACTIVE";
        return "OTHER";
    }

    /** Operational rule: operationalSummaryLine. */
    @Transactional(readOnly=true) public String operationalSummaryLine(AutomationRun item) {
        if (item == null) return "";
        return safeReferenceLabel(item) + " | " + lifecycleLabel(item) + " | " + riskBand(item)
            + " | SLA=" + slaState(item) + " | owner=" + ownershipLabel(item);
    }

    /** Operational rule: governanceCheck01. */
    @Transactional(readOnly=true) public boolean governanceCheck01(AutomationRun item) {
        if (item == null) return false;
        return isTitleValid(item) && isStatusValid(item) && isDueDatePlausible(item);
    }

    /** Operational rule: governanceCheck02. */
    @Transactional(readOnly=true) public boolean governanceCheck02(AutomationRun item) {
        if (item == null) return false;
        return !isTerminal(item) && (isMissingEvidence(item) || isUnassigned(item));
    }

    /** Operational rule: governanceCheck03. */
    @Transactional(readOnly=true) public boolean governanceCheck03(AutomationRun item) {
        if (item == null) return false;
        return isAuditMetadataPresent(item) && isRevisionTracked(item);
    }

    /** Operational rule: governanceCheck04. */
    @Transactional(readOnly=true) public boolean governanceCheck04(AutomationRun item) {
        if (item == null) return false;
        return isEscalationCandidate(item) && !isTerminal(item);
    }

    /** Operational rule: governanceCheck05. */
    @Transactional(readOnly=true) public boolean governanceCheck05(AutomationRun item) {
        if (item == null) return false;
        return isReadyForReview(item) && recordCompletenessPercent(item) >= 80;
    }

    /** Operational rule: governanceCheck06. */
    @Transactional(readOnly=true) public boolean governanceCheck06(AutomationRun item) {
        if (item == null) return false;
        return isRecentlyCreated(item) || isRecentlyUpdated(item) || isDueThisWeek(item);
    }

    /** Operational rule: governanceCheck07. */
    @Transactional(readOnly=true) public boolean governanceCheck07(AutomationRun item) {
        if (item == null) return false;
        return isTitleValid(item) && isStatusValid(item) && isDueDatePlausible(item);
    }

    /** Operational rule: governanceCheck08. */
    @Transactional(readOnly=true) public boolean governanceCheck08(AutomationRun item) {
        if (item == null) return false;
        return !isTerminal(item) && (isMissingEvidence(item) || isUnassigned(item));
    }

    /** Operational rule: governanceCheck09. */
    @Transactional(readOnly=true) public boolean governanceCheck09(AutomationRun item) {
        if (item == null) return false;
        return isAuditMetadataPresent(item) && isRevisionTracked(item);
    }

    /** Operational rule: governanceCheck10. */
    @Transactional(readOnly=true) public boolean governanceCheck10(AutomationRun item) {
        if (item == null) return false;
        return isEscalationCandidate(item) && !isTerminal(item);
    }

    /** Operational rule: governanceCheck11. */
    @Transactional(readOnly=true) public boolean governanceCheck11(AutomationRun item) {
        if (item == null) return false;
        return isReadyForReview(item) && recordCompletenessPercent(item) >= 80;
    }

    /** Operational rule: governanceCheck12. */
    @Transactional(readOnly=true) public boolean governanceCheck12(AutomationRun item) {
        if (item == null) return false;
        return isRecentlyCreated(item) || isRecentlyUpdated(item) || isDueThisWeek(item);
    }

    /** Operational rule: governanceCheck13. */
    @Transactional(readOnly=true) public boolean governanceCheck13(AutomationRun item) {
        if (item == null) return false;
        return isTitleValid(item) && isStatusValid(item) && isDueDatePlausible(item);
    }

    /** Operational rule: governanceCheck14. */
    @Transactional(readOnly=true) public boolean governanceCheck14(AutomationRun item) {
        if (item == null) return false;
        return !isTerminal(item) && (isMissingEvidence(item) || isUnassigned(item));
    }

    /** Operational rule: governanceCheck15. */
    @Transactional(readOnly=true) public boolean governanceCheck15(AutomationRun item) {
        if (item == null) return false;
        return isAuditMetadataPresent(item) && isRevisionTracked(item);
    }

    /** Operational rule: governanceCheck16. */
    @Transactional(readOnly=true) public boolean governanceCheck16(AutomationRun item) {
        if (item == null) return false;
        return isEscalationCandidate(item) && !isTerminal(item);
    }

    /** Operational rule: governanceCheck17. */
    @Transactional(readOnly=true) public boolean governanceCheck17(AutomationRun item) {
        if (item == null) return false;
        return isReadyForReview(item) && recordCompletenessPercent(item) >= 80;
    }

    /** Operational rule: governanceCheck18. */
    @Transactional(readOnly=true) public boolean governanceCheck18(AutomationRun item) {
        if (item == null) return false;
        return isRecentlyCreated(item) || isRecentlyUpdated(item) || isDueThisWeek(item);
    }

    /** Operational rule: governanceCheck19. */
    @Transactional(readOnly=true) public boolean governanceCheck19(AutomationRun item) {
        if (item == null) return false;
        return isTitleValid(item) && isStatusValid(item) && isDueDatePlausible(item);
    }

    /** Operational rule: governanceCheck20. */
    @Transactional(readOnly=true) public boolean governanceCheck20(AutomationRun item) {
        if (item == null) return false;
        return !isTerminal(item) && (isMissingEvidence(item) || isUnassigned(item));
    }

    /** Operational rule: governanceCheck21. */
    @Transactional(readOnly=true) public boolean governanceCheck21(AutomationRun item) {
        if (item == null) return false;
        return isAuditMetadataPresent(item) && isRevisionTracked(item);
    }

    /** Operational rule: governanceCheck22. */
    @Transactional(readOnly=true) public boolean governanceCheck22(AutomationRun item) {
        if (item == null) return false;
        return isEscalationCandidate(item) && !isTerminal(item);
    }

    /** Operational rule: governanceCheck23. */
    @Transactional(readOnly=true) public boolean governanceCheck23(AutomationRun item) {
        if (item == null) return false;
        return isReadyForReview(item) && recordCompletenessPercent(item) >= 80;
    }

    /** Operational rule: governanceCheck24. */
    @Transactional(readOnly=true) public boolean governanceCheck24(AutomationRun item) {
        if (item == null) return false;
        return isRecentlyCreated(item) || isRecentlyUpdated(item) || isDueThisWeek(item);
    }

    /** Operational rule: governanceCheck25. */
    @Transactional(readOnly=true) public boolean governanceCheck25(AutomationRun item) {
        if (item == null) return false;
        return isTitleValid(item) && isStatusValid(item) && isDueDatePlausible(item);
    }

    /** Operational rule: governanceCheck26. */
    @Transactional(readOnly=true) public boolean governanceCheck26(AutomationRun item) {
        if (item == null) return false;
        return !isTerminal(item) && (isMissingEvidence(item) || isUnassigned(item));
    }

    /** Operational rule: governanceCheck27. */
    @Transactional(readOnly=true) public boolean governanceCheck27(AutomationRun item) {
        if (item == null) return false;
        return isAuditMetadataPresent(item) && isRevisionTracked(item);
    }

    /** Operational rule: governanceCheck28. */
    @Transactional(readOnly=true) public boolean governanceCheck28(AutomationRun item) {
        if (item == null) return false;
        return isEscalationCandidate(item) && !isTerminal(item);
    }

    /** Operational rule: governanceCheck29. */
    @Transactional(readOnly=true) public boolean governanceCheck29(AutomationRun item) {
        if (item == null) return false;
        return isReadyForReview(item) && recordCompletenessPercent(item) >= 80;
    }

    /** Operational rule: governanceCheck30. */
    @Transactional(readOnly=true) public boolean governanceCheck30(AutomationRun item) {
        if (item == null) return false;
        return isRecentlyCreated(item) || isRecentlyUpdated(item) || isDueThisWeek(item);
    }


    @Transactional(readOnly=true) public Map<String,Object> governanceReport() {
        List<AutomationRun> records = list();
        Map<String,Object> report = new LinkedHashMap<>();
        report.put("total", records.size());
        report.put("overdue", records.stream().filter(this::isOverdue).count());
        report.put("dueToday", records.stream().filter(this::isDueToday).count());
        report.put("dueThisWeek", records.stream().filter(this::isDueThisWeek).count());
        report.put("unassigned", records.stream().filter(this::isUnassigned).count());
        report.put("missingEvidence", records.stream().filter(this::isMissingEvidence).count());
        report.put("highRisk", records.stream().filter(this::isHighRisk).count());
        report.put("escalationCandidates", records.stream().filter(this::isEscalationCandidate).count());
        report.put("averageCompleteness", records.stream().mapToLong(this::recordCompletenessPercent).average().orElse(0));
        report.put("riskDistribution", records.stream().collect(Collectors.groupingBy(this::riskBand, TreeMap::new, Collectors.counting())));
        report.put("slaDistribution", records.stream().collect(Collectors.groupingBy(this::slaState, TreeMap::new, Collectors.counting())));
        report.put("lifecycleDistribution", records.stream().collect(Collectors.groupingBy(this::lifecycleLabel, TreeMap::new, Collectors.counting())));
        return report;
    }

    @Transactional(readOnly=true) public List<Map<String,Object>> riskRegister() {
        return list().stream().filter(this::isEscalationCandidate)
            .sorted(Comparator.comparingLong(this::riskScore).reversed().thenComparing(AutomationRun::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(item -> {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("id", item.getId()); row.put("title", item.getTitle()); row.put("reference", safeReferenceLabel(item));
                row.put("riskScore", riskScore(item)); row.put("riskBand", riskBand(item)); row.put("slaState", slaState(item));
                row.put("nextAction", recommendedNextAction(item)); row.put("owner", ownershipLabel(item)); row.put("dueDate", item.getDueDate());
                return row;
            }).collect(Collectors.toList());
    }

    @Transactional(readOnly=true) public List<AutomationRun> findNeedingAttention() {
        return list().stream().filter(this::needsManagerAttention)
            .sorted(Comparator.comparingLong(this::riskScore).reversed()).collect(Collectors.toList());
    }

    @Transactional(readOnly=true) public List<AutomationRun> findReadyForReview() {
        return list().stream().filter(this::isReadyForReview).filter(x -> !isTerminal(x)).collect(Collectors.toList());
    }

    @Transactional(readOnly=true) public List<AutomationRun> findStaleRecords() {
        return list().stream().filter(this::isStale).sorted(Comparator.comparing(AutomationRun::getUpdatedAt, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
    }


    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findUnassignedRecords() {
        return list().stream().filter(this::isUnassigned).filter(x -> !isTerminal(x)).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findOverdueRecords() {
        return list().stream().filter(this::isOverdue).sorted(Comparator.comparing(AutomationRun::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findDueTodayRecords() {
        return list().stream().filter(this::isDueToday).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findDueThisWeekRecords() {
        return list().stream().filter(this::isDueThisWeek).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findHighRiskRecords() {
        return list().stream().filter(this::isHighRisk).sorted(Comparator.comparingLong(this::riskScore).reversed()).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecordsMissingEvidence() {
        return list().stream().filter(this::isMissingEvidence).filter(x -> !isTerminal(x)).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecordsMissingReferences() {
        return list().stream().filter(this::isMissingReference).filter(x -> !isTerminal(x)).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecentlyCreatedRecords() {
        return list().stream().filter(this::isRecentlyCreated).sorted(Comparator.comparing(AutomationRun::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecentlyUpdatedRecords() {
        return list().stream().filter(this::isRecentlyUpdated).sorted(Comparator.comparing(AutomationRun::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecordsReadyToClose() {
        return list().stream().filter(this::isReadyToClose).filter(x -> !isTerminal(x)).collect(Collectors.toList());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countUnassignedRecords() {
        return list().stream().filter(this::isUnassigned).filter(x -> !isTerminal(x)).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countMissingEvidenceRecords() {
        return list().stream().filter(this::isMissingEvidence).filter(x -> !isTerminal(x)).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countHighRiskRecords() {
        return list().stream().filter(this::isHighRisk).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countReadyForReviewRecords() {
        return list().stream().filter(this::isReadyForReview).filter(x -> !isTerminal(x)).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countStaleRecords() {
        return list().stream().filter(this::isStale).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countDueTodayRecords() {
        return list().stream().filter(this::isDueToday).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countDueThisWeekRecords() {
        return list().stream().filter(this::isDueThisWeek).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countEscalationCandidates() {
        return list().stream().filter(this::isEscalationCandidate).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public long countRecentlyCreatedRecords() {
        return list().stream().filter(this::isRecentlyCreated).count();
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> priorityDistribution() {
        return list().stream().collect(Collectors.groupingBy(x -> safeLabel(x.getPriority()), TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> riskBandDistribution() {
        return list().stream().collect(Collectors.groupingBy(this::riskBand, TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> slaDistribution() {
        return list().stream().collect(Collectors.groupingBy(this::slaState, TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> lifecycleDistribution() {
        return list().stream().collect(Collectors.groupingBy(this::lifecycleLabel, TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> ageBandDistribution() {
        return list().stream().collect(Collectors.groupingBy(this::ageBand, TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public double averageRecordCompleteness() {
        return list().stream().mapToLong(this::recordCompletenessPercent).average().orElse(0);
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public double averageRiskScore() {
        return list().stream().mapToLong(this::riskScore).average().orElse(0);
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Object> riskScoreRange() {
        long min = list().stream().mapToLong(this::riskScore).min().orElse(0);
        long max = list().stream().mapToLong(this::riskScore).max().orElse(0);
        return Map.of("minimum", min, "maximum", max, "average", averageRiskScore());
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> ownerWorkload() {
        return list().stream().filter(x -> !isTerminal(x)).collect(Collectors.groupingBy(this::ownershipLabel, TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> departmentWorkload() {
        return list().stream().filter(x -> !isTerminal(x)).collect(Collectors.groupingBy(x -> safeLabel(x.getDepartment()), TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Long> priorityWorkload() {
        return list().stream().filter(x -> !isTerminal(x)).collect(Collectors.groupingBy(x -> safeLabel(x.getPriority()), TreeMap::new, Collectors.counting()));
    }

    /** Query and reporting operation used by the operational integration hub. */
    @Transactional(readOnly=true) public Map<String,Object> buildQualityGateReport() {
        Map<String,Object> gate = new LinkedHashMap<>();
        gate.put("validTitles", list().stream().filter(this::isTitleValid).count());
        gate.put("validStatuses", list().stream().filter(this::isStatusValid).count());
        gate.put("validDates", list().stream().filter(this::isDueDatePlausible).count());
        gate.put("completeAuditMetadata", list().stream().filter(this::isAuditMetadataPresent).count());
        gate.put("recordsNeedingAttention", countEscalationCandidates());
        gate.put("readyForReview", countReadyForReviewRecords());
        gate.put("averageCompleteness", averageRecordCompleteness());
        gate.put("averageRiskScore", averageRiskScore());
        return gate;
    }


    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public List<AutomationRun> findUrgentRecords() {
        return list().stream().filter(this::isUrgent).filter(x -> !isTerminal(x)).sorted(Comparator.comparingLong(this::riskScore).reversed()).collect(Collectors.toList());
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecordsWithIncompleteMetadata() {
        return list().stream().filter(x -> !isTitleValid(x) || !isStatusValid(x) || !isAuditMetadataPresent(x)).collect(Collectors.toList());
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecordsNeedingOwnershipReview() {
        return list().stream().filter(this::needsOwnershipReview).filter(x -> !isTerminal(x)).collect(Collectors.toList());
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public List<AutomationRun> findRecordsNeedingEvidenceReview() {
        return list().stream().filter(this::needsEvidenceReview).collect(Collectors.toList());
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public List<AutomationRun> findManagerAttentionQueue() {
        return list().stream().filter(this::needsManagerAttention).sorted(Comparator.comparingLong(this::riskScore).reversed().thenComparing(AutomationRun::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public long countIncompleteMetadata() {
        return list().stream().filter(x -> !isTitleValid(x) || !isStatusValid(x) || !isAuditMetadataPresent(x)).count();
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public long countTerminalRecords() {
        return list().stream().filter(this::isTerminal).count();
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public long countActionableRecords() {
        return list().stream().filter(this::isActionable).count();
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public long countRecordsNeedingOwnershipReview() {
        return list().stream().filter(this::needsOwnershipReview).filter(x -> !isTerminal(x)).count();
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public long countRecordsNeedingEvidenceReview() {
        return list().stream().filter(this::needsEvidenceReview).count();
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public Map<String,Long> createdByDistribution() {
        return list().stream().collect(Collectors.groupingBy(x -> safeLabel(x.getCreatedBy()), TreeMap::new, Collectors.counting()));
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public Map<String,Long> updatedByDistribution() {
        return list().stream().collect(Collectors.groupingBy(x -> safeLabel(x.getUpdatedBy()), TreeMap::new, Collectors.counting()));
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public Map<String,Object> buildExecutiveSummary() {
        Map<String,Object> report = new LinkedHashMap<>();
        report.put("records", list().size()); report.put("open", countStatus("OPEN"));
        report.put("terminal", countTerminalRecords()); report.put("overdue", overdueCount());
        report.put("highRisk", countHighRiskRecords()); report.put("urgent", list().stream().filter(this::isUrgent).count());
        report.put("unassigned", countUnassignedRecords()); report.put("evidenceGaps", countMissingEvidenceRecords());
        report.put("managerQueue", countEscalationCandidates()); report.put("dataQualityGate", buildQualityGateReport());
        return report;
    }

    /** Executive reporting query for cross-team operational visibility. */
    @Transactional(readOnly=true) public Map<String,Object> buildWorkloadReport() {
        Map<String,Object> report = new LinkedHashMap<>();
        report.put("byOwner", ownerWorkload()); report.put("byDepartment", departmentWorkload());
        report.put("byPriority", priorityWorkload()); report.put("byLifecycle", lifecycleDistribution());
        report.put("byAge", ageBandDistribution()); report.put("byRisk", riskBandDistribution());
        report.put("bySla", slaDistribution()); return report;
    }


    /** Returns a bounded, deterministic set of report rows suitable for a review meeting. */
    @Transactional(readOnly=true) public List<Map<String,Object>> buildReviewMeetingAgenda() {
        return list().stream().filter(this::needsManagerAttention)
            .sorted(Comparator.comparingLong(this::riskScore).reversed())
            .limit(25)
            .map(item -> {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("id", item.getId());
                row.put("title", item.getTitle());
                row.put("owner", ownershipLabel(item));
                row.put("risk", riskBand(item));
                row.put("score", riskScore(item));
                row.put("nextAction", recommendedNextAction(item));
                row.put("dueDate", item.getDueDate());
                return row;
            }).collect(Collectors.toList());
    }

    /** Validates that a record can be represented safely in an operational report. */
    @Transactional(readOnly=true) public Map<String,Object> validateForOperationalReport(AutomationRun item) {
        Map<String,Object> validation = new LinkedHashMap<>();
        validation.put("validTitle", isTitleValid(item));
        validation.put("validStatus", isStatusValid(item));
        validation.put("validDueDate", isDueDatePlausible(item));
        validation.put("validReference", isReferenceValid(item));
        validation.put("validEvidenceReference", isEvidenceReferenceValid(item));
        validation.put("auditMetadataPresent", isAuditMetadataPresent(item));
        validation.put("revisionTracked", isRevisionTracked(item));
        validation.put("completePercent", recordCompletenessPercent(item));
        validation.put("riskBand", riskBand(item));
        validation.put("reportStatus", statusForReport(item));
        validation.put("ready", isTitleValid(item) && isStatusValid(item) && isDueDatePlausible(item));
        return validation;
    }
}
