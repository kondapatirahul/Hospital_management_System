package com.healthcare.hospital.enterprise.quality.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Audit trail for material quality workflow transitions. Do not store clinical narrative here. */
@Entity
@Table(name="clinical_quality_audit_events", indexes={
    @Index(name="idx_quality_audit_case", columnList="case_reference,occurred_at"),
    @Index(name="idx_quality_audit_actor", columnList="actor_username,occurred_at"),
    @Index(name="idx_quality_audit_type", columnList="event_type")
})
public class QualityAuditEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="case_reference", nullable=false, length=32) private String caseReference;
    @Column(name="event_type", nullable=false, length=48) private String eventType;
    @Column(name="actor_username", nullable=false, length=100) private String actorUsername;
    @Column(name="from_state", length=32) private String fromState;
    @Column(name="to_state", length=32) private String toState;
    @Column(length=500) private String summary;
    @Column(length=80) private String requestCorrelationId;
    @Column(nullable=false) private LocalDateTime occurredAt=LocalDateTime.now();
    public QualityAuditEvent() { }
    public Long getId() { return id; }
    public void setId(Long value) { this.id=value; }
    public String getCaseReference() { return caseReference; }
    public void setCaseReference(String value) { this.caseReference=value; }
    public String getEventType() { return eventType; }
    public void setEventType(String value) { this.eventType=value; }
    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String value) { this.actorUsername=value; }
    public String getFromState() { return fromState; }
    public void setFromState(String value) { this.fromState=value; }
    public String getToState() { return toState; }
    public void setToState(String value) { this.toState=value; }
    public String getSummary() { return summary; }
    public void setSummary(String value) { this.summary=value; }
    public String getRequestCorrelationId() { return requestCorrelationId; }
    public void setRequestCorrelationId(String value) { this.requestCorrelationId=value; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime value) { this.occurredAt=value; }
}
