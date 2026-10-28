package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.Invoice;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.enums.PaymentStatus;
import com.healthcare.hospital.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;

    public BillingService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public synchronized String generateNextInvoiceNumber() {
        int currentYear = LocalDate.now().getYear();
        long nextNum = 1;
        Optional<Invoice> topInvoice = invoiceRepository.findTopByOrderByIdDesc();
        if (topInvoice.isPresent() && topInvoice.get().getId() != null) {
            nextNum = topInvoice.get().getId() + 1;
        }
        return String.format("INV-%d-%04d", currentYear, nextNum);
    }

    public List<Invoice> getPatientInvoices(Patient patient) {
        return invoiceRepository.findByPatientOrderByInvoiceDateDesc(patient);
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Transactional
    public Invoice markInvoiceAsPaid(Long invoiceId, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        invoice.setStatus(PaymentStatus.PAID);
        invoice.setPaymentMethod(paymentMethod != null ? paymentMethod : "Cash");
        invoice.setPaymentDate(LocalDate.now());

        return invoiceRepository.save(invoice);
    }
}
