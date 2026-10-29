package com.healthcare.hospital.enterprise.quality.service;

import com.healthcare.hospital.enterprise.quality.dto.CreateQualityActionRequest;
import com.healthcare.hospital.enterprise.quality.dto.QualityActionUpdateRequest;
import com.healthcare.hospital.enterprise.quality.entity.QualityAction;
import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.repository.QualityActionRepository;
import com.healthcare.hospital.enterprise.quality.repository.QualityReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

/** Owns action assignment, evidence capture, blockers, verification and case linkage. */
@Service
@Transactional
public class QualityActionService {
    private static final List<String> CLOSED_STATES=List.of("VERIFIED","CANCELLED");
    private final QualityActionRepository actions;
    private final QualityReviewRepository reviews;
    private final QualityWorkflowPolicy policy;
    private final QualityAuditService audit;

    public QualityActionService(QualityActionRepository actions, QualityReviewRepository reviews,
                                QualityWorkflowPolicy policy, QualityAuditService audit) {
        this.actions=actions;
        this.reviews=reviews;
        this.policy=policy;
        this.audit=audit;
    }

    public QualityAction create(long reviewId, CreateQualityActionRequest request, String actor) {
        QualityReview review=requireReview(reviewId);
        if (List.of("CLOSED","CANCELLED").contains(policy.normalize(review.getStatus()))) {
            throw new IllegalStateException("Actions cannot be added to a closed or cancelled case.");
        }
        policy.validateActionType(request.getActionType());
        policy.validateActionPriority(request.getPriority());
        policy.validateDueDate(request.getDueDate());
        QualityAction action=new QualityAction();
        action.setReview(review);
        action.setActionReference("QA-" + UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT));
        action.setTitle(required(request.getTitle(),"Action title",180));
        action.setDescription(required(request.getDescription(),"Action description",4000));
        action.setActionType(policy.normalize(request.getActionType()));
        action.setPriority(policy.normalize(request.getPriority()));
        action.setOwnerUsername(required(request.getOwnerUsername(),"Action owner",100));
        action.setDueDate(request.getDueDate());
        action.setCreatedBy(required(actor,"Actor",100));
        action.setStatus("OPEN");
        QualityAction saved=actions.save(action);
        audit.record(review.getCaseReference(),"ACTION_CREATED",actor,null,"OPEN","Action " + saved.getActionReference() + " created.");
        if (!List.of("ACTION_REQUIRED","INVESTIGATING","REOPENED").contains(policy.normalize(review.getStatus()))) {
            String previous=review.getStatus();
            review.setStatus("ACTION_REQUIRED");
            reviews.save(review);
            audit.record(review.getCaseReference(),"CASE_ACTION_REQUIRED",actor,previous,"ACTION_REQUIRED","A corrective or preventive action was created.");
        }
        return saved;
    }

    public QualityAction update(long id, QualityActionUpdateRequest request, String actor) {
        QualityAction action=requireAction(id);
        if (List.of("VERIFIED","CANCELLED").contains(policy.normalize(action.getStatus()))) {
            throw new IllegalStateException("Verified or cancelled actions are immutable.");
        }
        if (request.getOwnerUsername()!=null) action.setOwnerUsername(required(request.getOwnerUsername(),"Owner",100));
        if (request.getDescription()!=null) action.setDescription(required(request.getDescription(),"Description",4000));
        if (request.getDueDate()!=null) {
            policy.validateDueDate(request.getDueDate());
            action.setDueDate(request.getDueDate());
        }
        if (request.getBlockedReason()!=null) action.setBlockedReason(optional(request.getBlockedReason(),1500));
        if (request.getCompletionEvidence()!=null) action.setCompletionEvidence(optional(request.getCompletionEvidence(),4000));
        if (request.getVerificationNotes()!=null) action.setVerificationNotes(optional(request.getVerificationNotes(),2500));
        QualityAction saved=actions.save(action);
        audit.record(action.getReview().getCaseReference(),"ACTION_UPDATED",actor,action.getStatus(),action.getStatus(),"Action details updated.");
        return saved;
    }

    public QualityAction start(long id, String actor) {
        return transition(id,"IN_PROGRESS",actor,"Work started.");
    }

    public QualityAction block(long id, String reason, String actor) {
        QualityAction action=requireAction(id);
        action.setBlockedReason(required(reason,"Blocker reason",1500));
        return transitionLoaded(action,"BLOCKED",actor,"Action blocked: " + action.getBlockedReason());
    }

    public QualityAction unblock(long id, String actor) {
        QualityAction action=requireAction(id);
        action.setBlockedReason(null);
        return transitionLoaded(action,"IN_PROGRESS",actor,"Action unblocked and returned to progress.");
    }

    public QualityAction submitForVerification(long id, String evidence, String actor) {
        QualityAction action=requireAction(id);
        action.setCompletionEvidence(required(evidence,"Completion evidence",4000));
        QualityAction saved=transitionLoaded(action,"COMPLETED",actor,"Completion evidence submitted for verification.");
        saved.setCompletedAt(LocalDateTime.now());
        return actions.save(saved);
    }

    public QualityAction verify(long id, String notes, String actor) {
        QualityAction action=requireAction(id);
        if (action.getCompletionEvidence()==null || action.getCompletionEvidence().isBlank()) {
            throw new IllegalStateException("Completion evidence must be recorded before verification.");
        }
        action.setVerificationNotes(required(notes,"Verification notes",2500));
        QualityAction saved=transitionLoaded(action,"VERIFIED",actor,"Action independently verified.");
        saved.setVerifiedAt(LocalDateTime.now());
        saved.setVerifiedBy(required(actor,"Verifier",100));
        return actions.save(saved);
    }

    public QualityAction cancel(long id, String reason, String actor) {
        QualityAction action=requireAction(id);
        action.setBlockedReason(required(reason,"Cancellation reason",1500));
        return transitionLoaded(action,"CANCELLED",actor,"Action cancelled with a recorded reason.");
    }

    public QualityAction transition(long id, String target, String actor, String summary) {
        return transitionLoaded(requireAction(id),target,actor,summary);
    }

    private QualityAction transitionLoaded(QualityAction action, String target, String actor, String summary) {
        String from=action.getStatus();
        policy.requireActionTransition(action,target);
        action.setStatus(policy.normalize(target));
        QualityAction saved=actions.save(action);
        audit.record(action.getReview().getCaseReference(),"ACTION_STATE_CHANGED",actor,from,saved.getStatus(),summary);
        return saved;
    }

    @Transactional(readOnly=true)
    public List<QualityAction> forReview(long reviewId) {
        requireReview(reviewId);
        return actions.findByReviewIdOrderByCreatedAtDesc(reviewId);
    }

    @Transactional(readOnly=true)
    public List<QualityAction> myOpenActions(String username) {
        return actions.findByOwnerUsernameIgnoreCaseAndStatusNotInOrderByDueDateAsc(required(username,"Username",100),CLOSED_STATES);
    }

    @Transactional(readOnly=true)
    public List<QualityAction> overdueActions() {
        return actions.findByStatusAndDueDateBeforeOrderByDueDateAsc("OPEN",LocalDate.now());
    }

    @Transactional(readOnly=true)
    public long openActionCount(long reviewId) {
        return actions.countByReviewIdAndStatusNotIn(reviewId,CLOSED_STATES);
    }

    @Transactional(readOnly=true)
    public long countByStatus(String status) {
        policy.validateActionState(status);
        return actions.countByStatus(policy.normalize(status));
    }

    public QualityReview requireReview(long id) {
        return reviews.findById(id).orElseThrow(()->new NoSuchElementException("Quality review not found."));
    }

    public QualityAction requireAction(long id) {
        return actions.findById(id).orElseThrow(()->new NoSuchElementException("Quality action not found."));
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
}

