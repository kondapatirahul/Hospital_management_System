package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.Invoice;
import com.healthcare.hospital.enums.PaymentStatus;
import com.healthcare.hospital.repository.InvoiceRepository;
import com.healthcare.hospital.service.BillingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/billing")
@PreAuthorize("hasAnyRole('BILLING', 'ADMIN')")
public class BillingDashboardController {
    private final InvoiceRepository invoiceRepository;
    private final BillingService billingService;
    public BillingDashboardController(InvoiceRepository invoiceRepository, BillingService billingService) {
        this.invoiceRepository = invoiceRepository; this.billingService = billingService;
    }

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        var invoices = invoiceRepository.findAll();
        BigDecimal paid = invoices.stream().filter(i -> i.getStatus() == PaymentStatus.PAID).map(Invoice::getTotalAmount).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = invoices.stream().filter(i -> i.getStatus() == PaymentStatus.PENDING || i.getStatus() == PaymentStatus.PARTIAL).map(Invoice::getTotalAmount).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("invoices", invoices);
        model.addAttribute("paidTotal", paid);
        model.addAttribute("outstandingTotal", outstanding);
        model.addAttribute("pendingCount", invoices.stream().filter(i -> i.getStatus() == PaymentStatus.PENDING).count());
        return "billing/dashboard";
    }

    @PostMapping("/invoices/{id}/pay")
    public String markPaid(@PathVariable Long id, @RequestParam(defaultValue = "Cash") String paymentMethod, RedirectAttributes redirectAttributes) {
        if (!java.util.Set.of("Cash", "UPI", "Credit Card", "Insurance", "Bank Transfer").contains(paymentMethod)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Choose a supported payment method.");
            return "redirect:/billing/dashboard";
        }
        billingService.markInvoiceAsPaid(id, paymentMethod);
        redirectAttributes.addFlashAttribute("successMessage", "Invoice marked as paid.");
        return "redirect:/billing/dashboard";
    }
}
