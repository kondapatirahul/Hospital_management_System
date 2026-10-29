package com.healthcare.hospital.enterprise.quality.service;

import com.healthcare.hospital.enterprise.quality.entity.QualityAuditEvent;
import com.healthcare.hospital.enterprise.quality.repository.QualityAuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Minimal-data audit logging for case lifecycle and action changes. */
@Service
@Transactional
public class QualityAuditService {
    private final QualityAuditEventRepository repository;

    public QualityAuditService(QualityAuditEventRepository repository) {
        this.repository = repository;
    }

    public QualityAuditEvent record(String reference, String eventType, String actor,
                                    String fromState, String toState, String summary) {
        QualityAuditEvent event = new QualityAuditEvent();
        event.setCaseReference(required(reference, "Case reference", 32));
        event.setEventType(required(eventType, "Event type", 48));
        event.setActorUsername(required(actor, "Actor", 100));
        event.setFromState(optional(fromState, 32));
        event.setToState(optional(toState, 32));
        event.setSummary(optional(summary, 500));
        return repository.save(event);
    }

    @Transactional(readOnly=true)
    public List<QualityAuditEvent> forCase(String reference) {
        return repository.findTop250ByCaseReferenceOrderByOccurredAtDesc(required(reference, "Case reference", 32));
    }

    @Transactional(readOnly=true)
    public List<QualityAuditEvent> forActor(String actor) {
        return repository.findTop250ByActorUsernameIgnoreCaseOrderByOccurredAtDesc(required(actor, "Actor", 100));
    }

    private String required(String value, String label, int max) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " is required.");
        String normalized=value.trim();
        if (normalized.length()>max) throw new IllegalArgumentException(label + " is too long.");
        return normalized;
    }

    private String optional(String value, int max) {
        if (value==null || value.isBlank()) return null;
        String normalized=value.trim();
        if (normalized.length()>max) throw new IllegalArgumentException("Audit summary is too long.");
        return normalized;
    }
}
