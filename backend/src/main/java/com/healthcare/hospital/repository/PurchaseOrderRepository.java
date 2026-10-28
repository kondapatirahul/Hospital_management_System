package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.PurchaseOrder;
import com.healthcare.hospital.entity.Supplier;
import com.healthcare.hospital.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoNumberIgnoreCase(String poNumber);

    List<PurchaseOrder> findBySupplierOrderByOrderDateDesc(Supplier supplier);

    @Query("SELECT po FROM PurchaseOrder po WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(po.poNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(po.supplier.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR po.status = :status) AND " +
           "(:supplierId IS NULL OR po.supplier.id = :supplierId) " +
           "ORDER BY po.orderDate DESC")
    Page<PurchaseOrder> searchPurchaseOrders(
            @Param("query") String query,
            @Param("status") PurchaseOrderStatus status,
            @Param("supplierId") Long supplierId,
            Pageable pageable
    );

    @Query("SELECT po FROM PurchaseOrder po WHERE " +
           "(:startDate IS NULL OR po.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR po.orderDate <= :endDate) AND " +
           "(:supplierId IS NULL OR po.supplier.id = :supplierId) " +
           "ORDER BY po.orderDate DESC")
    List<PurchaseOrder> findPurchasesReport(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("supplierId") Long supplierId
    );
}
