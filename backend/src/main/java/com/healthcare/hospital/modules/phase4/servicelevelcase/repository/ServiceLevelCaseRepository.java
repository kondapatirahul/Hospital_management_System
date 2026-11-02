package com.healthcare.hospital.modules.phase4.servicelevelcase.repository;
import com.healthcare.hospital.modules.phase4.servicelevelcase.entity.ServiceLevelCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface ServiceLevelCaseRepository extends JpaRepository<ServiceLevelCase,Long> {
    List<ServiceLevelCase> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<ServiceLevelCase> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<ServiceLevelCase> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<ServiceLevelCase> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<ServiceLevelCase> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from ServiceLevelCase r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from ServiceLevelCase r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
