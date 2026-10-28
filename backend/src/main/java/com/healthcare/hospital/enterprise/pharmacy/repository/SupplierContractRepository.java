package com.healthcare.hospital.enterprise.pharmacy.repository;

import com.healthcare.hospital.enterprise.pharmacy.entity.SupplierContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for supplier contract tracking. */
public interface SupplierContractRepository extends JpaRepository<SupplierContract, Long> {
    List<SupplierContract> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<SupplierContract> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<SupplierContract> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<SupplierContract> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<SupplierContract> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from SupplierContract e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from SupplierContract e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<SupplierContract> search(@Param("status") String status, @Param("assignee") String assignee);
}
