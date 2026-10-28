package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Medicine;
import com.healthcare.hospital.entity.MedicineCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByMedicineCodeIgnoreCase(String code);

    Optional<Medicine> findByNameIgnoreCase(String name);

    boolean existsByMedicineCodeIgnoreCase(String code);

    List<Medicine> findByCategoryAndActiveTrueOrderByNameAsc(MedicineCategory category);

    List<Medicine> findByActiveTrueOrderByNameAsc();

    @Query("SELECT m FROM Medicine m WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(m.medicineCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:categoryId IS NULL OR m.category.id = :categoryId) AND " +
           "(:status IS NULL OR :status = 'ALL' OR " +
           " (:status = 'ACTIVE' AND m.active = true) OR " +
           " (:status = 'INACTIVE' AND m.active = false) OR " +
           " (:status = 'IN_STOCK' AND m.totalStock > m.reorderLevel) OR " +
           " (:status = 'LOW_STOCK' AND m.totalStock > 0 AND m.totalStock <= m.reorderLevel) OR " +
           " (:status = 'OUT_OF_STOCK' AND m.totalStock <= 0))")
    Page<Medicine> searchMedicines(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.active = true")
    long countActiveMedicines();

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.active = true AND m.totalStock > m.reorderLevel")
    long countAvailableMedicines();

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.active = true AND m.totalStock > 0 AND m.totalStock <= m.reorderLevel")
    long countLowStockMedicines();

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.active = true AND m.totalStock <= 0")
    long countOutOfStockMedicines();

    @Query("SELECT m FROM Medicine m WHERE m.active = true AND m.totalStock > 0 AND m.totalStock <= m.reorderLevel ORDER BY m.totalStock ASC")
    List<Medicine> findLowStockMedicines();

    @Query("SELECT m FROM Medicine m WHERE m.active = true AND m.totalStock <= 0 ORDER BY m.name ASC")
    List<Medicine> findOutOfStockMedicines();
}
