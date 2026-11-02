package com.healthcare.hospital.enterprise.phase4.purchaseapproval.repository;
import com.healthcare.hospital.enterprise.phase4.purchaseapproval.entity.PurchaseApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface PurchaseApprovalRepository extends JpaRepository<PurchaseApproval,Long> {
    List<PurchaseApproval> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<PurchaseApproval> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<PurchaseApproval> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<PurchaseApproval> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<PurchaseApproval> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from PurchaseApproval r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from PurchaseApproval r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
