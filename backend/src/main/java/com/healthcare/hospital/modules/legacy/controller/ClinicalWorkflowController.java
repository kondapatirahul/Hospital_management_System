package com.healthcare.hospital.modules.legacy.controller;
import com.healthcare.hospital.modules.workflows.application.*;
import com.healthcare.hospital.modules.workflows.dto.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clinical-workflows")
public class ClinicalWorkflowController {
    private final PatientWorkflowApplicationService patients;
    private final DoctorWorkflowApplicationService doctors;
    private final NursingWorkflowApplicationService nursing;
    private final LaboratoryWorkflowApplicationService laboratory;
    private final PharmacyWorkflowApplicationService pharmacy;
    public ClinicalWorkflowController(PatientWorkflowApplicationService patients,DoctorWorkflowApplicationService doctors,
        NursingWorkflowApplicationService nursing,LaboratoryWorkflowApplicationService laboratory,PharmacyWorkflowApplicationService pharmacy) {
        this.patients=patients; this.doctors=doctors; this.nursing=nursing; this.laboratory=laboratory; this.pharmacy=pharmacy;
    }
    @GetMapping("/patients")
    public List<PatientWorkflowDto> patients(@RequestParam(required=false) String q) { return patients.search(q); }
    @GetMapping("/patients/{id}")
    public PatientWorkflowDto patient(@PathVariable Long id) { return patients.getById(id); }
    @GetMapping("/doctors")
    public List<DoctorWorkflowDto> doctors(@RequestParam(required=false) String q) { return doctors.search(q); }
    @GetMapping("/doctors/available")
    public List<DoctorWorkflowDto> availableDoctors() { return doctors.availableToday(); }
    @GetMapping("/nursing/board")
    public NursingBoardDto nursingBoard(@RequestParam(required=false) String ward) { return nursing.board(ward); }
    @GetMapping("/laboratory/board")
    public LaboratoryBoardDto laboratoryBoard() { return laboratory.board(); }
    @GetMapping("/pharmacy/board")
    public PharmacyBoardDto pharmacyBoard() { return pharmacy.board(); }
}
