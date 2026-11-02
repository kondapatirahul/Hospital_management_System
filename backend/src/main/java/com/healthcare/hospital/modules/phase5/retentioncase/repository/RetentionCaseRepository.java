package com.healthcare.hospital.modules.phase5.retentioncase.repository;

import com.healthcare.hospital.modules.phase5.retentioncase.entity.RetentionCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface RetentionCaseRepository extends JpaRepository<RetentionCase, Long> {
    List<RetentionCase> findTop100ByOrderByCreatedAtDesc();
    List<RetentionCase> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<RetentionCase> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<RetentionCase> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<RetentionCase> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<RetentionCase> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
