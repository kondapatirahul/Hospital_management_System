package com.healthcare.hospital.enterprise.quality.service;

import com.healthcare.hospital.enterprise.quality.dto.CreateQualityReviewRequest;
import com.healthcare.hospital.enterprise.quality.dto.QualityReviewUpdateRequest;
import com.healthcare.hospital.enterprise.quality.entity.QualityComment;
import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.repository.QualityActionRepository;
import com.healthcare.hospital.enterprise.quality.repository.QualityCommentRepository;
import com.healthcare.hospital.enterprise.quality.repository.QualityReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/** Transactional application service for clinical quality investigations. */
@Service
@Transactional
public class QualityReviewService {
    private static final List<String> TERMINAL=List.of("CLOSED","CANCELLED");
    private final QualityReviewRepository reviews;
    private final QualityActionRepository actions;
    private final QualityCommentRepository comments;
    private final QualityWorkflowPolicy policy;
    private final QualityAuditService audit;

    public QualityReviewService(QualityReviewRepository reviews, QualityActionRepository actions,
                                QualityCommentRepository comments, QualityWorkflowPolicy policy,
                                QualityAuditService audit) {
        this.reviews=reviews;
        this.actions=actions;
        this.comments=comments;
        this.policy=policy;
        this.audit=audit;
    }

    public QualityReview create(CreateQualityReviewRequest request, String actor) {
        QualityReview review=new QualityReview();
        review.setCaseReference(nextReference());
        review.setTitle(required(request.getTitle(),"Title",160));
        review.setCategory(policy.normalize(request.getCategory()));
        review.setSeverity(policy.normalize(request.getSeverity()));
        review.setStatus("DRAFT");
        review.setEventDescription(required(request.getEventDescription(),"Event description",5000));
        review.setPatientReference(optional(request.getPatientReference(),80));
        review.setPatientDisplayName(optional(request.getPatientDisplayName(),160));
        review.setLocation(optional(request.getLocation(),100));
        review.setDepartment(optional(request.getDepartment(),100));
        review.setOwnerUsername(optional(request.getOwnerUsername(),100));
        review.setReporterUsername(required(actor,"Reporter",100));
        review.setEventDate(request.getEventDate());
        review.setDueDate(request.getDueDate());
        review.setPatientSafetyImpact(request.isPatientSafetyImpact());
        review.setRequiresDisclosure(request.isRequiresDisclosure());
        review.setRegulatoryNotification(request.isRegulatoryNotification());
        review.setSentinelEvent(request.isSentinelEvent());
        review.setPrivacySensitive(request.isPrivacySensitive());
        review.setFollowUpRequired(request.isFollowUpRequired());
        review.setEvidencePreserved(request.isEvidencePreserved());
        policy.validateCreate(review);
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"CASE_CREATED",actor,null,"DRAFT","Quality case created.");
        return saved;
    }

    @Transactional(readOnly=true)
    public QualityReview get(long id) {
        return reviews.findById(id).orElseThrow(()->new NoSuchElementException("Quality case not found."));
    }

    @Transactional(readOnly=true)
    public QualityReview getByReference(String reference) {
        return reviews.findByCaseReferenceIgnoreCase(required(reference,"Case reference",32))
            .orElseThrow(()->new NoSuchElementException("Quality case not found."));
    }

    @Transactional(readOnly=true)
    public Page<QualityReview> search(String status,String severity,String category,String owner,
                                      LocalDate from,LocalDate to,String term,int page,int size) {
        if(from!=null&&to!=null&&from.isAfter(to)) throw new IllegalArgumentException("Start date must be on or before end date.");
        String normalizedStatus=filter(status,QualityWorkflowPolicy.REVIEW_STATES);
        String normalizedSeverity=filter(severity,QualityWorkflowPolicy.SEVERITIES);
        String normalizedCategory=filter(category,QualityWorkflowPolicy.CATEGORIES);
        String normalizedOwner=optional(owner,100);
        String normalizedTerm=optional(term,120);
        int safePage=Math.max(0,Math.min(page,10000));
        int safeSize=Math.max(10,Math.min(size,100));
        return reviews.search(normalizedStatus,normalizedSeverity,normalizedCategory,normalizedOwner,from,to,normalizedTerm,
            PageRequest.of(safePage,safeSize,Sort.by(Sort.Direction.DESC,"updatedAt")));
    }

    @Transactional(readOnly=true)
    public List<QualityReview> recent() {
        return reviews.findTop200ByOrderByUpdatedAtDesc();
    }

    @Transactional(readOnly=true)
    public List<QualityReview> assignedTo(String username) {
        return reviews.findByOwnerUsernameIgnoreCaseOrderByDueDateAsc(required(username,"Username",100));
    }

    @Transactional(readOnly=true)
    public List<QualityReview> patientHistory(String patientReference) {
        return reviews.findByPatientReferenceIgnoreCaseOrderByReportedAtDesc(required(patientReference,"Patient reference",80));
    }

    public QualityReview updateInvestigation(long id, QualityReviewUpdateRequest request, String actor) {
        QualityReview review=get(id);
        if(TERMINAL.contains(policy.normalize(review.getStatus()))) throw new IllegalStateException("Closed cases must be reopened before editing.");
        if(request.getImmediateContainment()!=null) review.setImmediateContainment(optional(request.getImmediateContainment(),5000));
        if(request.getContributingFactors()!=null) review.setContributingFactors(optional(request.getContributingFactors(),5000));
        if(request.getRootCause()!=null) review.setRootCause(optional(request.getRootCause(),5000));
        if(request.getCorrectivePlan()!=null) review.setCorrectivePlan(optional(request.getCorrectivePlan(),5000));
        if(request.getPreventionPlan()!=null) review.setPreventionPlan(optional(request.getPreventionPlan(),5000));
        if(request.getLearningSummary()!=null) review.setLearningSummary(optional(request.getLearningSummary(),5000));
        if(request.getOwnerUsername()!=null) review.setOwnerUsername(required(request.getOwnerUsername(),"Owner",100));
        if(request.getReviewerUsername()!=null) review.setReviewerUsername(optional(request.getReviewerUsername(),100));
        if(request.getClosureSummary()!=null) review.setClosureSummary(optional(request.getClosureSummary(),4000));
        if(request.getDueDate()!=null) {
            policy.validateDueDate(request.getDueDate());
            review.setDueDate(request.getDueDate());
        }
        review.setEvidencePreserved(request.isEvidencePreserved());
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"INVESTIGATION_UPDATED",actor,saved.getStatus(),saved.getStatus(),"Investigation fields updated.");
        return saved;
    }

    public QualityReview submit(long id,String actor) {
        QualityReview review=get(id);
        requireNonBlank(review.getTitle(),"Title");
        requireNonBlank(review.getEventDescription(),"Event description");
        policy.requireTransition(review,"SUBMITTED");
        String from=review.getStatus();
        review.setStatus("SUBMITTED");
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"CASE_SUBMITTED",actor,from,"SUBMITTED","Case submitted for triage.");
        return saved;
    }

    public QualityReview triage(long id,String owner,String reviewer,String actor) {
        QualityReview review=get(id);
        policy.requireTransition(review,"TRIAGED");
        review.setOwnerUsername(required(owner,"Assigned owner",100));
        review.setReviewerUsername(optional(reviewer,100));
        if(review.getDueDate()==null) review.setDueDate(defaultDueDate(review.getSeverity()));
        String from=review.getStatus();
        review.setStatus("TRIAGED");
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"CASE_TRIAGED",actor,from,"TRIAGED","Case owner and target date assigned.");
        return saved;
    }

    public QualityReview startInvestigation(long id,String actor) {
        QualityReview review=get(id);
        policy.requireTransition(review,"INVESTIGATING");
        if(review.getOwnerUsername()==null||review.getOwnerUsername().isBlank()) throw new IllegalStateException("Assign an accountable owner before investigation.");
        String from=review.getStatus();
        review.setStatus("INVESTIGATING");
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"INVESTIGATION_STARTED",actor,from,"INVESTIGATING","Investigation started.");
        return saved;
    }

    public QualityReview requestActions(long id,String actor) {
        QualityReview review=get(id);
        policy.requireTransition(review,"ACTION_REQUIRED");
        String from=review.getStatus();
        review.setStatus("ACTION_REQUIRED");
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"ACTIONS_REQUIRED",actor,from,"ACTION_REQUIRED","Case requires corrective or preventive actions.");
        return saved;
    }

    public QualityReview submitForVerification(long id,String actor) {
        QualityReview review=get(id);
        policy.requireTransition(review,"PENDING_VERIFICATION");
        long open=actions.countByReviewIdAndStatusNotIn(id,List.of("VERIFIED","CANCELLED"));
        if(open>0) throw new IllegalStateException("All corrective and preventive actions must be completed or cancelled before verification.");
        requireNonBlank(review.getRootCause(),"Root-cause analysis");
        requireNonBlank(review.getCorrectivePlan(),"Corrective plan");
        if(review.isSentinelEvent() && (review.getReviewerUsername()==null||review.getReviewerUsername().isBlank())) {
            throw new IllegalStateException("Assign an independent reviewer before verification.");
        }
        String from=review.getStatus();
        review.setStatus("PENDING_VERIFICATION");
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"VERIFICATION_REQUESTED",actor,from,"PENDING_VERIFICATION","Case submitted for independent verification.");
        return saved;
    }

    public QualityReview close(long id,String closureSummary,String actor) {
        QualityReview review=get(id);
        policy.requireTransition(review,"CLOSED");
        review.setClosureSummary(required(closureSummary,"Closure summary",4000));
        long open=actions.countByReviewIdAndStatusNotIn(id,List.of("VERIFIED","CANCELLED"));
        policy.requireReadyForClosure(review,open);
        String from=review.getStatus();
        review.setStatus("CLOSED");
        review.setClosedAt(LocalDateTime.now());
        review.setReviewerUsername(review.getReviewerUsername()==null?required(actor,"Reviewer",100):review.getReviewerUsername());
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"CASE_CLOSED",actor,from,"CLOSED","Case closed after required checks.");
        return saved;
    }

    public QualityReview reopen(long id,String reason,String actor) {
        QualityReview review=get(id);
        policy.requireTransition(review,"REOPENED");
        String closureReason=required(reason,"Reopen reason",1000);
        String from=review.getStatus();
        review.setStatus("REOPENED");
        review.setClosedAt(null);
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"CASE_REOPENED",actor,from,"REOPENED",closureReason);
        return saved;
    }

    public QualityReview cancel(long id,String reason,String actor) {
        QualityReview review=get(id);
        policy.requireTransition(review,"CANCELLED");
        String cancellation=required(reason,"Cancellation reason",1000);
        String from=review.getStatus();
        review.setStatus("CANCELLED");
        review.setClosureSummary(cancellation);
        review.setClosedAt(LocalDateTime.now());
        QualityReview saved=reviews.save(review);
        audit.record(saved.getCaseReference(),"CASE_CANCELLED",actor,from,"CANCELLED",cancellation);
        return saved;
    }

    public QualityComment addComment(long id,String body,String visibility,String type,boolean actionRequired,
                                     String assignedTo,LocalDate followUpDate,String actor) {
        QualityReview review=get(id);
        if(TERMINAL.contains(policy.normalize(review.getStatus()))) throw new IllegalStateException("Cannot comment on a terminal case.");
        String normalizedVisibility=policy.normalize(visibility);
        if(!Set.of("INTERNAL","RESTRICTED").contains(normalizedVisibility)) throw new IllegalArgumentException("Unsupported comment visibility.");
        String normalizedType=policy.normalize(type);
        if(!Set.of("NOTE","DECISION","FOLLOW_UP","RISK","LEARNING").contains(normalizedType)) throw new IllegalArgumentException("Unsupported comment type.");
        if(actionRequired && (assignedTo==null||assignedTo.isBlank())) throw new IllegalArgumentException("Assign a person when follow-up is required.");
        if(actionRequired && followUpDate==null) throw new IllegalArgumentException("Choose a follow-up date for an action-required comment.");
        QualityComment comment=new QualityComment();
        comment.setReview(review);
        comment.setAuthorUsername(required(actor,"Author",100));
        comment.setBody(required(body,"Comment",5000));
        comment.setVisibility(normalizedVisibility);
        comment.setCommentType(normalizedType);
        comment.setActionRequired(actionRequired);
        comment.setAssignedTo(optional(assignedTo,100));
        comment.setFollowUpDate(followUpDate);
        QualityComment saved=comments.save(comment);
        audit.record(review.getCaseReference(),"COMMENT_ADDED",actor,review.getStatus(),review.getStatus(),"A " + normalizedType.toLowerCase(Locale.ROOT) + " entry was added.");
        return saved;
    }

    @Transactional(readOnly=true)
    public List<QualityComment> comments(long id) {
        get(id);
        return comments.findByReviewIdOrderByCreatedAtDesc(id);
    }

    @Transactional(readOnly=true)
    public List<Map<String,Object>> dashboardMetrics() {
        List<QualityReview> all=reviews.findTop200ByOrderByUpdatedAtDesc();
        long overdue=all.stream().filter(policy::overdue).count();
        long critical=all.stream().filter(r->"CRITICAL".equals(policy.normalize(r.getSeverity()))&&!TERMINAL.contains(policy.normalize(r.getStatus()))).count();
        long active=all.stream().filter(r->!TERMINAL.contains(policy.normalize(r.getStatus()))).count();
        long waiting=all.stream().filter(r->"PENDING_VERIFICATION".equals(policy.normalize(r.getStatus()))).count();
        long safetyImpact=all.stream().filter(QualityReview::isPatientSafetyImpact).filter(r->!TERMINAL.contains(policy.normalize(r.getStatus()))).count();
        return List.of(metric("Active cases",active,"Cases not yet closed or cancelled"),
            metric("Overdue",overdue,"Open cases past their due date"),
            metric("Critical",critical,"Critical severity cases requiring prioritization"),
            metric("Awaiting verification",waiting,"Cases awaiting independent review"),
            metric("Safety impact",safetyImpact,"Open cases marked with patient-safety impact"));
    }

    @Transactional(readOnly=true)
    public Map<String,Object> summary() {
        List<QualityReview> all=reviews.findTop200ByOrderByUpdatedAtDesc();
        Map<String,Object> result=new LinkedHashMap<>();
        Map<String,Long> byStatus=new LinkedHashMap<>();
        Map<String,Long> bySeverity=new LinkedHashMap<>();
        Map<String,Long> byCategory=new LinkedHashMap<>();
        for(String state:QualityWorkflowPolicy.REVIEW_STATES) byStatus.put(state,all.stream().filter(r->state.equals(policy.normalize(r.getStatus()))).count());
        for(String severity:QualityWorkflowPolicy.SEVERITIES) bySeverity.put(severity,all.stream().filter(r->severity.equals(policy.normalize(r.getSeverity()))).count());
        for(String category:QualityWorkflowPolicy.CATEGORIES) byCategory.put(category,all.stream().filter(r->category.equals(policy.normalize(r.getCategory()))).count());
        result.put("byStatus",byStatus);
        result.put("bySeverity",bySeverity);
        result.put("byCategory",byCategory);
        result.put("overdue",all.stream().filter(policy::overdue).count());
        result.put("totalInRecentWindow",all.size());
        return result;
    }

    @Transactional(readOnly=true)
    public List<QualityReview> overdueCases() {
        return reviews.findTop200ByOrderByUpdatedAtDesc().stream().filter(policy::overdue).toList();
    }

    @Transactional(readOnly=true)
    public String csvExport(List<QualityReview> rows) {
        StringBuilder csv=new StringBuilder("Case Reference,Title,Category,Severity,Status,Patient Reference,Department,Owner,Reported At,Event Date,Due Date,Overdue\n");
        for(QualityReview review:rows) {
            appendCsv(csv,review.getCaseReference());
            appendCsv(csv,review.getTitle());
            appendCsv(csv,review.getCategory());
            appendCsv(csv,review.getSeverity());
            appendCsv(csv,review.getStatus());
            appendCsv(csv,review.getPatientReference());
            appendCsv(csv,review.getDepartment());
            appendCsv(csv,review.getOwnerUsername());
            appendCsv(csv,review.getReportedAt()==null?null:review.getReportedAt().toString());
            appendCsv(csv,review.getEventDate()==null?null:review.getEventDate().toString());
            appendCsv(csv,review.getDueDate()==null?null:review.getDueDate().toString());
            appendCsv(csv,Boolean.toString(policy.overdue(review)));
            csv.append('\n');
        }
        return csv.toString();
    }

    private Map<String,Object> metric(String label,long value,String detail) {
        Map<String,Object> item=new LinkedHashMap<>();
        item.put("label",label);
        item.put("value",value);
        item.put("detail",detail);
        return item;
    }

    private LocalDate defaultDueDate(String severity) {
        return switch(policy.normalize(severity)) {
            case "CRITICAL" -> LocalDate.now();
            case "HIGH" -> LocalDate.now().plusDays(1);
            case "MODERATE" -> LocalDate.now().plusDays(7);
            default -> LocalDate.now().plusDays(14);
        };
    }

    private String nextReference() {
        String prefix="QCR-"+LocalDate.now().toString().replace("-","");
        String candidate;
        int attempt=0;
        do {
            candidate=prefix+"-"+UUID.randomUUID().toString().substring(0,4).toUpperCase(Locale.ROOT);
            attempt++;
        } while(reviews.existsByCaseReferenceIgnoreCase(candidate)&&attempt<5);
        if(reviews.existsByCaseReferenceIgnoreCase(candidate)) throw new IllegalStateException("Unable to allocate a unique case reference. Please retry.");
        return candidate;
    }

    private String filter(String value,Set<String> allowed) {
        if(value==null||value.isBlank()) return null;
        String normalized=policy.normalize(value);
        if(!allowed.contains(normalized)) throw new IllegalArgumentException("Unsupported filter value: "+value);
        return normalized;
    }

    private String required(String value,String label,int max) {
        if(value==null||value.isBlank()) throw new IllegalArgumentException(label+" is required.");
        String v=value.trim();
        if(v.length()>max) throw new IllegalArgumentException(label+" exceeds "+max+" characters.");
        return v;
    }

    private String optional(String value,int max) {
        if(value==null||value.isBlank()) return null;
        String v=value.trim();
        if(v.length()>max) throw new IllegalArgumentException("Value exceeds "+max+" characters.");
        return v;
    }

    private void requireNonBlank(String value,String label) {
        if(value==null||value.isBlank()) throw new IllegalStateException(label+" is required before this workflow step.");
    }

    private void appendCsv(StringBuilder csv,String value) {
        String safe=value==null?"":value;
        csv.append('"').append(safe.replace("\"", "\"\"")).append('"').append(',');
    }
}
