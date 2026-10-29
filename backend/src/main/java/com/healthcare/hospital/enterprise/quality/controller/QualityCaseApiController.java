package com.healthcare.hospital.enterprise.quality.controller;

import com.healthcare.hospital.enterprise.quality.dto.CreateQualityReviewRequest;
import com.healthcare.hospital.enterprise.quality.dto.QualityReviewUpdateRequest;
import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.service.QualityReviewService;
import com.healthcare.hospital.enterprise.quality.service.QualityAnalyticsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JSON endpoints for internal dashboards and future integrations.
 * These routes use the same service methods and lifecycle policies as the server-rendered UI.
 */
@RestController
@RequestMapping("/api/quality/reviews")
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF')")
public class QualityCaseApiController {
    private final QualityReviewService service;
    private final QualityAnalyticsService analytics;

    public QualityCaseApiController(QualityReviewService service, QualityAnalyticsService analytics) {
        this.service=service;
        this.analytics=analytics;
    }

    @GetMapping
    public Map<String,Object> search(
        @RequestParam(required=false) String status,
        @RequestParam(required=false) String severity,
        @RequestParam(required=false) String category,
        @RequestParam(required=false) String owner,
        @RequestParam(required=false) LocalDate from,
        @RequestParam(required=false) LocalDate to,
        @RequestParam(required=false) String q,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="25") int size) {
        Page<QualityReview> result=service.search(status,severity,category,owner,from,to,q,page,size);
        Map<String,Object> body=new LinkedHashMap<>();
        body.put("items",result.getContent());
        body.put("page",result.getNumber());
        body.put("size",result.getSize());
        body.put("totalItems",result.getTotalElements());
        body.put("totalPages",result.getTotalPages());
        body.put("first",result.isFirst());
        body.put("last",result.isLast());
        return body;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        try {
            QualityReview review=service.get(id);
            return ResponseEntity.ok(review);
        } catch(java.util.NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("CASE_NOT_FOUND",ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateQualityReviewRequest request,Principal principal) {
        try {
            QualityReview review=service.create(request,actor(principal));
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error("VALIDATION_FAILED",ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/investigation")
    public ResponseEntity<?> update(@PathVariable long id,@Valid @RequestBody QualityReviewUpdateRequest request,
                                    Principal principal) {
        try {
            return ResponseEntity.ok(service.updateInvestigation(id,request,actor(principal)));
        } catch(java.util.NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("CASE_NOT_FOUND",ex.getMessage()));
        } catch(IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(error("UPDATE_REJECTED",ex.getMessage()));
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submit(@PathVariable long id,Principal principal) {
        return transition(() -> service.submit(id,actor(principal)));
    }

    @PostMapping("/{id}/triage")
    public ResponseEntity<?> triage(@PathVariable long id,@RequestParam String owner,
                                    @RequestParam(required=false) String reviewer,Principal principal) {
        return transition(() -> service.triage(id,owner,reviewer,actor(principal)));
    }

    @PostMapping("/{id}/start-investigation")
    public ResponseEntity<?> startInvestigation(@PathVariable long id,Principal principal) {
        return transition(() -> service.startInvestigation(id,actor(principal)));
    }

    @PostMapping("/{id}/request-actions")
    public ResponseEntity<?> requestActions(@PathVariable long id,Principal principal) {
        return transition(() -> service.requestActions(id,actor(principal)));
    }

    @PostMapping("/{id}/verification")
    public ResponseEntity<?> verification(@PathVariable long id,Principal principal) {
        return transition(() -> service.submitForVerification(id,actor(principal)));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> close(@PathVariable long id,@RequestParam String summary,Principal principal) {
        return transition(() -> service.close(id,summary,actor(principal)));
    }

    @PostMapping("/{id}/reopen")
    public ResponseEntity<?> reopen(@PathVariable long id,@RequestParam String reason,Principal principal) {
        return transition(() -> service.reopen(id,reason,actor(principal)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable long id,@RequestParam String reason,Principal principal) {
        return transition(() -> service.cancel(id,reason,actor(principal)));
    }

    @GetMapping("/metrics")
    public Map<String,Object> metrics() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("cards",service.dashboardMetrics());
        result.put("distribution",service.summary());
        result.put("analytics",analytics.dashboard());
        result.put("highRiskQueue",analytics.highRiskQueue());
        result.put("ownerWorkload",analytics.ownerWorkload());
        result.put("generatedAt",java.time.OffsetDateTime.now().toString());
        return result;
    }

    @GetMapping("/analytics")
    public Map<String,Object> analytics() {
        return analytics.dashboard();
    }

    @GetMapping("/analytics/high-risk")
    public Map<String,Object> highRisk() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("items",analytics.highRiskQueue());
        result.put("count",analytics.highRiskQueue().size());
        return result;
    }

    @GetMapping("/analytics/owners")
    public Map<String,Object> ownerWorkload() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("items",analytics.ownerWorkload());
        result.put("count",analytics.ownerWorkload().size());
        return result;
    }

    @GetMapping("/overdue")
    public Map<String,Object> overdue() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("items",service.overdueCases());
        result.put("count",service.overdueCases().size());
        result.put("asOf",LocalDate.now().toString());
        return result;
    }

    private ResponseEntity<?> transition(java.util.function.Supplier<QualityReview> operation) {
        try {
            return ResponseEntity.ok(operation.get());
        } catch(java.util.NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("CASE_NOT_FOUND",ex.getMessage()));
        } catch(IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(error("TRANSITION_REJECTED",ex.getMessage()));
        }
    }

    private Map<String,String> error(String code,String message) {
        Map<String,String> body=new LinkedHashMap<>();
        body.put("code",code);
        body.put("message",message==null?"Request failed.":message);
        return body;
    }

    private String actor(Principal principal) {
        return principal==null?"system":principal.getName();
    }
}
