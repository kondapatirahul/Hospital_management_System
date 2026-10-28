package com.healthcare.hospital.enterprise.pharmacy.repository;

import com.healthcare.hospital.enterprise.pharmacy.entity.PharmacyStocktake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for pharmacy stocktake sessions. */
public interface PharmacyStocktakeRepository extends JpaRepository<PharmacyStocktake, Long> {
    List<PharmacyStocktake> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<PharmacyStocktake> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<PharmacyStocktake> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<PharmacyStocktake> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<PharmacyStocktake> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from PharmacyStocktake e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from PharmacyStocktake e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<PharmacyStocktake> search(@Param("status") String status, @Param("assignee") String assignee);
}
