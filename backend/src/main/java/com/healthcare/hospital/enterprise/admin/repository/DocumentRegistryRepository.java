package com.healthcare.hospital.enterprise.admin.repository;

import com.healthcare.hospital.enterprise.admin.entity.DocumentRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for medical document registry metadata. */
public interface DocumentRegistryRepository extends JpaRepository<DocumentRegistry, Long> {
    List<DocumentRegistry> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<DocumentRegistry> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<DocumentRegistry> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<DocumentRegistry> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<DocumentRegistry> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from DocumentRegistry e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from DocumentRegistry e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<DocumentRegistry> search(@Param("status") String status, @Param("assignee") String assignee);
}
