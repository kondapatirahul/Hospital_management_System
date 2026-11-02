package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.modules.legacy.entity.AuditEvent;
import com.healthcare.hospital.modules.legacy.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditEventService {
    private final AuditEventRepository repository;
    public AuditEventService(AuditEventRepository repository) { this.repository = repository; }

    @Transactional
    public void record(String actor, String action, String entityType, Object entityId, String details) {
        String id = entityId == null ? null : String.valueOf(entityId);
        String safeDetails = details == null ? null : details.substring(0, Math.min(details.length(), 1000));
        repository.save(new AuditEvent(actor, action, entityType, id, safeDetails, "WEB"));
    }
}
