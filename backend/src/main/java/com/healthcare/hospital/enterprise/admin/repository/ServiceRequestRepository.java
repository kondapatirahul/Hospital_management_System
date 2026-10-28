package com.healthcare.hospital.enterprise.admin.repository;

import com.healthcare.hospital.enterprise.admin.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for internal it/service request tracking. */
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<ServiceRequest> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<ServiceRequest> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<ServiceRequest> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<ServiceRequest> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from ServiceRequest e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from ServiceRequest e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<ServiceRequest> search(@Param("status") String status, @Param("assignee") String assignee);
}
