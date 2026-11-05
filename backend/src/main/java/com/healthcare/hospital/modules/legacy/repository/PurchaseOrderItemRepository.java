package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.PurchaseOrder;
import com.healthcare.hospital.modules.legacy.entity.PurchaseOrderItem;
import com.healthcare.hospital.modules.legacy.entity.Medicine;
import com.healthcare.hospital.shared.enums.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Query("SELECT COALESCE(SUM(item.orderedQuantity - item.receivedQuantity), 0) " +
           "FROM PurchaseOrderItem item WHERE item.medicine = :medicine " +
           "AND item.purchaseOrder.status IN :statuses")
    long sumUnreceivedQuantityForMedicineAndOrderStatusIn(
            @Param("medicine") Medicine medicine,
            @Param("statuses") List<PurchaseOrderStatus> statuses);
}
