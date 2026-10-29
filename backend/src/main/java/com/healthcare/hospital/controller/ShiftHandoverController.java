package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.ShiftHandoverForm;
import com.healthcare.hospital.service.ShiftHandoverService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/staff/handovers")
@PreAuthorize("hasAnyRole('ADMIN','STAFF','NURSE','DOCTOR','PHARMACY','LABORATORY')")
public class ShiftHandoverController {
    private final ShiftHandoverService handovers;
    public ShiftHandoverController(ShiftHandoverService handovers){this.handovers=handovers;}

    @GetMapping public String index(@RequestParam(required=false) String status,
        @RequestParam(required=false) String department,@RequestParam(required=false) String q,Model model){
        model.addAttribute("handovers",handovers.search(status,department,q)); model.addAttribute("metrics",handovers.metrics());
        model.addAttribute("filterStatus",status==null?"":status); model.addAttribute("filterDepartment",department==null?"":department);
        model.addAttribute("filterQuery",q==null?"":q);
        model.addAttribute("departments",List.of("Emergency","Outpatient","Inpatient","Pharmacy","Laboratory","Radiology","Billing","Facilities","Other"));
        return "staff/handovers";
    }
    @GetMapping("/new") public String createForm(Model model){if(!model.containsAttribute("form"))model.addAttribute("form",new ShiftHandoverForm());
        model.addAttribute("departments",List.of("Emergency","Outpatient","Inpatient","Pharmacy","Laboratory","Radiology","Billing","Facilities","Other"));
        model.addAttribute("shifts",List.of("Morning","Afternoon","Night","On-call"));return "staff/handover-form";}
    @PostMapping public String create(@Valid @ModelAttribute("form") ShiftHandoverForm form,BindingResult binding,RedirectAttributes flash,Model model){
        if(binding.hasErrors()){model.addAttribute("departments",List.of("Emergency","Outpatient","Inpatient","Pharmacy","Laboratory","Radiology","Billing","Facilities","Other"));model.addAttribute("shifts",List.of("Morning","Afternoon","Night","On-call"));return "staff/handover-form";}
        try{var saved=handovers.create(form);flash.addFlashAttribute("successMessage","Handover "+saved.getReferenceCode()+" saved as draft.");return "redirect:/staff/handovers/"+saved.getId();}
        catch(RuntimeException ex){flash.addFlashAttribute("errorMessage",ex.getMessage());return "redirect:/staff/handovers/new";}}
    @GetMapping("/{id}") public String detail(@PathVariable long id,Model model,RedirectAttributes flash){var found=handovers.find(id);if(found.isEmpty()){flash.addFlashAttribute("errorMessage","Handover not found.");return "redirect:/staff/handovers";}model.addAttribute("handover",found.get());return "staff/handover-detail";}
    @PostMapping("/{id}/submit") public String submit(@PathVariable long id,RedirectAttributes flash){return act(id,flash,()->handovers.submit(id),"Handover submitted to the incoming team.");}
    @PostMapping("/{id}/acknowledge") public String acknowledge(@PathVariable long id,@RequestParam(required=false) String comment,Principal principal,RedirectAttributes flash){
        return act(id,flash,()->handovers.acknowledge(id,actor(principal),comment),"Handover acknowledged.");}
    @PostMapping("/{id}/return") public String returnForCorrection(@PathVariable long id,@RequestParam String reason,Principal principal,RedirectAttributes flash){
        return act(id,flash,()->handovers.returnForCorrection(id,actor(principal),reason),"Handover returned with feedback.");}
    private String act(long id,RedirectAttributes flash,Runnable action,String success){try{action.run();flash.addFlashAttribute("successMessage",success);}catch(RuntimeException ex){flash.addFlashAttribute("errorMessage",ex.getMessage());}return "redirect:/staff/handovers/"+id;}
    private static String actor(Principal p){return p==null?"authenticated-user":p.getName();}
}
