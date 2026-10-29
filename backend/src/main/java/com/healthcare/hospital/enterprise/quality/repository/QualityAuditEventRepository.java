package com.healthcare.hospital.enterprise.quality.repository;

import com.healthcare.hospital.enterprise.quality.entity.QualityAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QualityAuditEventRepository extends JpaRepository<QualityAuditEvent,Long> {
    List<QualityAuditEvent> findTop250ByCaseReferenceOrderByOccurredAtDesc(String caseReference);
    List<QualityAuditEvent> findTop250ByActorUsernameIgnoreCaseOrderByOccurredAtDesc(String actor);
}
