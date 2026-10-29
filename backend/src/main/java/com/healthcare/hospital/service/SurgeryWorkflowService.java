package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.ChecklistItemUpdateDto;
import com.healthcare.hospital.dto.SurgeryStatusUpdateDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.ChecklistPhase;
import com.healthcare.hospital.enums.ChecklistStatus;
import com.healthcare.hospital.enums.SurgeryStatus;
import com.healthcare.hospital.enums.TheatreStatus;
import com.healthcare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SurgeryWorkflowService {

    private static final Map<SurgeryStatus, Set<SurgeryStatus>> ALLOWED_TRANSITIONS = Map.of(
            SurgeryStatus.SCHEDULED, Set.of(SurgeryStatus.PREPARATION, SurgeryStatus.CANCELLED, SurgeryStatus.POSTPONED),
            SurgeryStatus.PREPARATION, Set.of(SurgeryStatus.IN_PROGRESS, SurgeryStatus.SCHEDULED, SurgeryStatus.CANCELLED, SurgeryStatus.POSTPONED),
            SurgeryStatus.IN_PROGRESS, Set.of(SurgeryStatus.COMPLETED, SurgeryStatus.POSTPONED),
            SurgeryStatus.POSTPONED, Set.of(SurgeryStatus.SCHEDULED, SurgeryStatus.CANCELLED),
            SurgeryStatus.COMPLETED, Set.of(),
            SurgeryStatus.CANCELLED, Set.of()
    );

    private final SurgeryBookingRepository bookingRepository;
    private final SurgeryChecklistItemRepository checklistRepository;
    private final SurgeryAuditTrailRepository auditTrailRepository;
    private final OperationTheatreRepository theatreRepository;
    private final AuditEventService auditEventService;

    public SurgeryWorkflowService(SurgeryBookingRepository bookingRepository,
                                  SurgeryChecklistItemRepository checklistRepository,
                                  SurgeryAuditTrailRepository auditTrailRepository,
                                  OperationTheatreRepository theatreRepository,
                                  AuditEventService auditEventService) {
        this.bookingRepository = bookingRepository;
        this.checklistRepository = checklistRepository;
        this.auditTrailRepository = auditTrailRepository;
        this.theatreRepository = theatreRepository;
        this.auditEventService = auditEventService;
    }

    public List<SurgeryChecklistItem> getChecklistItems(SurgeryBooking booking) {
        return checklistRepository.findBySurgeryBookingOrderByPhaseAscIdAsc(booking);
    }

    public List<SurgeryChecklistItem> getChecklistForPhase(SurgeryBooking booking, ChecklistPhase phase) {
        return checklistRepository.findBySurgeryBookingAndPhaseOrderByIdAsc(booking, phase);
    }

    public List<SurgeryAuditTrail> getAuditHistory(SurgeryBooking booking) {
        return auditTrailRepository.findBySurgeryBookingOrderByTimestampDesc(booking);
    }

    /**
     * Initializes default checklist items across Preop, WHO phases and Postop Handover.
     */
    @Transactional
    public void initializeChecklistForSurgery(SurgeryBooking booking) {
        // 1. Preoperative Checklist
        addChecklistDef(booking, ChecklistPhase.PREOPERATIVE, "PREOP_ID", "Patient identity verified using two active identifiers", true);
        addChecklistDef(booking, ChecklistPhase.PREOPERATIVE, "PREOP_CONSENT", "Informed surgical and anesthesia consent signed and verified in chart", true);
        addChecklistDef(booking, ChecklistPhase.PREOPERATIVE, "PREOP_SITE", "Surgical site confirmed and marked by surgeon", true);
        addChecklistDef(booking, ChecklistPhase.PREOPERATIVE, "PREOP_ALLERGIES", "Drug, latex, and antiseptic allergies checked and documented", true);
        addChecklistDef(booking, ChecklistPhase.PREOPERATIVE, "PREOP_NPO", "NPO / fasting guidelines confirmed (solid foods & clear fluids)", true);
        addChecklistDef(booking, ChecklistPhase.PREOPERATIVE, "PREOP_LABS", "Required pre-op lab investigations and blood cross-match available", true);
        addChecklistDef(booking, ChecklistPhase.PREOPERATIVE, "PREOP_MEDS", "Pre-operative medications administered per doctor orders", false);

        // 2. WHO Sign-In (Before induction of anesthesia)
        addChecklistDef(booking, ChecklistPhase.SIGN_IN, "SIGNIN_CONFIRM", "Patient confirmed identity, surgical site, procedure and consent", true);
        addChecklistDef(booking, ChecklistPhase.SIGN_IN, "SIGNIN_SITE_MARKED", "Surgical site marked by operating team", true);
        addChecklistDef(booking, ChecklistPhase.SIGN_IN, "SIGNIN_ANESTHESIA_CHECK", "Anesthesia safety checklist and medication readiness confirmed", true);
        addChecklistDef(booking, ChecklistPhase.SIGN_IN, "SIGNIN_PULSE_OXIMETER", "Pulse oximeter connected and functioning continuously", true);
        addChecklistDef(booking, ChecklistPhase.SIGN_IN, "SIGNIN_AIRWAY_EVAL", "Difficult airway / aspiration risk evaluated and equipment ready", true);

        // 3. WHO Time-Out (Before skin incision)
        addChecklistDef(booking, ChecklistPhase.TIME_OUT, "TIMEOUT_TEAM_INTRO", "All team members introduced by name and clinical role", true);
        addChecklistDef(booking, ChecklistPhase.TIME_OUT, "TIMEOUT_CONFIRM_SURGERY", "Surgeon, anesthesiologist and nurse confirm patient, site and procedure", true);
        addChecklistDef(booking, ChecklistPhase.TIME_OUT, "TIMEOUT_ANTICIPATED_RISKS", "Surgeon reviews critical steps, expected duration, and blood loss", true);
        addChecklistDef(booking, ChecklistPhase.TIME_OUT, "TIMEOUT_ANTIBIOTIC", "Prophylactic antibiotics administered within 60 minutes prior", true);
        addChecklistDef(booking, ChecklistPhase.TIME_OUT, "TIMEOUT_STERILITY", "Sterility and indicator strips confirmed by scrub nurse", true);

        // 4. WHO Sign-Out (Before patient leaves operating room)
        addChecklistDef(booking, ChecklistPhase.SIGN_OUT, "SIGNOUT_PROCEDURE_RECORD", "Operative procedure officially recorded in chart", true);
        addChecklistDef(booking, ChecklistPhase.SIGN_OUT, "SIGNOUT_COUNTS", "Instrument, sponge, and needle counts confirmed correct", true);
        addChecklistDef(booking, ChecklistPhase.SIGN_OUT, "SIGNOUT_SPECIMENS", "Surgical specimens correctly labelled with patient details", false);
        addChecklistDef(booking, ChecklistPhase.SIGN_OUT, "SIGNOUT_EQUIPMENT", "Equipment malfunctions identified and logged if any", false);
        addChecklistDef(booking, ChecklistPhase.SIGN_OUT, "SIGNOUT_RECOVERY_PLAN", "Surgeon and anesthesiologist review recovery key concerns", true);

        // 5. Postoperative Handover
        addChecklistDef(booking, ChecklistPhase.POSTOPERATIVE_HANDOVER, "HANDOVER_AIRWAY", "Airway patent and respiratory status stable", true);
        addChecklistDef(booking, ChecklistPhase.POSTOPERATIVE_HANDOVER, "HANDOVER_VITALS", "Baseline post-anesthesia vitals recorded and within limits", true);
        addChecklistDef(booking, ChecklistPhase.POSTOPERATIVE_HANDOVER, "HANDOVER_LINES_DRAINS", "Surgical drains, catheters, and IV access lines checked", true);
        addChecklistDef(booking, ChecklistPhase.POSTOPERATIVE_HANDOVER, "HANDOVER_POSTOP_ORDERS", "Postoperative analgesia, antiemetic, and care orders signed", true);
        addChecklistDef(booking, ChecklistPhase.POSTOPERATIVE_HANDOVER, "HANDOVER_NURSE_ACCEPT", "Formal clinical handover accepted by receiving recovery/ward nurse", true);
    }

    private void addChecklistDef(SurgeryBooking booking, ChecklistPhase phase, String itemCode, String label, boolean isMandatory) {
        SurgeryChecklistItem item = new SurgeryChecklistItem(booking, phase, itemCode, label, isMandatory);
        booking.addChecklistItem(item);
    }

    @Transactional
    public SurgeryChecklistItem updateChecklistItem(Long itemId, ChecklistItemUpdateDto dto, String username) {
        SurgeryChecklistItem item = checklistRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist item not found: " + itemId));

        item.setStatus(dto.getStatus());
        item.setRemarks(dto.getRemarks());
        item.setCompletedBy(username);
        item.setCompletedAt(dto.getStatus() == ChecklistStatus.COMPLETED ? LocalDateTime.now() : null);

        SurgeryChecklistItem saved = checklistRepository.save(item);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                item.getSurgeryBooking(), null, "CHECKLIST_UPDATE", username,
                String.format("Checklist [%s] %s set to %s. Notes: %s",
                        item.getPhase(), item.getItemCode(), item.getStatus(),
                        dto.getRemarks() != null ? dto.getRemarks() : "None")
        );
        auditTrailRepository.save(audit);

        return saved;
    }

    /**
     * Checks if all mandatory checklist items for a phase are COMPLETED.
     */
    public boolean areMandatoryItemsCompleted(SurgeryBooking booking, ChecklistPhase phase) {
        long pendingMandatoryCount = checklistRepository.countBySurgeryBookingAndPhaseAndIsMandatoryTrueAndStatusNot(
                booking, phase, ChecklistStatus.COMPLETED);
        return pendingMandatoryCount == 0;
    }

    /**
     * Updates surgery status with strict validation of state machine and mandatory preop checklist items.
     */
    @Transactional
    public SurgeryBooking updateSurgeryStatus(Long surgeryId, SurgeryStatusUpdateDto dto, String username) {
        SurgeryBooking booking = bookingRepository.findByIdWithLock(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        SurgeryStatus currentStatus = booking.getStatus();
        SurgeryStatus targetStatus = dto.getTargetStatus();

        if (currentStatus == targetStatus) {
            return booking;
        }

        // 1. Validate state transition
        Set<SurgeryStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowed.contains(targetStatus)) {
            throw new IllegalStateException(String.format(
                    "Invalid workflow transition: Cannot transition surgery from %s to %s.",
                    currentStatus, targetStatus
            ));
        }

        // 2. Validate mandatory preoperative checklist before moving to IN_PROGRESS
        if (targetStatus == SurgeryStatus.IN_PROGRESS) {
            if (!areMandatoryItemsCompleted(booking, ChecklistPhase.PREOPERATIVE)) {
                throw new IllegalStateException("Cannot start surgery: Mandatory Preoperative checklist items are incomplete. All mandatory preop checks must be completed first.");
            }
        }

        // 3. Handle state specific logic
        OperationTheatre theatre = booking.getTheatre();

        if (targetStatus == SurgeryStatus.PREPARATION) {
            theatre.setStatus(TheatreStatus.RESERVED);
            theatreRepository.save(theatre);
        } else if (targetStatus == SurgeryStatus.IN_PROGRESS) {
            booking.setActualStartTime(LocalDateTime.now());
            theatre.setStatus(TheatreStatus.IN_USE);
            theatreRepository.save(theatre);
        } else if (targetStatus == SurgeryStatus.COMPLETED) {
            booking.setActualEndTime(LocalDateTime.now());
            theatre.setStatus(TheatreStatus.CLEANING);
            theatreRepository.save(theatre);

            if (dto.getOperativeNotes() != null) {
                booking.setOperativeNotes(dto.getOperativeNotes());
            }
            if (dto.getAnesthesiaNotes() != null) {
                booking.setAnesthesiaNotes(dto.getAnesthesiaNotes());
            }
            if (dto.getPostoperativeDiagnosis() != null) {
                booking.setPostoperativeDiagnosis(dto.getPostoperativeDiagnosis());
            }
            if (dto.getRecoveryDestination() != null) {
                booking.setRecoveryDestination(dto.getRecoveryDestination());
            }
        } else if (targetStatus == SurgeryStatus.POSTPONED) {
            booking.setPostponedReason(dto.getReason() != null ? dto.getReason() : "Postponed by clinical team");
            theatre.setStatus(TheatreStatus.AVAILABLE);
            theatreRepository.save(theatre);
        }

        booking.setStatus(targetStatus);
        SurgeryBooking saved = bookingRepository.save(booking);

        // 4. Record audit trail
        String detailText = String.format("Status transitioned from %s to %s. Details: %s",
                currentStatus, targetStatus, dto.getReason() != null ? dto.getReason() : "Workflow update");
        SurgeryAuditTrail audit = new SurgeryAuditTrail(saved, currentStatus.name(), targetStatus.name(), username, detailText);
        auditTrailRepository.save(audit);

        auditEventService.record(username, "SURGERY_STATUS_CHANGE", "SurgeryBooking", saved.getId(), detailText);

        return saved;
    }

    /**
     * Saves clinical operative notes and findings.
     */
    @Transactional
    public SurgeryBooking saveOperativeNotes(Long surgeryId, String operativeNotes, String anesthesiaNotes,
                                            String postopDiagnosis, String username) {
        SurgeryBooking booking = bookingRepository.findByIdWithLock(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        if (operativeNotes != null) {
            booking.setOperativeNotes(operativeNotes);
        }
        if (anesthesiaNotes != null) {
            booking.setAnesthesiaNotes(anesthesiaNotes);
        }
        if (postopDiagnosis != null) {
            booking.setPostoperativeDiagnosis(postopDiagnosis);
        }

        SurgeryBooking saved = bookingRepository.save(booking);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                saved, null, "OPERATIVE_NOTES_SAVED", username,
                "Operative notes and postoperative diagnosis documented by " + username
        );
        auditTrailRepository.save(audit);

        return saved;
    }

    /**
     * Records postoperative handover to recovery or inpatient ward.
     */
    @Transactional
    public SurgeryBooking recordHandover(Long surgeryId, String recoveryDestination, String handoverNotes, String username) {
        SurgeryBooking booking = bookingRepository.findByIdWithLock(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        if (recoveryDestination == null || recoveryDestination.trim().isBlank()) {
            throw new IllegalArgumentException("Recovery destination is mandatory for postoperative handover.");
        }

        booking.setRecoveryDestination(recoveryDestination.trim());
        SurgeryBooking saved = bookingRepository.save(booking);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                saved, null, "POSTOP_HANDOVER", username,
                String.format("Handover to %s recorded. Notes: %s", recoveryDestination.trim(),
                        handoverNotes != null ? handoverNotes : "None")
        );
        auditTrailRepository.save(audit);

        return saved;
    }
}
