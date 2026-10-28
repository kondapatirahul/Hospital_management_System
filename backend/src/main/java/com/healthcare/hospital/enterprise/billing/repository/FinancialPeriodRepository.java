package com.healthcare.hospital.enterprise.billing.repository;

import com.healthcare.hospital.enterprise.billing.entity.FinancialPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for financial period close controls. */
public interface FinancialPeriodRepository extends JpaRepository<FinancialPeriod, Long> {
    List<FinancialPeriod> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<FinancialPeriod> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<FinancialPeriod> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<FinancialPeriod> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<FinancialPeriod> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from FinancialPeriod e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from FinancialPeriod e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<FinancialPeriod> search(@Param("status") String status, @Param("assignee") String assignee);
}
