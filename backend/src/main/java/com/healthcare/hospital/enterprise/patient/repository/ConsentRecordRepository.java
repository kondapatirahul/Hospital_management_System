package com.healthcare.hospital.enterprise.patient.repository;

import com.healthcare.hospital.enterprise.patient.entity.ConsentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for patient consent record management. */
public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, Long> {
    List<ConsentRecord> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<ConsentRecord> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<ConsentRecord> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<ConsentRecord> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<ConsentRecord> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from ConsentRecord e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from ConsentRecord e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<ConsentRecord> search(@Param("status") String status, @Param("assignee") String assignee);
}
