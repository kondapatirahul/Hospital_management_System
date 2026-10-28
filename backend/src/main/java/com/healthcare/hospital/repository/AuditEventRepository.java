package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findTop250ByOrderByCreatedAtDesc();
    List<AuditEvent> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    List<AuditEvent> findByActorIgnoreCaseOrderByCreatedAtDesc(String actor);
    long countByCreatedAtAfter(LocalDateTime since);
}
