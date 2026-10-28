package com.healthcare.hospital.enterprise.admin.repository;

import com.healthcare.hospital.enterprise.admin.entity.StaffCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for staff credential and expiry tracking. */
public interface StaffCredentialRepository extends JpaRepository<StaffCredential, Long> {
    List<StaffCredential> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<StaffCredential> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<StaffCredential> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<StaffCredential> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<StaffCredential> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from StaffCredential e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from StaffCredential e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<StaffCredential> search(@Param("status") String status, @Param("assignee") String assignee);
}
