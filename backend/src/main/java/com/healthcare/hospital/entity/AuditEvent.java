package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Append-only application audit event. Avoid recording passwords or sensitive clinical content here. */
@Entity
@Table(name = "audit_events", indexes = {
    @Index(name = "idx_audit_created", columnList = "created_at"),
    @Index(name = "idx_audit_actor", columnList = "actor")
})
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String actor;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 100)
    private String entityType;

    @Column(length = 80)
    private String entityId;

    @Column(length = 1000)
    private String details;

    @Column(length = 60)
    private String source = "WEB";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected AuditEvent() {}
    public AuditEvent(String actor, String action, String entityType, String entityId, String details, String source) {
        this.actor = actor == null || actor.isBlank() ? "system" : actor;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.source = source == null ? "WEB" : source;
        this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public String getSource() { return source; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
