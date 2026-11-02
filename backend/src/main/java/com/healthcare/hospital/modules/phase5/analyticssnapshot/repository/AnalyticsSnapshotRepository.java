package com.healthcare.hospital.modules.phase5.analyticssnapshot.repository;

import com.healthcare.hospital.modules.phase5.analyticssnapshot.entity.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, Long> {
    List<AnalyticsSnapshot> findTop100ByOrderByCreatedAtDesc();
    List<AnalyticsSnapshot> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<AnalyticsSnapshot> findByOwnerIgnoreCaseOrderByDueDateAsc(String owner);
    List<AnalyticsSnapshot> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    List<AnalyticsSnapshot> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    Optional<AnalyticsSnapshot> findByReferenceCodeIgnoreCase(String referenceCode);
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date, String status);
}
