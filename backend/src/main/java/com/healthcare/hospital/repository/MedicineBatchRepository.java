package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Medicine;
import com.healthcare.hospital.entity.MedicineBatch;
import com.healthcare.hospital.enums.BatchStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, Long> {

    Optional<MedicineBatch> findByBatchNumberAndMedicine(String batchNumber, Medicine medicine);

    List<MedicineBatch> findByMedicineOrderByExpiryDateAsc(Medicine medicine);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM MedicineBatch b WHERE b.id = :id")
    Optional<MedicineBatch> findByIdWithLock(@Param("id") Long id);

    /**
     * Earliest Expiry First Out (FEFO) query for dispensing:
     * Only returns batches that have stock > 0, are ACTIVE, and haven't expired.
     */
    @Query("SELECT b FROM MedicineBatch b WHERE b.medicine = :medicine " +
           "AND b.currentQuantity > 0 " +
           "AND b.status = com.healthcare.hospital.enums.BatchStatus.ACTIVE " +
           "AND b.expiryDate >= :today " +
           "ORDER BY b.expiryDate ASC, b.id ASC")
    List<MedicineBatch> findEligibleBatchesForDispensing(
            @Param("medicine") Medicine medicine,
            @Param("today") LocalDate today
    );

    @Query("SELECT b FROM MedicineBatch b WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(b.batchNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(b.medicine.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(b.medicine.medicineCode) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:medicineId IS NULL OR b.medicine.id = :medicineId)")
    Page<MedicineBatch> searchBatches(
            @Param("query") String query,
            @Param("status") BatchStatus status,
            @Param("medicineId") Long medicineId,
            Pageable pageable
    );

    @Query("SELECT b FROM MedicineBatch b WHERE b.currentQuantity > 0 " +
           "AND b.expiryDate BETWEEN :today AND :nearExpiryDate " +
           "ORDER BY b.expiryDate ASC")
    List<MedicineBatch> findNearExpiryBatches(
            @Param("today") LocalDate today,
            @Param("nearExpiryDate") LocalDate nearExpiryDate
    );

    @Query("SELECT b FROM MedicineBatch b WHERE b.currentQuantity > 0 " +
           "AND b.expiryDate < :today " +
           "ORDER BY b.expiryDate ASC")
    List<MedicineBatch> findExpiredBatches(@Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM MedicineBatch b WHERE b.currentQuantity > 0 AND b.expiryDate < :today")
    long countExpiredBatches(@Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM MedicineBatch b WHERE b.currentQuantity > 0 AND b.expiryDate BETWEEN :today AND :nearExpiryDate")
    long countNearExpiryBatches(@Param("today") LocalDate today, @Param("nearExpiryDate") LocalDate nearExpiryDate);

    @Query("SELECT COALESCE(SUM(b.currentQuantity), 0) FROM MedicineBatch b WHERE b.medicine = :medicine AND b.expiryDate >= :today AND b.status = com.healthcare.hospital.enums.BatchStatus.ACTIVE")
    int calculateValidStockForMedicine(@Param("medicine") Medicine medicine, @Param("today") LocalDate today);
}
