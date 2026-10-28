package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.GoodsReceivedNote;
import com.healthcare.hospital.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsReceivedNoteRepository extends JpaRepository<GoodsReceivedNote, Long> {
    Optional<GoodsReceivedNote> findByGrnNumberIgnoreCase(String grnNumber);
    List<GoodsReceivedNote> findByPurchaseOrder(PurchaseOrder purchaseOrder);
    Page<GoodsReceivedNote> findAllByOrderByReceivedDateDesc(Pageable pageable);
}
