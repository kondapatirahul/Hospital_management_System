package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.GoodsReceivedItem;
import com.healthcare.hospital.modules.legacy.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsReceivedItemRepository extends JpaRepository<GoodsReceivedItem, Long> {
    List<GoodsReceivedItem> findByGoodsReceivedNote(GoodsReceivedNote goodsReceivedNote);
}
