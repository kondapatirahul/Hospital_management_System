package com.healthcare.hospital.enterprise.phase5.analyticssnapshot.controller;

import com.healthcare.hospital.enterprise.phase5.analyticssnapshot.entity.AnalyticsSnapshot;
import com.healthcare.hospital.enterprise.phase5.analyticssnapshot.service.AnalyticsSnapshotService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Controller
@RequestMapping("/enterprise/phase5/analyticssnapshot")
public class AnalyticsSnapshotController {
    private final AnalyticsSnapshotService service;
    public AnalyticsSnapshotController(AnalyticsSnapshotService service) { this.service=service; }
    @GetMapping public String index(@RequestParam(required=false) String q, @RequestParam(required=false) String status, @RequestParam(required=false) String priority, Model model) {
        model.addAttribute("pageTitle", "Analytics Snapshots"); model.addAttribute("records", q==null?service.filter(status,priority,null,null):service.search(q));
        model.addAttribute("summary",service.dashboard()); model.addAttribute("q",q); model.addAttribute("statusFilter",status); model.addAttribute("priorityFilter",priority); model.addAttribute("basePath","/enterprise/phase5/analyticssnapshot"); return "enterprise/phase5/workspace";
    }
    @GetMapping("/new") public String createForm(Model model) { model.addAttribute("pageTitle","Create Analytics snapshot"); model.addAttribute("record",new AnalyticsSnapshot()); model.addAttribute("basePath","/enterprise/phase5/analyticssnapshot"); return "enterprise/phase5/form"; }
    @PostMapping public String create(@Valid @ModelAttribute("record") AnalyticsSnapshot record, BindingResult errors, Principal principal, Model model, RedirectAttributes flash) {
        if(errors.hasErrors()) { model.addAttribute("pageTitle","Create Analytics snapshot"); model.addAttribute("basePath","/enterprise/phase5/analyticssnapshot"); return "enterprise/phase5/form"; }
        try { AnalyticsSnapshot saved=service.create(record, actor(principal)); flash.addFlashAttribute("successMessage","Analytics snapshot created successfully"); return "redirect:/enterprise/phase5/analyticssnapshot/"+saved.getId(); }
        catch(RuntimeException ex) { model.addAttribute("pageTitle","Create Analytics snapshot"); model.addAttribute("basePath","/enterprise/phase5/analyticssnapshot"); model.addAttribute("errorMessage",ex.getMessage()); return "enterprise/phase5/form"; }
    }
    @GetMapping("/{id}") public String detail(@PathVariable long id, Model model) { model.addAttribute("record",service.find(id).orElseThrow()); model.addAttribute("pageTitle","Analytics snapshot details"); model.addAttribute("basePath","/enterprise/phase5/analyticssnapshot"); return "enterprise/phase5/detail"; }
    @PostMapping("/{id}/transition") public String transition(@PathVariable long id, @RequestParam String status, @RequestParam(required=false) String note, Principal principal, RedirectAttributes flash) {
        try { service.transition(id,status,note,actor(principal)); flash.addFlashAttribute("successMessage","Status updated"); } catch(RuntimeException ex) { flash.addFlashAttribute("errorMessage",ex.getMessage()); } return "redirect:/enterprise/phase5/analyticssnapshot/"+id;
    }
    @PostMapping("/{id}/assign") public String assign(@PathVariable long id, @RequestParam String owner, @RequestParam(required=false) String department, Principal principal, RedirectAttributes flash) {
        try { service.assign(id,owner,department,actor(principal)); flash.addFlashAttribute("successMessage","Assignment saved"); } catch(RuntimeException ex) { flash.addFlashAttribute("errorMessage",ex.getMessage()); } return "redirect:/enterprise/phase5/analyticssnapshot/"+id;
    }
    @PostMapping("/{id}/note") public String note(@PathVariable long id, @RequestParam String note, Principal principal, RedirectAttributes flash) {
        try { service.addNote(id,note,actor(principal)); flash.addFlashAttribute("successMessage","Note added"); } catch(RuntimeException ex) { flash.addFlashAttribute("errorMessage",ex.getMessage()); } return "redirect:/enterprise/phase5/analyticssnapshot/"+id;
    }
    @GetMapping("/export.csv") public ResponseEntity<byte[]> export() { byte[] bytes=service.exportCsv().getBytes(StandardCharsets.UTF_8); return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=analyticssnapshot.csv").contentType(new MediaType("text","csv",StandardCharsets.UTF_8)).body(bytes); }
    private static String actor(Principal p) { return p==null?"authenticated-user":p.getName(); }
}
