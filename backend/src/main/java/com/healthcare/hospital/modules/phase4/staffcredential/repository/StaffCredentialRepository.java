package com.healthcare.hospital.modules.phase4.staffcredential.repository;
import com.healthcare.hospital.modules.phase4.staffcredential.entity.StaffCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
@Repository("phase4StaffCredentialRepository")
public interface StaffCredentialRepository extends JpaRepository<StaffCredential,Long> {
    List<StaffCredential> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<StaffCredential> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<StaffCredential> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<StaffCredential> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<StaffCredential> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from Phase4StaffCredential r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from Phase4StaffCredential r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
