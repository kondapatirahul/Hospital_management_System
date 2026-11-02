package com.healthcare.hospital.modules.phase4.payercontract.repository;
import com.healthcare.hospital.modules.phase4.payercontract.entity.PayerContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface PayerContractRepository extends JpaRepository<PayerContract,Long> {
    List<PayerContract> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<PayerContract> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<PayerContract> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<PayerContract> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<PayerContract> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from PayerContract r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from PayerContract r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
