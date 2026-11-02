package com.healthcare.hospital.enterprise.phase4.budgettransfer.repository;
import com.healthcare.hospital.enterprise.phase4.budgettransfer.entity.BudgetTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface BudgetTransferRepository extends JpaRepository<BudgetTransfer,Long> {
    List<BudgetTransfer> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<BudgetTransfer> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<BudgetTransfer> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<BudgetTransfer> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<BudgetTransfer> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from BudgetTransfer r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from BudgetTransfer r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
