package com.healthcare.hospital.controller;

import com.healthcare.hospital.repository.InvoiceRepository;
import com.healthcare.hospital.service.PaymentRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequestMapping("/billing/payments")
@PreAuthorize("hasAnyRole('BILLING', 'ADMIN')")
public class PaymentRecordController {
    private final PaymentRecordService paymentService;
    private final InvoiceRepository invoiceRepository;

    public PaymentRecordController(PaymentRecordService paymentService, InvoiceRepository invoiceRepository) {
        this.paymentService = paymentService;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("payments", paymentService.recent());
        model.addAttribute("invoices", invoiceRepository.findAll());
        return "billing/payments";
    }

    @PostMapping
    public String record(@RequestParam Long invoiceId, @RequestParam BigDecimal amount,
                         @RequestParam String paymentMethod,
                         @RequestParam(required = false) String transactionReference,
                         @RequestParam(required = false) String notes,
                         Principal principal, RedirectAttributes flash) {
        try {
            paymentService.record(invoiceId, amount, paymentMethod, transactionReference,
                    principal == null ? "billing-user" : principal.getName(), notes);
            flash.addFlashAttribute("successMessage", "Payment recorded and receipt generated.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/billing/payments";
    }
}
