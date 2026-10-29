package com.healthcare.hospital.controller;

import com.healthcare.hospital.dto.*;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.*;
import com.healthcare.hospital.repository.*;
import com.healthcare.hospital.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/ot")
public class OperationTheatreController {

    private final OperationTheatreService theatreService;
    private final SurgerySchedulingService schedulingService;
    private final SurgicalTeamService teamService;
    private final SurgeryWorkflowService workflowService;
    private final SurgeryInventoryService inventoryService;
    private final SurgeryBillingService billingService;
    private final SurgeryReportService reportService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final WardBedRepository wardBedRepository;
    private final MedicineRepository medicineRepository;

    public OperationTheatreController(OperationTheatreService theatreService,
                                      SurgerySchedulingService schedulingService,
                                      SurgicalTeamService teamService,
                                      SurgeryWorkflowService workflowService,
                                      SurgeryInventoryService inventoryService,
                                      SurgeryBillingService billingService,
                                      SurgeryReportService reportService,
                                      PatientRepository patientRepository,
                                      DoctorRepository doctorRepository,
                                      UserRepository userRepository,
                                      WardBedRepository wardBedRepository,
                                      MedicineRepository medicineRepository) {
        this.theatreService = theatreService;
        this.schedulingService = schedulingService;
        this.teamService = teamService;
        this.workflowService = workflowService;
        this.inventoryService = inventoryService;
        this.billingService = billingService;
        this.reportService = reportService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.wardBedRepository = wardBedRepository;
        this.medicineRepository = medicineRepository;
    }

    @GetMapping
    public String index() {
        return "redirect:/ot/schedule";
    }

    /**
     * OT Schedule view with daily / weekly agenda, filtering, and quick booking access.
     */
    @GetMapping("/schedule")
    public String scheduleView(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "view", defaultValue = "daily") String view,
            @RequestParam(value = "theatreId", required = false) Long theatreId,
            @RequestParam(value = "surgeonId", required = false) Long surgeonId,
            @RequestParam(value = "status", required = false) SurgeryStatus status,
            @RequestParam(value = "priority", required = false) SurgeryPriority priority,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        LocalDate selectedDate = date != null ? date : LocalDate.now();
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("viewMode", view);
        model.addAttribute("theatres", theatreService.getAllActiveTheatres());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("statuses", SurgeryStatus.values());
        model.addAttribute("priorities", SurgeryPriority.values());

        // Quick stats for top counters
        SurgeryReportStatsDto stats = reportService.generateStats(selectedDate, selectedDate);
        model.addAttribute("stats", stats);

        if ("weekly".equalsIgnoreCase(view)) {
            LocalDate startOfWeek = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            List<SurgeryBooking> weeklySurgeries = schedulingService.getScheduleForDateRange(startOfWeek, endOfWeek);
            model.addAttribute("weeklySurgeries", weeklySurgeries);
            model.addAttribute("startOfWeek", startOfWeek);
            model.addAttribute("endOfWeek", endOfWeek);
        } else {
            // Paginated / filtered search
            LocalDate filterStart = (search != null && !search.isBlank()) ? null : selectedDate;
            LocalDate filterEnd = (search != null && !search.isBlank()) ? null : selectedDate;
            Page<SurgeryBooking> surgeryPage = schedulingService.searchSurgeries(
                    search, theatreId, surgeonId, status, priority, filterStart, filterEnd, page, 15, "plannedStartTime", "asc");
            model.addAttribute("surgeryPage", surgeryPage);
            model.addAttribute("surgeries", surgeryPage.getContent());
        }

        model.addAttribute("filterTheatreId", theatreId);
        model.addAttribute("filterSurgeonId", surgeonId);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterPriority", priority);
        model.addAttribute("search", search);

        return "ot/schedule";
    }

    /**
     * Operation Theatre and Resource Management.
     */
    @GetMapping("/theatres")
    public String theatresView(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) TheatreStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Page<OperationTheatre> theatrePage = theatreService.searchTheatres(search, status, false, page, 10);
        model.addAttribute("theatrePage", theatrePage);
        model.addAttribute("theatres", theatrePage.getContent());
        model.addAttribute("statuses", TheatreStatus.values());
        model.addAttribute("search", search);
        model.addAttribute("statusFilter", status);

        if (!model.containsAttribute("theatreDto")) {
            model.addAttribute("theatreDto", new OperationTheatreDto());
        }

        return "ot/theatres";
    }

    @PostMapping("/theatres")
    @PreAuthorize("hasRole('ADMIN')")
    public String createTheatre(@Valid @ModelAttribute("theatreDto") OperationTheatreDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/ot/theatres";
        }
        try {
            theatreService.createTheatre(dto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Operation Theatre " + dto.getTheatreCode() + " registered successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/theatres";
    }

    @PostMapping("/theatres/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'STAFF')")
    public String updateTheatreStatus(@PathVariable("id") Long id,
                                      @RequestParam("status") TheatreStatus status,
                                      RedirectAttributes redirectAttributes,
                                      Principal principal) {
        try {
            theatreService.updateStatus(id, status, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Theatre status updated to " + status + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/theatres";
    }

    @PostMapping("/theatres/{id}/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    public String recordMaintenance(@PathVariable("id") Long id,
                                    @RequestParam("maintenanceType") String maintenanceType,
                                    @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                    @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                    @RequestParam("details") String details,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal) {
        try {
            theatreService.recordMaintenance(id, maintenanceType, startTime, endTime, details, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Maintenance downtime recorded and theatre placed in MAINTENANCE mode.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/theatres";
    }

    /**
     * Surgery Booking Form.
     */
    @GetMapping("/surgeries/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newSurgeryForm(Model model) {
        if (!model.containsAttribute("bookingDto")) {
            SurgeryBookingDto dto = new SurgeryBookingDto();
            dto.setPlannedDate(LocalDate.now().plusDays(1));
            dto.setPlannedStartTime(LocalTime.of(9, 0));
            dto.setExpectedDurationMinutes(120);
            model.addAttribute("bookingDto", dto);
        }
        model.addAttribute("theatres", theatreService.getAvailableTheatres());
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("priorities", SurgeryPriority.values());
        model.addAttribute("wardBeds", wardBedRepository.findAll());
        return "ot/booking-form";
    }

    @PostMapping("/surgeries")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String scheduleSurgery(@Valid @ModelAttribute("bookingDto") SurgeryBookingDto dto,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Principal principal,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix errors: " + result.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("theatres", theatreService.getAvailableTheatres());
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("priorities", SurgeryPriority.values());
            model.addAttribute("wardBeds", wardBedRepository.findAll());
            return "ot/booking-form";
        }
        try {
            SurgeryBooking booking = schedulingService.scheduleSurgery(dto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Surgery " + booking.getBookingNumber() + " scheduled successfully.");
            return "redirect:/ot/surgeries/" + booking.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("theatres", theatreService.getAvailableTheatres());
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("priorities", SurgeryPriority.values());
            model.addAttribute("wardBeds", wardBedRepository.findAll());
            return "ot/booking-form";
        }
    }

    /**
     * Surgery Details Hub (Tabs: Overview & Team, Pre-op, Intra-op, Post-op Handover, Consumables, Billing).
     */
    @GetMapping("/surgeries/{id}")
    public String surgeryDetails(@PathVariable("id") Long id,
                                 @RequestParam(value = "tab", defaultValue = "overview") String tab,
                                 Model model) {
        SurgeryBooking booking = schedulingService.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found with ID: " + id));

        model.addAttribute("booking", booking);
        model.addAttribute("activeTab", tab);
        model.addAttribute("teamMembers", teamService.getTeamForSurgery(booking));
        model.addAttribute("checklistItems", workflowService.getChecklistItems(booking));
        model.addAttribute("preopItems", workflowService.getChecklistForPhase(booking, ChecklistPhase.PREOPERATIVE));
        model.addAttribute("signInItems", workflowService.getChecklistForPhase(booking, ChecklistPhase.SIGN_IN));
        model.addAttribute("timeOutItems", workflowService.getChecklistForPhase(booking, ChecklistPhase.TIME_OUT));
        model.addAttribute("signOutItems", workflowService.getChecklistForPhase(booking, ChecklistPhase.SIGN_OUT));
        model.addAttribute("handoverItems", workflowService.getChecklistForPhase(booking, ChecklistPhase.POSTOPERATIVE_HANDOVER));
        model.addAttribute("consumables", inventoryService.getConsumablesForSurgery(booking));
        model.addAttribute("auditHistory", workflowService.getAuditHistory(booking));

        // Options for actions
        model.addAttribute("allDoctors", doctorRepository.findAll());
        model.addAttribute("allStaffUsers", userRepository.findAll());
        model.addAttribute("allMedicines", medicineRepository.findByActiveTrueOrderByNameAsc());
        model.addAttribute("allTheatres", theatreService.getAllActiveTheatres());
        model.addAttribute("surgeryStatuses", SurgeryStatus.values());
        model.addAttribute("checklistStatuses", ChecklistStatus.values());
        model.addAttribute("totalCharges", billingService.calculateSurgeryTotal(booking));

        return "ot/surgery-details";
    }

    @PostMapping("/surgeries/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String rescheduleSurgery(@PathVariable("id") Long id,
                                    @RequestParam("newDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
                                    @RequestParam("newStartTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newStartTime,
                                    @RequestParam(value = "newDuration", required = false) Integer newDuration,
                                    @RequestParam(value = "newTheatreId", required = false) Long newTheatreId,
                                    @RequestParam(value = "reason", required = false) String reason,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal) {
        try {
            schedulingService.rescheduleSurgery(id, newDate, newStartTime, newDuration, newTheatreId, reason, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Surgery rescheduled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id;
    }

    @PostMapping("/surgeries/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String cancelSurgery(@PathVariable("id") Long id,
                                @RequestParam("reason") String reason,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {
        try {
            schedulingService.cancelSurgery(id, reason, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Surgery cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id;
    }

    @PostMapping("/surgeries/{id}/team")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String assignTeamMember(@PathVariable("id") Long id,
                                   @ModelAttribute SurgicalTeamMemberDto dto,
                                   RedirectAttributes redirectAttributes,
                                   Principal principal) {
        try {
            teamService.assignTeamMember(id, dto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Team member assigned successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=team";
    }

    @PostMapping("/surgeries/{id}/team/{memberId}/remove")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String removeTeamMember(@PathVariable("id") Long id,
                                   @PathVariable("memberId") Long memberId,
                                   RedirectAttributes redirectAttributes,
                                   Principal principal) {
        try {
            teamService.removeTeamMember(id, memberId, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Team member removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=team";
    }

    @PostMapping("/surgeries/{id}/checklist/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'STAFF')")
    public String updateChecklistItem(@PathVariable("id") Long id,
                                      @PathVariable("itemId") Long itemId,
                                      @ModelAttribute ChecklistItemUpdateDto dto,
                                      @RequestParam(value = "phaseTab", defaultValue = "preop") String phaseTab,
                                      RedirectAttributes redirectAttributes,
                                      Principal principal) {
        try {
            workflowService.updateChecklistItem(itemId, dto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Checklist item updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=" + phaseTab;
    }

    @PostMapping("/surgeries/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String updateSurgeryStatus(@PathVariable("id") Long id,
                                      @ModelAttribute SurgeryStatusUpdateDto dto,
                                      RedirectAttributes redirectAttributes,
                                      Principal principal) {
        try {
            workflowService.updateSurgeryStatus(id, dto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Surgery workflow status updated to " + dto.getTargetStatus() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id;
    }

    @PostMapping("/surgeries/{id}/operative-notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String saveOperativeNotes(@PathVariable("id") Long id,
                                     @RequestParam("operativeNotes") String operativeNotes,
                                     @RequestParam(value = "anesthesiaNotes", required = false) String anesthesiaNotes,
                                     @RequestParam(value = "postoperativeDiagnosis", required = false) String postopDiagnosis,
                                     RedirectAttributes redirectAttributes,
                                     Principal principal) {
        try {
            workflowService.saveOperativeNotes(id, operativeNotes, anesthesiaNotes, postopDiagnosis, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Operative notes saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=intraop";
    }

    @PostMapping("/surgeries/{id}/handover")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'STAFF')")
    public String recordHandover(@PathVariable("id") Long id,
                                 @RequestParam("recoveryDestination") String recoveryDestination,
                                 @RequestParam(value = "handoverNotes", required = false) String handoverNotes,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        try {
            workflowService.recordHandover(id, recoveryDestination, handoverNotes, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Postoperative recovery handover recorded.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=postop";
    }

    @PostMapping("/surgeries/{id}/consumables")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'STAFF')")
    public String addConsumable(@PathVariable("id") Long id,
                                @ModelAttribute SurgeryConsumableDto dto,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {
        try {
            inventoryService.addConsumable(id, dto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Consumable recorded.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=inventory";
    }

    @PostMapping("/surgeries/{id}/consumables/deduct")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'STAFF')")
    public String deductStock(@PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {
        try {
            int count = inventoryService.deductStockForSurgery(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Deducted stock for " + count + " items from pharmacy inventory.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=inventory";
    }

    @PostMapping("/surgeries/{id}/billing")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING')")
    public String generateBilling(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes,
                                  Principal principal) {
        try {
            Invoice invoice = billingService.generateSurgeryInvoice(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Surgery Invoice " + invoice.getInvoiceNumber() + " generated/synchronized.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ot/surgeries/" + id + "?tab=billing";
    }

    /**
     * OT Inventory & Consumables general overview.
     */
    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'STAFF')")
    public String inventoryView(Model model) {
        model.addAttribute("medicines", medicineRepository.findByActiveTrueOrderByNameAsc());
        return "ot/inventory";
    }

    /**
     * OT Analytics and Reports.
     */
    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'BILLING')")
    public String reportsView(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        SurgeryReportStatsDto stats = reportService.generateStats(start, end);
        model.addAttribute("stats", stats);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "ot/reports";
    }

    @GetMapping("/reports/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'BILLING')")
    public void exportReportCsv(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=ot_surgeries_report.csv");
        String csv = reportService.generateCsvExport(startDate, endDate);
        response.getOutputStream().write(csv.getBytes(StandardCharsets.UTF_8));
        response.getOutputStream().flush();
    }
}
