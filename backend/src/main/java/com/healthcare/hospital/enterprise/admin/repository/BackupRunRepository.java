package com.healthcare.hospital.enterprise.admin.repository;

import com.healthcare.hospital.enterprise.admin.entity.BackupRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for backup execution history. */
public interface BackupRunRepository extends JpaRepository<BackupRun, Long> {
    List<BackupRun> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<BackupRun> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<BackupRun> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<BackupRun> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<BackupRun> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from BackupRun e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from BackupRun e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<BackupRun> search(@Param("status") String status, @Param("assignee") String assignee);
}
