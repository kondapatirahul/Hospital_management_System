package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findBySupplierCodeIgnoreCase(String code);
    List<Supplier> findByActiveTrueOrderByNameAsc();
    boolean existsBySupplierCodeIgnoreCase(String code);

    @Query("SELECT s FROM Supplier s WHERE (:query IS NULL OR :query = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.supplierCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Supplier> searchSuppliers(@Param("query") String query, Pageable pageable);
}
