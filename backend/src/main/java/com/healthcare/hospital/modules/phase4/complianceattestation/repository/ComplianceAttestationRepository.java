package com.healthcare.hospital.modules.phase4.complianceattestation.repository;
import com.healthcare.hospital.modules.phase4.complianceattestation.entity.ComplianceAttestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface ComplianceAttestationRepository extends JpaRepository<ComplianceAttestation,Long> {
    List<ComplianceAttestation> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<ComplianceAttestation> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<ComplianceAttestation> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<ComplianceAttestation> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<ComplianceAttestation> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from ComplianceAttestation r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from ComplianceAttestation r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
