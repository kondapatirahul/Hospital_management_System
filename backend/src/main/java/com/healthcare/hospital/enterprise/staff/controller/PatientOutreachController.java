package com.healthcare.hospital.enterprise.staff.controller;

import com.healthcare.hospital.enterprise.staff.entity.DischargeChecklist;
import com.healthcare.hospital.enterprise.staff.entity.PatientOutreach;
import com.healthcare.hospital.enterprise.staff.repository.DischargeChecklistRepository;
import com.healthcare.hospital.enterprise.staff.service.PatientOutreachService;
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
import java.util.List;

/** Role-guarded post-discharge outreach UI and workflow endpoints. */
@Controller
@RequestMapping("/staff/enterprise/patient-outreach")
@PreAuthorize("hasAnyRole('ADMIN','NURSE','STAFF')")
public class PatientOutreachController {
    private final PatientOutreachService service;
    private final DischargeChecklistRepository checklists;
    public PatientOutreachController(PatientOutreachService service,DischargeChecklistRepository checklists){this.service=service;this.checklists=checklists;}

    @GetMapping public String index(@RequestParam(required=false) String status,@RequestParam(required=false) String team,@RequestParam(required=false) String priority,@RequestParam(required=false) String escalation,@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,@RequestParam(required=false) String q,Model model){
        try{model.addAttribute("items",service.search(status,team,priority,escalation,from,to,q));}catch(RuntimeException ex){model.addAttribute("items",service.all());model.addAttribute("errorMessage",ex.getMessage());}
        model.addAttribute("metrics",service.metrics());model.addAttribute("filterStatus",safe(status));model.addAttribute("filterTeam",safe(team));model.addAttribute("filterPriority",safe(priority));model.addAttribute("filterEscalation",safe(escalation));model.addAttribute("filterFrom",from);model.addAttribute("filterTo",to);model.addAttribute("filterQuery",safe(q));model.addAttribute("statuses",List.of("OPEN","IN_PROGRESS","WAITING_FOR_PATIENT","WAITING_FOR_CLINICIAN","CLOSED","CANCELLED"));model.addAttribute("teams",teams());model.addAttribute("priorities",List.of("LOW","NORMAL","HIGH","URGENT"));model.addAttribute("escalations",List.of("NONE","ROUTINE","PRIORITY","URGENT","EMERGENCY"));model.addAttribute("today",LocalDate.now());return "enterprise/staff/outreach/index";
    }

    @GetMapping("/new") @PreAuthorize("hasAnyRole('ADMIN','NURSE','STAFF')")
    public String newForm(@RequestParam(required=false) Long checklistId,Model model){List<DischargeChecklist> eligible=checklists.findTop100ByOrderByCreatedAtDesc().stream().filter(c->"COMPLETED".equalsIgnoreCase(c.getStatus())).toList();model.addAttribute("eligibleChecklists",eligible);model.addAttribute("checklistId",checklistId==null?"":checklistId);model.addAttribute("teams",teams());model.addAttribute("channels",List.of("PHONE","SMS","PORTAL","VIDEO","CAREGIVER","IN_PERSON"));model.addAttribute("priorities",List.of("LOW","NORMAL","HIGH","URGENT"));model.addAttribute("today",LocalDate.now());return "enterprise/staff/outreach/new";}

    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','NURSE','STAFF')")
    public String create(@RequestParam long checklistId,@RequestParam String patientReference,@RequestParam String patientName,@RequestParam String purpose,@RequestParam(defaultValue="PHONE") String channel,@RequestParam(defaultValue="NORMAL") String priority,@RequestParam String assignedTeam,@RequestParam(required=false) String assignedTo,@RequestParam(required=false) LocalDate nextContactDate,@RequestParam(required=false) String contactNumber,@RequestParam(required=false) String preferredWindow,@RequestParam(defaultValue="false") boolean consent,@RequestParam(defaultValue="false") boolean interpreter,Principal principal,RedirectAttributes flash){
        try{PatientOutreach item=service.create(checklistId,patientReference,patientName,purpose,channel,priority,assignedTeam,assignedTo,nextContactDate,contactNumber,preferredWindow,consent,interpreter,actor(principal));flash.addFlashAttribute("successMessage","Outreach "+item.getReferenceCode()+" created.");return "redirect:/staff/enterprise/patient-outreach/"+item.getId();}
        catch(RuntimeException ex){flash.addFlashAttribute("errorMessage",ex.getMessage());return "redirect:/staff/enterprise/patient-outreach/new?checklistId="+checklistId;}
    }

    @GetMapping("/{id}") public String detail(@PathVariable long id,Model model){PatientOutreach item=service.get(id);model.addAttribute("item",item);model.addAttribute("patientHistory",service.patientHistory(item.getPatientReference()));model.addAttribute("safetyFlags",safetyFlags(item));model.addAttribute("today",LocalDate.now());model.addAttribute("teams",teams());model.addAttribute("priorities",List.of("LOW","NORMAL","HIGH","URGENT"));model.addAttribute("escalations",List.of("ROUTINE","PRIORITY","URGENT","EMERGENCY"));return "enterprise/staff/outreach/detail";}

    @PostMapping("/{id}/attempt") public String attempt(@PathVariable long id,@RequestParam String outcome,@RequestParam(required=false) String summary,@RequestParam(required=false) String symptoms,@RequestParam(required=false) String medication,@RequestParam(required=false) String wound,@RequestParam(required=false) String mobility,@RequestParam(required=false) String social,@RequestParam(required=false) String questions,@RequestParam(required=false) String advice,@RequestParam(required=false) LocalDate nextDate,@RequestParam(defaultValue="false") boolean identity,@RequestParam(defaultValue="false") boolean caregiver,@RequestParam(defaultValue="false") boolean appointment,@RequestParam(defaultValue="false") boolean warningSigns,@RequestParam(defaultValue="false") boolean redFlag,@RequestParam(defaultValue="false") boolean breathing,@RequestParam(defaultValue="false") boolean chestPain,@RequestParam(defaultValue="false") boolean neurological,@RequestParam(defaultValue="false") boolean pain,@RequestParam(defaultValue="false") boolean medicationAccess,Principal principal,RedirectAttributes flash){return action(id,flash,()->service.recordAttempt(id,outcome,summary,symptoms,medication,wound,mobility,social,questions,advice,nextDate,identity,caregiver,appointment,warningSigns,redFlag,breathing,chestPain,neurological,pain,medicationAccess,actor(principal)),"Contact attempt saved. Review any safety banner before continuing.");}
    @PostMapping("/{id}/assign") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')") public String assign(@PathVariable long id,@RequestParam String assignedTeam,@RequestParam(required=false) String assignedTo,@RequestParam String priority,Principal p,RedirectAttributes f){return action(id,f,()->service.assign(id,assignedTeam,assignedTo,priority,actor(p)),"Assignment updated.");}
    @PostMapping("/{id}/escalate") public String escalate(@PathVariable long id,@RequestParam String level,@RequestParam String reason,@RequestParam(defaultValue="false") boolean clinician,@RequestParam(defaultValue="false") boolean supervisor,Principal p,RedirectAttributes f){return action(id,f,()->service.escalate(id,level,reason,clinician,supervisor,actor(p)),"Escalation recorded.");}
    @PostMapping("/{id}/acknowledge") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')") public String acknowledge(@PathVariable long id,@RequestParam String note,Principal p,RedirectAttributes f){return action(id,f,()->service.acknowledgeHandoff(id,note,actor(p)),"Clinical handoff acknowledgement recorded.");}
    @PostMapping("/{id}/resolve-safety") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')") public String resolveSafety(@PathVariable long id,@RequestParam String note,Principal p,RedirectAttributes f){return action(id,f,()->service.resolveSafety(id,note,actor(p)),"Safety resolution recorded.");}
    @PostMapping("/{id}/close") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')") public String close(@PathVariable long id,@RequestParam String notes,Principal p,RedirectAttributes f){return action(id,f,()->service.close(id,notes,actor(p)),"Outreach record closed.");}
    @PostMapping("/{id}/reopen") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')") public String reopen(@PathVariable long id,@RequestParam String reason,Principal p,RedirectAttributes f){return action(id,f,()->service.reopen(id,reason,actor(p)),"Outreach reopened.");}
    @PostMapping("/{id}/cancel") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')") public String cancel(@PathVariable long id,@RequestParam String reason,Principal p,RedirectAttributes f){return action(id,f,()->service.cancel(id,reason,actor(p)),"Outreach cancelled.");}

    @GetMapping("/safety-queue") public String safety(Model model){model.addAttribute("items",service.safetyQueue());model.addAttribute("metrics",service.metrics());return "enterprise/staff/outreach/safety-queue";}
    @GetMapping("/overdue") public String overdue(Model model){model.addAttribute("items",service.overdue());model.addAttribute("metrics",service.metrics());return "enterprise/staff/outreach/overdue";}
    @GetMapping("/analytics") public String analytics(Model model){model.addAttribute("metrics",service.metrics());model.addAttribute("statuses",service.statusCounts());model.addAttribute("outcomes",service.outcomeCounts());model.addAttribute("workload",service.teamWorkload());model.addAttribute("queue",service.safetyQueue().stream().limit(8).toList());return "enterprise/staff/outreach/analytics";}
    @GetMapping(value="/export.csv",produces="text/csv") public ResponseEntity<String> export(@RequestParam(required=false) String status,@RequestParam(required=false) String team,@RequestParam(required=false) String priority,@RequestParam(required=false) String escalation,@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,@RequestParam(required=false) String q){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=careflow-patient-outreach.csv").contentType(MediaType.parseMediaType("text/csv; charset=UTF-8")).body(service.exportCsv(service.search(status,team,priority,escalation,from,to,q)));}

    private String action(long id,RedirectAttributes flash,Runnable work,String message){try{work.run();flash.addFlashAttribute("successMessage",message);}catch(RuntimeException ex){flash.addFlashAttribute("errorMessage",ex.getMessage());}return "redirect:/staff/enterprise/patient-outreach/"+id;}
    private List<String> safetyFlags(PatientOutreach i){java.util.ArrayList<String> f=new java.util.ArrayList<>();if(i.isRedFlagSymptoms())f.add("Red-flag symptoms reported — clinical review required.");if(i.isBreathingConcern())f.add("Breathing concern reported.");if(i.isChestPainConcern())f.add("Chest pain concern reported.");if(i.isNeurologicalConcern())f.add("Neurological concern reported.");if(i.isUncontrolledPain())f.add("Uncontrolled pain reported.");if(i.isMedicationAccessConcern())f.add("Medication access issue requires review.");if(i.isClinicianHandoffRequired())f.add("Clinician handoff awaiting acknowledgement.");if(i.isSupervisorReviewRequired())f.add("Supervisor review awaiting acknowledgement.");return f;}
    private static List<String> teams(){return List.of("Clinical","Nursing","Pharmacy","Reception","Home Care","Social Work","Care Coordination","Other");}
    private static String actor(Principal p){return p==null?"authenticated-user":p.getName();}private static String safe(String s){return s==null?"":s;}
}
