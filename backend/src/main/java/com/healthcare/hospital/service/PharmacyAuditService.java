package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.PharmacyAuditLog;
import com.healthcare.hospital.repository.PharmacyAuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PharmacyAuditService {

    private final PharmacyAuditLogRepository auditLogRepository;

    public PharmacyAuditService(PharmacyAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void logEvent(String eventType, String entityName, String entityId, String username, String details) {
        PharmacyAuditLog log = new PharmacyAuditLog(
                eventType,
                entityName,
                entityId,
                username != null ? username : "SYSTEM",
                details,
                "127.0.0.1"
        );
        auditLogRepository.save(log);
    }

    public Page<PharmacyAuditLog> searchLogs(String query, LocalDateTime start, LocalDateTime end, int page, int size) {
        return auditLogRepository.searchAuditLogs(query, start, end, PageRequest.of(page, size));
    }
}
