package com.healthcare.hospital.enterprise.quality.service;

import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.entity.QualityAction;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Set;

/** Central policy rules used by service operations and exposed to the UI as explanations. */
@Component
public class QualityWorkflowPolicy {
    public QualityWorkflowPolicy() { }
    public static final Set<String> CATEGORIES = Set.of(
        "MEDICATION", "PATIENT_FALL", "INFECTION_CONTROL", "IDENTIFICATION",
        "DOCUMENTATION", "EQUIPMENT", "PRIVACY", "DELAY_IN_CARE", "SPECIMEN",
        "COMMUNICATION", "ENVIRONMENT", "NEAR_MISS", "OTHER");
    public static final Set<String> SEVERITIES = Set.of("LOW", "MODERATE", "HIGH", "CRITICAL");
    public static final Set<String> REVIEW_STATES = Set.of(
        "DRAFT", "SUBMITTED", "TRIAGED", "INVESTIGATING", "ACTION_REQUIRED",
        "PENDING_VERIFICATION", "CLOSED", "REOPENED", "CANCELLED");
    public static final Set<String> ACTION_TYPES = Set.of("CORRECTIVE", "PREVENTIVE", "CONTAINMENT", "TRAINING", "AUDIT");
    public static final Set<String> ACTION_PRIORITIES = Set.of("LOW", "NORMAL", "HIGH", "URGENT");
    public static final Set<String> ACTION_STATES = Set.of("OPEN", "IN_PROGRESS", "BLOCKED", "COMPLETED", "VERIFIED", "CANCELLED");

    public String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(java.util.Locale.ROOT);
    }

    public void validateCategory(String category) {
        if (!CATEGORIES.contains(normalize(category))) throw new IllegalArgumentException("Choose a supported quality category.");
    }

    public void validateSeverity(String severity) {
        if (!SEVERITIES.contains(normalize(severity))) throw new IllegalArgumentException("Choose a supported severity.");
    }

    public void validateReviewState(String state) {
        if (!REVIEW_STATES.contains(normalize(state))) throw new IllegalArgumentException("Unsupported review state.");
    }

    public void validateActionType(String type) {
        if (!ACTION_TYPES.contains(normalize(type))) throw new IllegalArgumentException("Choose a supported action type.");
    }

    public void validateActionPriority(String priority) {
        if (!ACTION_PRIORITIES.contains(normalize(priority))) throw new IllegalArgumentException("Choose a supported action priority.");
    }

    public void validateActionState(String state) {
        if (!ACTION_STATES.contains(normalize(state))) throw new IllegalArgumentException("Unsupported action state.");
    }

    public void validateDueDate(LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDate.now().minusDays(1))) {
            throw new IllegalArgumentException("A new due date cannot be set in the past.");
        }
    }

    public void validateCreate(QualityReview review) {
        if (review.getTitle() == null || review.getTitle().isBlank()) throw new IllegalArgumentException("A case title is required.");
        if (review.getEventDescription() == null || review.getEventDescription().isBlank()) throw new IllegalArgumentException("Describe the event before saving.");
        if (review.isSentinelEvent() && !review.isPatientSafetyImpact()) throw new IllegalArgumentException("A sentinel event must be marked as having patient-safety impact.");
        if ((review.isRequiresDisclosure() || review.isRegulatoryNotification()) && !review.isPatientSafetyImpact()) {
            throw new IllegalArgumentException("Disclosure or regulatory review requires a recorded patient-safety impact assessment.");
        }
        if (review.isPrivacySensitive() && "OTHER".equals(review.getCategory())) {
            throw new IllegalArgumentException("Privacy-sensitive cases must use a specific category.");
        }
        validateCategory(review.getCategory());
        validateSeverity(review.getSeverity());
        validateDueDate(review.getDueDate());
    }

    public void requireTransition(QualityReview review, String target) {
        String from=normalize(review.getStatus());
        String to=normalize(target);
        boolean allowed = switch (from) {
            case "DRAFT" -> Set.of("SUBMITTED", "CANCELLED").contains(to);
            case "SUBMITTED" -> Set.of("TRIAGED", "CANCELLED").contains(to);
            case "TRIAGED" -> Set.of("INVESTIGATING", "ACTION_REQUIRED", "CANCELLED").contains(to);
            case "INVESTIGATING" -> Set.of("ACTION_REQUIRED", "PENDING_VERIFICATION").contains(to);
            case "ACTION_REQUIRED" -> Set.of("INVESTIGATING", "PENDING_VERIFICATION").contains(to);
            case "PENDING_VERIFICATION" -> Set.of("CLOSED", "INVESTIGATING", "ACTION_REQUIRED").contains(to);
            case "CLOSED" -> Set.of("REOPENED").contains(to);
            case "REOPENED" -> Set.of("TRIAGED", "INVESTIGATING", "CANCELLED").contains(to);
            case "CANCELLED" -> false;
            default -> false;
        };
        if (!allowed) throw new IllegalStateException("Transition from " + from + " to " + to + " is not allowed.");
    }

    public void requireReadyForClosure(QualityReview review, long openActions) {
        if (openActions > 0) throw new IllegalStateException("Close all quality actions before closing this case.");
        if (review.getRootCause() == null || review.getRootCause().isBlank()) throw new IllegalStateException("Record the root-cause analysis before closure.");
        if (review.getCorrectivePlan() == null || review.getCorrectivePlan().isBlank()) throw new IllegalStateException("Record corrective action or explain why none is required.");
        if (review.getClosureSummary() == null || review.getClosureSummary().isBlank()) throw new IllegalStateException("A closure summary is required.");
        if (!review.isEvidencePreserved()) throw new IllegalStateException("Confirm that relevant evidence is preserved before closure.");
        if (review.isSentinelEvent() && (review.getReviewerUsername() == null || review.getReviewerUsername().isBlank())) {
            throw new IllegalStateException("Sentinel events require a named independent reviewer.");
        }
    }

    public void requireActionTransition(QualityAction action, String target) {
        String from=normalize(action.getStatus());
        String to=normalize(target);
        boolean allowed = switch (from) {
            case "OPEN" -> Set.of("IN_PROGRESS", "BLOCKED", "CANCELLED").contains(to);
            case "IN_PROGRESS" -> Set.of("BLOCKED", "COMPLETED", "CANCELLED").contains(to);
            case "BLOCKED" -> Set.of("IN_PROGRESS", "CANCELLED").contains(to);
            case "COMPLETED" -> Set.of("VERIFIED", "IN_PROGRESS").contains(to);
            case "VERIFIED", "CANCELLED" -> false;
            default -> false;
        };
        if (!allowed) throw new IllegalStateException("Action transition from " + from + " to " + to + " is not allowed.");
    }

    public String severityGuidance(String severity) {
        return switch (normalize(severity)) {
            case "CRITICAL" -> "Escalate immediately to the designated clinical leader and follow local emergency and reporting procedures.";
            case "HIGH" -> "Prioritize triage today; assign an accountable owner and review progress at each shift handover.";
            case "MODERATE" -> "Assign an owner and a due date; verify corrective actions before closure.";
            default -> "Record the learning, monitor for recurrence, and close only after required evidence is reviewed.";
        };
    }

    public boolean overdue(QualityReview review) {
        return review.getDueDate()!=null && review.getDueDate().isBefore(LocalDate.now())
            && !Set.of("CLOSED","CANCELLED").contains(normalize(review.getStatus()));
    }

    public boolean overdue(QualityAction action) {
        return action.getDueDate()!=null && action.getDueDate().isBefore(LocalDate.now())
            && !Set.of("VERIFIED","CANCELLED").contains(normalize(action.getStatus()));
    }
}
