package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.modules.legacy.entity.LabReport;
import com.healthcare.hospital.modules.legacy.entity.MedicalRecord;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.modules.legacy.entity.Prescription;
import com.healthcare.hospital.modules.legacy.repository.LabReportRepository;
import com.healthcare.hospital.modules.legacy.repository.MedicalRecordRepository;
import com.healthcare.hospital.modules.legacy.repository.PrescriptionRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@Service
public class PatientReportPdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private final LabReportRepository labReportRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PatientReportPdfService(LabReportRepository labReportRepository,
                                   MedicalRecordRepository medicalRecordRepository,
                                   PrescriptionRepository prescriptionRepository) {
        this.labReportRepository = labReportRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public byte[] createLabReport(Long reportId, Patient patient) {
        LabReport report = labReportRepository.findByIdAndPatient(reportId, patient)
                .orElseThrow(() -> new NoSuchElementException("Report not found."));
        return createPdf("Laboratory Report", report.getTestName(), patient, pdf -> {
            add(pdf, "Report date: " + formatDate(report.getCompletedDate() == null
                    ? report.getRequestedDate() : report.getCompletedDate()));
            add(pdf, "Type: " + value(report.getTestCategory()));
            add(pdf, "Status: " + report.getStatus().getDisplayName());
            add(pdf, "Requested date: " + formatDate(report.getRequestedDate()));
            add(pdf, "Completed date: " + formatDate(report.getCompletedDate()));
            add(pdf, "Summary findings: " + value(report.getSummaryResult()));
            add(pdf, "Reference range: " + value(report.getReferenceRange()));
            add(pdf, "Physician notes: " + value(report.getDoctorNotes()));
        });
    }

    public byte[] createMedicalRecord(Long recordId, Patient patient) {
        MedicalRecord record = medicalRecordRepository.findByIdAndPatient(recordId, patient)
                .orElseThrow(() -> new NoSuchElementException("Report not found."));
        return createPdf("Medical Record", record.getDiagnosis(), patient, pdf -> {
            add(pdf, "Report date: " + formatDate(record.getVisitDate()));
            add(pdf, "Type: Medical record");
            add(pdf, "Status: Completed");
            add(pdf, "Attending physician: " + record.getDoctor().getFullName());
            add(pdf, "Symptoms: " + value(record.getSymptoms()));
            add(pdf, "Diagnosis: " + value(record.getDiagnosis()));
            add(pdf, "Clinical notes: " + value(record.getClinicalNotes()));
            add(pdf, "Vital signs: " + value(record.getVitalSigns()));
            add(pdf, "Follow-up instructions: " + value(record.getFollowUpInstructions()));
        });
    }

    public byte[] createPrescription(Long prescriptionId, Patient patient) {
        Prescription prescription = prescriptionRepository.findByIdAndPatient(prescriptionId, patient)
                .orElseThrow(() -> new NoSuchElementException("Report not found."));
        return createPdf("Prescription", prescription.getMedicationName(), patient, pdf -> {
            add(pdf, "Report date: " + formatDate(prescription.getPrescriptionDate()));
            add(pdf, "Type: Prescription");
            add(pdf, "Status: " + value(prescription.getStatus()));
            add(pdf, "Prescribed by: " + prescription.getDoctor().getFullName());
            add(pdf, "Medication: " + prescription.getMedicationName());
            add(pdf, "Dosage: " + prescription.getDosage());
            add(pdf, "Frequency: " + prescription.getFrequency());
            add(pdf, "Duration: " + value(prescription.getDuration()));
            add(pdf, "Instructions: " + value(prescription.getInstructions()));
        });
    }

    private byte[] createPdf(String type, String title, Patient patient, ReportContent content) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("CareFlow Healthcare Management System",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph(type + ": " + value(title),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)));
            document.add(new Paragraph("Patient: " + patient.getFullName()));
            document.add(new Paragraph("Patient ID: " + patient.getPatientId()));
            document.add(new Paragraph(" "));
            content.write(document);
            document.close();
            return output.toByteArray();
        } catch (DocumentException exception) {
            throw new IllegalStateException("Unable to generate the patient report PDF.", exception);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Unable to create the patient report PDF.", exception);
        }
    }

    private static void add(Document document, String text) throws DocumentException {
        document.add(new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private static String value(String text) {
        return text == null || text.isBlank() ? "Not recorded" : text;
    }

    private static String formatDate(java.time.LocalDate date) {
        return date == null ? "Not recorded" : DATE_FORMAT.format(date);
    }

    @FunctionalInterface
    private interface ReportContent {
        void write(Document document) throws DocumentException;
    }
}
