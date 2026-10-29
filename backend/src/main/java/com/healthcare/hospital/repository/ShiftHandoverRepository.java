package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.ShiftHandover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftHandoverRepository extends JpaRepository<ShiftHandover, Long> {
    Optional<ShiftHandover> findByReferenceCode(String referenceCode);
    boolean existsByReferenceCode(String referenceCode);
    List<ShiftHandover> findAllByOrderByShiftDateDescCreatedAtDesc();
    List<ShiftHandover> findByStatusOrderByShiftDateDesc(String status);
    List<ShiftHandover> findByDepartmentIgnoreCaseOrderByShiftDateDesc(String department);
    long countByStatus(String status);
    long countByUrgentTrueAndStatusNot(String status);
    long countByShiftDateBetween(LocalDateTime start, LocalDateTime end);
    long countByDepartmentIgnoreCaseAndStatus(String department, String status);

    @Query("select h from ShiftHandover h where (:status is null or h.status=:status) and " +
        "(:department is null or lower(h.department)=lower(:department)) and " +
        "(:term is null or lower(h.referenceCode) like lower(concat('%',:term,'%')) or " +
        "lower(h.outgoingStaff) like lower(concat('%',:term,'%')) or lower(h.incomingStaff) like lower(concat('%',:term,'%'))) " +
        "order by h.urgent desc, h.shiftDate desc")
    List<ShiftHandover> search(@Param("status") String status, @Param("department") String department, @Param("term") String term);
}
