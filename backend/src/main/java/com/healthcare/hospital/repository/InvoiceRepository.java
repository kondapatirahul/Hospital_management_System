package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Invoice;
import com.healthcare.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPatientOrderByInvoiceDateDesc(Patient patient);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Optional<Invoice> findTopByOrderByIdDesc();
}
