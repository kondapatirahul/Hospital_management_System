package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Medicine;
import com.healthcare.hospital.entity.StockTransaction;
import com.healthcare.hospital.enums.StockTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findTop10ByOrderByTransactionDateDesc();

    List<StockTransaction> findByMedicineOrderByTransactionDateDesc(Medicine medicine);

    @Query("SELECT t FROM StockTransaction t WHERE " +
           "(:medicineId IS NULL OR t.medicine.id = :medicineId) AND " +
           "(:type IS NULL OR t.transactionType = :type) AND " +
           "(:startDate IS NULL OR t.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "ORDER BY t.transactionDate DESC")
    Page<StockTransaction> searchTransactions(
            @Param("medicineId") Long medicineId,
            @Param("type") StockTransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT t FROM StockTransaction t WHERE " +
           "(:startDate IS NULL OR t.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "ORDER BY t.transactionDate DESC")
    List<StockTransaction> findMovementReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
