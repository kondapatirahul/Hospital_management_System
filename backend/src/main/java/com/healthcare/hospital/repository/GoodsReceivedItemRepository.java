package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.GoodsReceivedItem;
import com.healthcare.hospital.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsReceivedItemRepository extends JpaRepository<GoodsReceivedItem, Long> {
    List<GoodsReceivedItem> findByGoodsReceivedNote(GoodsReceivedNote goodsReceivedNote);
}
