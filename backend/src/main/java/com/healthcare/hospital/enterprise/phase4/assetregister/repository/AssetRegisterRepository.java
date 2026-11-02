package com.healthcare.hospital.enterprise.phase4.assetregister.repository;
import com.healthcare.hospital.enterprise.phase4.assetregister.entity.AssetRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
public interface AssetRegisterRepository extends JpaRepository<AssetRegister,Long> {
    List<AssetRegister> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);
    List<AssetRegister> findByOwnerContainingIgnoreCaseOrderByCreatedAtDesc(String owner);
    List<AssetRegister> findByDepartmentContainingIgnoreCaseOrderByCreatedAtDesc(String department);
    List<AssetRegister> findByDueDateBeforeAndStatusNot(LocalDate date, String terminalStatus);
    List<AssetRegister> findByReferenceCodeIgnoreCase(String referenceCode);
    @Query("select coalesce(sum(r.amount),0) from AssetRegister r where upper(r.status) in :statuses")
    BigDecimal sumAmountByStatuses(@Param("statuses") List<String> statuses);
    @Query("select count(r) from AssetRegister r where upper(r.status) = upper(:status)")
    long countByStatus(@Param("status") String status);
}
