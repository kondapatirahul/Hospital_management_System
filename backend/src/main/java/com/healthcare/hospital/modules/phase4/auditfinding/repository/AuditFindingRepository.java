package com.healthcare.hospital.modules.phase4.auditfinding.repository;
import com.healthcare.hospital.modules.phase4.auditfinding.entity.AuditFinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface AuditFindingRepository extends JpaRepository<AuditFinding,Long> {
    List<AuditFinding> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<AuditFinding> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<AuditFinding> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<AuditFinding> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<AuditFinding> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from AuditFinding r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from AuditFinding r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
