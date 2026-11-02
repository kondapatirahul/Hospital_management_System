package com.healthcare.hospital.enterprise.phase4.expensereview.repository;
import com.healthcare.hospital.enterprise.phase4.expensereview.entity.ExpenseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface ExpenseReviewRepository extends JpaRepository<ExpenseReview,Long> {
    List<ExpenseReview> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<ExpenseReview> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<ExpenseReview> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<ExpenseReview> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<ExpenseReview> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from ExpenseReview r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from ExpenseReview r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
