package com.healthcare.hospital.enterprise.quality.controller;

import com.healthcare.hospital.enterprise.quality.dto.CreateQualityReviewRequest;
import com.healthcare.hospital.enterprise.quality.dto.CreateQualityActionRequest;
import com.healthcare.hospital.enterprise.quality.dto.CreateQualityCommentRequest;
import com.healthcare.hospital.enterprise.quality.dto.QualityReviewUpdateRequest;
import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

/** Role-protected clinical quality case management screens. */
@Controller
@RequestMapping("/quality/reviews")
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF')")
public class QualityReviewController {
    private final QualityReviewService reviews;
    private final QualityActionService actions;
    private final QualityAuditService audit;
    private final QualityExportService exports;

    public QualityReviewController(QualityReviewService reviews, QualityActionService actions,
                                   QualityAuditService audit, QualityExportService exports) {
        this.reviews=reviews; this.actions=actions; this.audit=audit; this.exports=exports;
    }

    @GetMapping
    public String index(@RequestParam(required=false) String status,
        @RequestParam(required=false) String severity, @RequestParam(required=false) String category,
        @RequestParam(required=false) String owner, @RequestParam(required=false) LocalDate from,
        @RequestParam(required=false) LocalDate to, @RequestParam(required=false) String q,
        @RequestParam(defaultValue="0") int page, Model model) {
        Page<QualityReview> result=reviews.search(status,severity,category,owner,from,to,q,page,25);
        model.addAttribute("results",result);
        model.addAttribute("items",result.getContent());
        model.addAttribute("metrics",reviews.dashboardMetrics());
        model.addAttribute("summary",reviews.summary());
        model.addAttribute("filterStatus",status); model.addAttribute("filterSeverity",severity);
        model.addAttribute("filterCategory",category); model.addAttribute("filterOwner",owner);
        model.addAttribute("filterFrom",from); model.addAttribute("filterTo",to); model.addAttribute("filterQuery",q);
        model.addAttribute("statuses",QualityWorkflowPolicy.REVIEW_STATES);
        model.addAttribute("severities",QualityWorkflowPolicy.SEVERITIES);
        model.addAttribute("categories",QualityWorkflowPolicy.CATEGORIES.stream().sorted().toList());
        return "enterprise/quality/index";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("request",new CreateQualityReviewRequest());
        model.addAttribute("categories",QualityWorkflowPolicy.CATEGORIES.stream().sorted().toList());
        model.addAttribute("severities",QualityWorkflowPolicy.SEVERITIES);
        return "enterprise/quality/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("request") CreateQualityReviewRequest request,
                         Principal principal, RedirectAttributes flash) {
        try {
            QualityReview saved=reviews.create(request,actor(principal));
            flash.addFlashAttribute("successMessage","Quality case "+saved.getCaseReference()+" created.");
            return "redirect:/quality/reviews/"+saved.getId();
        } catch(RuntimeException ex) {
            flash.addFlashAttribute("errorMessage",message(ex));
            return "redirect:/quality/reviews/new";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id,Model model) {
        QualityReview item=reviews.get(id);
        model.addAttribute("item",item);
        model.addAttribute("actions",actions.forReview(id));
        model.addAttribute("comments",reviews.comments(id));
        model.addAttribute("auditEvents",audit.forCase(item.getCaseReference()));
        model.addAttribute("openActionCount",actions.openActionCount(id));
        model.addAttribute("severityGuidance",new QualityWorkflowPolicy().severityGuidance(item.getSeverity()));
        model.addAttribute("actionTypes",QualityWorkflowPolicy.ACTION_TYPES);
        model.addAttribute("actionPriorities",QualityWorkflowPolicy.ACTION_PRIORITIES);
        return "enterprise/quality/detail";
    }

    @PostMapping("/{id}/investigation")
    public String updateInvestigation(@PathVariable long id,@ModelAttribute QualityReviewUpdateRequest request,
                                      Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.updateInvestigation(id,request,actor(principal)),"Investigation updated.");
    }

    @PostMapping("/{id}/submit")
    public String submit(@PathVariable long id,Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.submit(id,actor(principal)),"Case submitted for triage.");
    }

    @PostMapping("/{id}/triage")
    public String triage(@PathVariable long id,@RequestParam String owner,@RequestParam(required=false) String reviewer,
                         Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.triage(id,owner,reviewer,actor(principal)),"Case triaged.");
    }

    @PostMapping("/{id}/start-investigation")
    public String startInvestigation(@PathVariable long id,Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.startInvestigation(id,actor(principal)),"Investigation started.");
    }

    @PostMapping("/{id}/request-actions")
    public String requestActions(@PathVariable long id,Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.requestActions(id,actor(principal)),"Case moved to action required.");
    }

    @PostMapping("/{id}/verification")
    public String verification(@PathVariable long id,Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.submitForVerification(id,actor(principal)),"Submitted for verification.");
    }

    @PostMapping("/{id}/close")
    public String close(@PathVariable long id,@RequestParam String closureSummary,Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.close(id,closureSummary,actor(principal)),"Case closed.");
    }

    @PostMapping("/{id}/reopen")
    public String reopen(@PathVariable long id,@RequestParam String reason,Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.reopen(id,reason,actor(principal)),"Case reopened.");
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable long id,@RequestParam String reason,Principal principal,RedirectAttributes flash) {
        return execute(id,flash,()->reviews.cancel(id,reason,actor(principal)),"Case cancelled.");
    }

    @PostMapping("/{id}/actions")
    public String addAction(@PathVariable long id,@Valid @ModelAttribute CreateQualityActionRequest request,
                            Principal principal,RedirectAttributes flash) {
        try { actions.create(id,request,actor(principal)); flash.addFlashAttribute("successMessage","Action created."); }
        catch(RuntimeException ex) { flash.addFlashAttribute("errorMessage",message(ex)); }
        return "redirect:/quality/reviews/"+id;
    }

    @PostMapping("/actions/{actionId}/start")
    public String startAction(@PathVariable long actionId,Principal principal,RedirectAttributes flash) {
        return actionExecute(actionId,flash,()->actions.start(actionId,actor(principal)),"Action started.");
    }

    @PostMapping("/actions/{actionId}/block")
    public String blockAction(@PathVariable long actionId,@RequestParam String reason,Principal principal,RedirectAttributes flash) {
        return actionExecute(actionId,flash,()->actions.block(actionId,reason,actor(principal)),"Action blocked.");
    }

    @PostMapping("/actions/{actionId}/unblock")
    public String unblockAction(@PathVariable long actionId,Principal principal,RedirectAttributes flash) {
        return actionExecute(actionId,flash,()->actions.unblock(actionId,actor(principal)),"Action unblocked.");
    }

    @PostMapping("/actions/{actionId}/complete")
    public String completeAction(@PathVariable long actionId,@RequestParam String evidence,Principal principal,RedirectAttributes flash) {
        return actionExecute(actionId,flash,()->actions.submitForVerification(actionId,evidence,actor(principal)),"Evidence submitted.");
    }

    @PostMapping("/actions/{actionId}/verify")
    public String verifyAction(@PathVariable long actionId,@RequestParam String notes,Principal principal,RedirectAttributes flash) {
        return actionExecute(actionId,flash,()->actions.verify(actionId,notes,actor(principal)),"Action verified.");
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable long id,@Valid @ModelAttribute CreateQualityCommentRequest request,
                             Principal principal,RedirectAttributes flash) {
        try {
            reviews.addComment(id,request.getBody(),request.getVisibility(),request.getCommentType(),
                request.isActionRequired(),request.getAssignedTo(),request.getFollowUpDate(),actor(principal));
            flash.addFlashAttribute("successMessage","Case note added.");
        } catch(RuntimeException ex) { flash.addFlashAttribute("errorMessage",message(ex)); }
        return "redirect:/quality/reviews/"+id;
    }

    @GetMapping(value="/export.csv",produces="text/csv")
    public ResponseEntity<String> export() {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=careflow-quality-register.csv")
            .contentType(MediaType.parseMediaType("text/csv")).body(exports.exportCurrentRegister());
    }

    @GetMapping(value="/export-overdue.csv",produces="text/csv")
    public ResponseEntity<String> exportOverdue() {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=careflow-quality-overdue.csv")
            .contentType(MediaType.parseMediaType("text/csv")).body(exports.exportOverdueRegister());
    }

    private String execute(long id,RedirectAttributes flash,Runnable operation,String success) {
        try { operation.run(); flash.addFlashAttribute("successMessage",success); }
        catch(RuntimeException ex) { flash.addFlashAttribute("errorMessage",message(ex)); }
        return "redirect:/quality/reviews/"+id;
    }

    private String actionExecute(long actionId,RedirectAttributes flash,Runnable operation,String success) {
        try {
            long reviewId=actions.requireAction(actionId).getReview().getId();
            operation.run(); flash.addFlashAttribute("successMessage",success);
            return "redirect:/quality/reviews/"+reviewId;
        } catch(RuntimeException ex) {
            flash.addFlashAttribute("errorMessage",message(ex));
            return "redirect:/quality/reviews";
        }
    }

    private String actor(Principal principal) { return principal==null?"system":principal.getName(); }
    private String message(RuntimeException ex) {
        if(ex instanceof NoSuchElementException) return ex.getMessage();
        return ex.getMessage()==null?"The operation could not be completed.":ex.getMessage();
    }
}
