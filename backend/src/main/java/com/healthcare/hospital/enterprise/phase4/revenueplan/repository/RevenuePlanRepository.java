package com.healthcare.hospital.enterprise.phase4.revenueplan.repository;
import com.healthcare.hospital.enterprise.phase4.revenueplan.entity.RevenuePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface RevenuePlanRepository extends JpaRepository<RevenuePlan,Long> {
    List<RevenuePlan> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<RevenuePlan> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<RevenuePlan> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<RevenuePlan> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<RevenuePlan> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from RevenuePlan r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from RevenuePlan r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
