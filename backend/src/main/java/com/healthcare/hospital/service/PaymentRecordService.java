package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.Invoice;
import com.healthcare.hospital.entity.PaymentRecord;
import com.healthcare.hospital.enums.PaymentStatus;
import com.healthcare.hospital.repository.InvoiceRepository;
import com.healthcare.hospital.repository.PaymentRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/** Records individual receipts and updates invoice settlement status from recorded amounts. */
@Service
public class PaymentRecordService {
    private static final Set<String> METHODS =
            Set.of("Cash", "UPI", "Credit Card", "Insurance", "Bank Transfer");
    private final PaymentRecordRepository payments;
    private final InvoiceRepository invoices;

    public PaymentRecordService(PaymentRecordRepository payments, InvoiceRepository invoices) {
        this.payments = payments;
        this.invoices = invoices;
    }

    @Transactional
    public PaymentRecord record(Long invoiceId, BigDecimal amount, String method,
                                String reference, String receivedBy, String notes) {
        Invoice invoice = invoices.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice was not found."));
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (invoice.getTotalAmount() == null || invoice.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Invoice total must be positive before recording payment.");
        }
        String cleanMethod = method == null ? "" : method.trim();
        if (!METHODS.contains(cleanMethod)) throw new IllegalArgumentException("Choose a supported payment method.");
        BigDecimal previouslyReceived = payments.sumReceivedForInvoice(invoice);
        BigDecimal remaining = invoice.getTotalAmount().subtract(previouslyReceived);
        if (amount.compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Payment exceeds the remaining balance of " + remaining + ".");
        }
        String receipt = "RCT-" + LocalDate.now().getYear() + "-" + Long.toUnsignedString(System.nanoTime(), 36).toUpperCase();
        PaymentRecord record = new PaymentRecord(receipt, invoice, amount, cleanMethod,
                optional(reference, 100), optional(receivedBy, 100));
        record.setNotes(optional(notes, 500));
        PaymentRecord saved = payments.save(record);
        BigDecimal totalReceived = previouslyReceived.add(amount);
        if (totalReceived.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(PaymentStatus.PAID);
            invoice.setPaymentDate(LocalDate.now());
            invoice.setPaymentMethod(cleanMethod);
        } else {
            invoice.setStatus(PaymentStatus.PARTIAL);
            invoice.setPaymentMethod(cleanMethod);
        }
        invoices.save(invoice);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<PaymentRecord> recent() { return payments.findAllByOrderByReceivedAtDesc(); }

    @Transactional(readOnly = true)
    public BigDecimal receivedFor(Invoice invoice) { return payments.sumReceivedForInvoice(invoice); }

    private String optional(String value, int max) {
        if (value == null || value.isBlank()) return null;
        String cleaned = value.trim();
        if (cleaned.length() > max) throw new IllegalArgumentException("Field exceeds " + max + " characters.");
        return cleaned;
    }
}
