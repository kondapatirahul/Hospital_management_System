package com.healthcare.hospital.enterprise.admin.repository;

import com.healthcare.hospital.enterprise.admin.entity.DataRetentionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for data retention policy management. */
public interface DataRetentionPolicyRepository extends JpaRepository<DataRetentionPolicy, Long> {
    List<DataRetentionPolicy> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<DataRetentionPolicy> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<DataRetentionPolicy> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<DataRetentionPolicy> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<DataRetentionPolicy> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from DataRetentionPolicy e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from DataRetentionPolicy e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<DataRetentionPolicy> search(@Param("status") String status, @Param("assignee") String assignee);
}
