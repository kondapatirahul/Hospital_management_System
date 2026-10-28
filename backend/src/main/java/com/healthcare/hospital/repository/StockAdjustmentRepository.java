package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.StockAdjustment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
    Page<StockAdjustment> findAllByOrderByAdjustmentDateDesc(Pageable pageable);
}
