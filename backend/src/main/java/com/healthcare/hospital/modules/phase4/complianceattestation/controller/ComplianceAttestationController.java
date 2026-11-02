package com.healthcare.hospital.modules.phase4.complianceattestation.controller;
import com.healthcare.hospital.modules.phase4.complianceattestation.entity.ComplianceAttestation;
import com.healthcare.hospital.modules.phase4.complianceattestation.service.ComplianceAttestationService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDate;
@Controller
@RequestMapping("/enterprise/phase4/complianceattestation")
public class ComplianceAttestationController {
 private final ComplianceAttestationService service;
 public ComplianceAttestationController(ComplianceAttestationService service){this.service=service;}
 @GetMapping public String index(@RequestParam(required=false) String q,@RequestParam(required=false) String status,Model model){
  model.addAttribute("pageTitle","Compliance attestations"); model.addAttribute("records",q!=null?service.search(q):(status!=null?service.byStatus(status):service.list()));
  model.addAttribute("dashboard",service.dashboard()); model.addAttribute("query",q); model.addAttribute("selectedStatus",status); model.addAttribute("statuses",new String[]{"DRAFT", "SUBMITTED", "REVIEW", "ACCEPTED", "REJECTED", "EXPIRED"}); return "enterprise/phase4/complianceattestation/index";
 }
 @GetMapping("/new") public String createForm(Model model){model.addAttribute("record",new ComplianceAttestation());model.addAttribute("pageTitle","Create Compliance attestations");return "enterprise/phase4/complianceattestation/form";}
 @PostMapping public String create(@Valid @ModelAttribute("record") ComplianceAttestation record,BindingResult errors,Principal principal,RedirectAttributes flash,Model model){
  if(errors.hasErrors()){model.addAttribute("pageTitle","Create Compliance attestations");return "enterprise/phase4/complianceattestation/form";}
  try{service.create(record,principal==null?"system":principal.getName());flash.addFlashAttribute("successMessage","Record created successfully");return "redirect:/enterprise/phase4/complianceattestation";}
  catch(RuntimeException ex){model.addAttribute("pageTitle","Create Compliance attestations");model.addAttribute("errorMessage",ex.getMessage());return "enterprise/phase4/complianceattestation/form";}
 }
 @PostMapping("/{id}/transition") public String transition(@PathVariable long id,@RequestParam String status,@RequestParam(required=false) String note,Principal principal,RedirectAttributes flash){
  try{service.transition(id,status,note,principal==null?"system":principal.getName());flash.addFlashAttribute("successMessage","Workflow status updated");}
  catch(RuntimeException ex){flash.addFlashAttribute("errorMessage",ex.getMessage());} return "redirect:/enterprise/phase4/complianceattestation";
 }
 @PostMapping("/{id}/assign") public String assign(@PathVariable long id,@RequestParam String owner,@RequestParam(required=false) String department,Principal principal,RedirectAttributes flash){
  try{service.assign(id,owner,department,principal==null?"system":principal.getName());flash.addFlashAttribute("successMessage","Owner updated");}
  catch(RuntimeException ex){flash.addFlashAttribute("errorMessage",ex.getMessage());} return "redirect:/enterprise/phase4/complianceattestation";
 }
 @PostMapping("/{id}/evidence") public String evidence(@PathVariable long id,@RequestParam String evidence,Principal principal,RedirectAttributes flash){
  try{service.addEvidence(id,evidence,principal==null?"system":principal.getName());flash.addFlashAttribute("successMessage","Evidence reference saved");}
  catch(RuntimeException ex){flash.addFlashAttribute("errorMessage",ex.getMessage());} return "redirect:/enterprise/phase4/complianceattestation";
 }
 @GetMapping("/export.csv") public ResponseEntity<byte[]> export(){byte[] body=service.exportCsv().getBytes(java.nio.charset.StandardCharsets.UTF_8);return ResponseEntity.ok().contentType(new MediaType("text","csv",java.nio.charset.StandardCharsets.UTF_8)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=complianceattestation-export.csv").body(body);}
}
