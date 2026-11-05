package com.healthcare.hospital.modules.legacy.controller;

import com.healthcare.hospital.core.config.CustomUserDetails;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.modules.legacy.service.PatientReportPdfService;
import com.healthcare.hospital.modules.legacy.service.PatientService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/patient/reports")
public class PatientReportController {

    private final PatientService patientService;
    private final PatientReportPdfService reportPdfService;

    public PatientReportController(PatientService patientService, PatientReportPdfService reportPdfService) {
        this.patientService = patientService;
        this.reportPdfService = reportPdfService;
    }

    @GetMapping("/{type}/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> report(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean download) {
        Patient patient = patientService.getPatientByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("Patient record not found."));
        String title = type + "-" + id;
        byte[] pdf;
        try {
            pdf = switch (type) {
                case "lab" -> reportPdfService.createLabReport(id, patient);
                case "medical-record" -> reportPdfService.createMedicalRecord(id, patient);
                case "prescription" -> reportPdfService.createPrescription(id, patient);
                default -> throw new NoSuchElementException("Report not found.");
            };
        } catch (NoSuchElementException exception) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder(download ? "attachment" : "inline")
                .filename(title + ".pdf", StandardCharsets.UTF_8)
                .build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
