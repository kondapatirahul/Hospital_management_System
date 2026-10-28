package com.healthcare.hospital.enterprise.pharmacy.controller;

import com.healthcare.hospital.enterprise.pharmacy.entity.PurchaseApproval;
import com.healthcare.hospital.enterprise.pharmacy.service.PurchaseApprovalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

/**
 * Web workflow for purchase approval workflow.
 * The shared security configuration continues to control access to /pharmacy/enterprise/**.
 */
@Controller
@RequestMapping("/pharmacy/enterprise/purchaseApproval")
public class PurchaseApprovalController {
    private final PurchaseApprovalService service;

    public PurchaseApprovalController(PurchaseApprovalService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required=false) String status,
                        @RequestParam(required=false) String assignee, Model model) {
        model.addAttribute("records", service.list(status, assignee));
        model.addAttribute("overdueRecords", service.overdue());
        model.addAttribute("totalCount", service.countAll());
        model.addAttribute("openCount", service.countOpen());
        model.addAttribute("completedCount", service.countCompleted());
        model.addAttribute("overdueCount", service.countOverdue());
        model.addAttribute("recordType", "PurchaseApproval");
        model.addAttribute("statusFilter", status);
        model.addAttribute("assigneeFilter", assignee);
        return "enterprise/pharmacy/purchaseApproval";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", new PurchaseApproval());
        model.addAttribute("formAction", "/pharmacy/enterprise/purchaseApproval");
        return "enterprise/pharmacy/purchaseApproval-form";
    }

    @PostMapping
    public String create(@ModelAttribute PurchaseApproval record, Principal principal, RedirectAttributes flash) {
        try {
            service.create(record, principal == null ? "system" : principal.getName());
            flash.addFlashAttribute("successMessage", "Record created successfully.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/pharmacy/enterprise/purchaseApproval";
        }
        return "redirect:/pharmacy/enterprise/purchaseApproval";
    }

    @PostMapping("/<built-in function id>/start")
    public String start(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.start(id, principal == null ? "system" : principal.getName()), flash, "Record started.");
    }

    @PostMapping("/<built-in function id>/complete")
    public String complete(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.complete(id, principal == null ? "system" : principal.getName()), flash, "Record completed.");
    }

    @PostMapping("/<built-in function id>/cancel")
    public String cancel(@PathVariable Long id, @RequestParam String reason, Principal principal, RedirectAttributes flash) {
        return transition(() -> service.cancel(id, principal == null ? "system" : principal.getName(), reason), flash, "Record cancelled.");
    }

    private String transition(Runnable action, RedirectAttributes flash, String message) {
        try {
            action.run();
            flash.addFlashAttribute("successMessage", message);
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pharmacy/enterprise/purchaseApproval";
    }
}
