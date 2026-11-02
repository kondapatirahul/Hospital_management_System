package com.healthcare.hospital.enterprise.phase5.consentrecord.repository;

import com.healthcare.hospital.enterprise.phase5.consentrecord.entity.ConsentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, Long> {
    List<ConsentRecord> findTop100ByOrderByCreatedAtDesc();
    List<ConsentRecord> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<ConsentRecord> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<ConsentRecord> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<ConsentRecord> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<ConsentRecord> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
