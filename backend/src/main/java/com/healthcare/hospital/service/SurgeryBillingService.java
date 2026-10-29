package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.Invoice;
import com.healthcare.hospital.entity.SurgeryBooking;
import com.healthcare.hospital.enums.PaymentStatus;
import com.healthcare.hospital.repository.InvoiceRepository;
import com.healthcare.hospital.repository.SurgeryBookingRepository;
import com.healthcare.hospital.repository.SurgeryConsumableUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class SurgeryBillingService {

    private final SurgeryBookingRepository bookingRepository;
    private final SurgeryConsumableUsageRepository consumableRepository;
    private final InvoiceRepository invoiceRepository;
    private final BillingService billingService;
    private final AuditEventService auditEventService;

    public SurgeryBillingService(SurgeryBookingRepository bookingRepository,
                                 SurgeryConsumableUsageRepository consumableRepository,
                                 InvoiceRepository invoiceRepository,
                                 BillingService billingService,
                                 AuditEventService auditEventService) {
        this.bookingRepository = bookingRepository;
        this.consumableRepository = consumableRepository;
        this.invoiceRepository = invoiceRepository;
        this.billingService = billingService;
        this.auditEventService = auditEventService;
    }

    /**
     * Calculates surgery charges entirely on backend:
     * - Theatre Fee: $150/hour based on expected or actual duration
     * - Procedure Base Fee: $500 routine, $800 urgent, $1200 emergency
     * - Surgeon Professional Fee: 3x doctor consultation fee or min $300
     * - Anesthesia Fee: $250
     * - Consumables Total: sum of all consumables recorded
     */
    public BigDecimal calculateSurgeryTotal(SurgeryBooking booking) {
        int durationMinutes = booking.getExpectedDurationMinutes() != null ? booking.getExpectedDurationMinutes() : 120;
        BigDecimal hours = BigDecimal.valueOf(durationMinutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal theatreRatePerHour = BigDecimal.valueOf(150.00);
        BigDecimal theatreFee = hours.multiply(theatreRatePerHour).setScale(2, RoundingMode.HALF_UP);

        BigDecimal procedureBaseFee;
        switch (booking.getPriority()) {
            case EMERGENCY -> procedureBaseFee = BigDecimal.valueOf(1200.00);
            case URGENT -> procedureBaseFee = BigDecimal.valueOf(800.00);
            default -> procedureBaseFee = BigDecimal.valueOf(500.00);
        }

        BigDecimal surgeonFee = BigDecimal.valueOf(300.00);
        if (booking.getLeadSurgeon() != null && booking.getLeadSurgeon().getConsultationFee() != null) {
            BigDecimal docFeeScaled = booking.getLeadSurgeon().getConsultationFee().multiply(BigDecimal.valueOf(3));
            surgeonFee = surgeonFee.max(docFeeScaled);
        }

        BigDecimal anesthesiaFee = BigDecimal.valueOf(250.00);

        BigDecimal consumablesTotal = consumableRepository.calculateTotalCostForSurgery(booking);
        if (consumablesTotal == null) {
            consumablesTotal = BigDecimal.ZERO;
        }

        return theatreFee.add(procedureBaseFee).add(surgeonFee).add(anesthesiaFee).add(consumablesTotal);
    }

    /**
     * Idempotently generates or synchronizes an invoice for the surgery booking.
     */
    @Transactional
    public Invoice generateSurgeryInvoice(Long surgeryId, String username) {
        SurgeryBooking booking = bookingRepository.findByIdWithLock(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        BigDecimal totalAmount = calculateSurgeryTotal(booking);
        BigDecimal tax = totalAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP); // 5% hospital tax
        BigDecimal finalTotal = totalAmount.add(tax);

        Invoice invoice = booking.getInvoice();
        if (invoice != null) {
            // Idempotent: Update existing invoice rather than creating duplicate invoices!
            invoice.setAmount(totalAmount);
            invoice.setTax(tax);
            invoice.setTotalAmount(finalTotal);
            invoice.setServiceDescription(String.format("Surgical Care: %s (%s) - Theatre, Surgeon, Anesthesia & Consumables",
                    booking.getProcedureName(), booking.getBookingNumber()));
            Invoice saved = invoiceRepository.save(invoice);
            auditEventService.record(username, "UPDATE_SURGERY_INVOICE", "Invoice", saved.getId(),
                    "Updated surgery invoice " + saved.getInvoiceNumber() + " for amount $" + finalTotal);
            return saved;
        }

        // Generate new invoice
        String invoiceNumber = billingService.generateNextInvoiceNumber();
        invoice = new Invoice(
                invoiceNumber,
                booking.getPatient(),
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                String.format("Surgical Care: %s (%s) - Theatre, Surgeon, Anesthesia & Consumables",
                        booking.getProcedureName(), booking.getBookingNumber()),
                totalAmount,
                tax,
                BigDecimal.ZERO,
                finalTotal,
                PaymentStatus.PENDING,
                null,
                null
        );

        Invoice saved = invoiceRepository.save(invoice);
        booking.setInvoice(saved);
        bookingRepository.save(booking);

        auditEventService.record(username, "GENERATE_SURGERY_INVOICE", "Invoice", saved.getId(),
                "Generated surgery invoice " + saved.getInvoiceNumber() + " ($" + finalTotal + ") for " + booking.getBookingNumber());

        return saved;
    }
}
