package com.healthcare.hospital.enterprise.admin.repository;

import com.healthcare.hospital.enterprise.admin.entity.CommunicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for patient communication history. */
public interface CommunicationLogRepository extends JpaRepository<CommunicationLog, Long> {
    List<CommunicationLog> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<CommunicationLog> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<CommunicationLog> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<CommunicationLog> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<CommunicationLog> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from CommunicationLog e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from CommunicationLog e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<CommunicationLog> search(@Param("status") String status, @Param("assignee") String assignee);
}
