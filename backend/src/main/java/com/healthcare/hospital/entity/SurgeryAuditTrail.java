package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable audit trail of surgical state transitions and important events.
 */
@Entity
@Table(name = "surgery_audit_trails", indexes = {
    @Index(name = "idx_surg_audit_booking", columnList = "surgery_booking_id"),
    @Index(name = "idx_surg_audit_time", columnList = "timestamp")
})
public class SurgeryAuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgery_booking_id", nullable = false)
    private SurgeryBooking surgeryBooking;

    @Column(name = "transition_from", length = 50)
    private String transitionFrom;

    @Column(name = "transition_to", nullable = false, length = 50)
    private String transitionTo;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "action_details", length = 1000)
    private String actionDetails;

    public SurgeryAuditTrail() {
    }

    public SurgeryAuditTrail(SurgeryBooking surgeryBooking, String transitionFrom, String transitionTo, String performedBy, String actionDetails) {
        this.surgeryBooking = surgeryBooking;
        this.transitionFrom = transitionFrom;
        this.transitionTo = transitionTo;
        this.performedBy = performedBy;
        this.actionDetails = actionDetails;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SurgeryBooking getSurgeryBooking() {
        return surgeryBooking;
    }

    public void setSurgeryBooking(SurgeryBooking surgeryBooking) {
        this.surgeryBooking = surgeryBooking;
    }

    public String getTransitionFrom() {
        return transitionFrom;
    }

    public void setTransitionFrom(String transitionFrom) {
        this.transitionFrom = transitionFrom;
    }

    public String getTransitionTo() {
        return transitionTo;
    }

    public void setTransitionTo(String transitionTo) {
        this.transitionTo = transitionTo;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }
}
