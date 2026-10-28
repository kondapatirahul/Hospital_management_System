package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Invoice;
import com.healthcare.hospital.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findAllByOrderByReceivedAtDesc();
    List<PaymentRecord> findByInvoiceOrderByReceivedAtDesc(Invoice invoice);
    boolean existsByReceiptNumber(String receiptNumber);

    @Query("select coalesce(sum(p.amount), 0) from PaymentRecord p where p.invoice = :invoice")
    BigDecimal sumReceivedForInvoice(@Param("invoice") Invoice invoice);
}
