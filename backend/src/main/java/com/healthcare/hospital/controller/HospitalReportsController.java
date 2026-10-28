package com.healthcare.hospital.controller;

import com.healthcare.hospital.repository.*;
import com.healthcare.hospital.entity.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Comparator;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'BILLING', 'LAB_TECHNICIAN', 'PHARMACIST')")
public class HospitalReportsController {
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;
    private final LabReportRepository labs;
    private final InvoiceRepository invoices;
    private final PaymentRecordRepository payments;
    private final MedicineRepository medicines;
    private final AuditEventRepository auditEvents;

    public HospitalReportsController(PatientRepository patients, DoctorRepository doctors,
            AppointmentRepository appointments, LabReportRepository labs, InvoiceRepository invoices,
            PaymentRecordRepository payments, MedicineRepository medicines, AuditEventRepository auditEvents) {
        this.patients = patients; this.doctors = doctors; this.appointments = appointments;
        this.labs = labs; this.invoices = invoices; this.payments = payments;
        this.medicines = medicines; this.auditEvents = auditEvents;
    }

    @GetMapping
    public String index(Model model) {
        LocalDate today = LocalDate.now();
        var allPayments = payments.findAll();
        BigDecimal recordedPaymentTotal = allPayments.stream().map(PaymentRecord::getAmount)
                .filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("reportDate", today);
        model.addAttribute("patientCount", patients.count());
        model.addAttribute("doctorCount", doctors.count());
        model.addAttribute("appointmentCount", appointments.count());
        model.addAttribute("labReportCount", labs.count());
        model.addAttribute("invoiceCount", invoices.count());
        model.addAttribute("paymentCount", allPayments.size());
        model.addAttribute("recordedPaymentTotal", recordedPaymentTotal);
        model.addAttribute("medicineCount", medicines.count());
        model.addAttribute("auditEventCount", auditEvents.count());
        model.addAttribute("recentAuditEvents", auditEvents.findTop250ByOrderByCreatedAtDesc().stream().limit(10).toList());
        model.addAttribute("recentPayments", allPayments.stream()
                .sorted(Comparator.comparing(PaymentRecord::getReceivedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10).toList());
        return "reports/index";
    }

    /** Download filtered module data as CSV for authorized operational reporting. */
    @GetMapping("/export/{dataset}")
    public ResponseEntity<byte[]> exportCsv(@PathVariable String dataset) {
        StringBuilder csv = new StringBuilder();
        switch (dataset.toLowerCase()) {
            case "patients" -> {
                csv.append("patient_id,full_name,email,phone,registered_at\n");
                for (Patient p : patients.findAll())
                    csv.append(row(p.getPatientId(), p.getFullName(), p.getEmail(), p.getPhone(), p.getRegisteredAt()));
            }
            case "appointments" -> {
                csv.append("id,patient,doctor,date,time,status,reason\n");
                for (Appointment a : appointments.findAll())
                    csv.append(row(a.getId(), a.getPatient() == null ? "" : a.getPatient().getFullName(),
                            a.getDoctor() == null ? "" : a.getDoctor().getFullName(), a.getAppointmentDate(),
                            a.getAppointmentTime(), a.getStatus(), a.getReasonForVisit()));
            }
            case "invoices" -> {
                csv.append("invoice_number,patient,invoice_date,due_date,amount,tax,discount,total,status\n");
                for (Invoice i : invoices.findAll())
                    csv.append(row(i.getInvoiceNumber(), i.getPatient() == null ? "" : i.getPatient().getFullName(),
                            i.getInvoiceDate(), i.getDueDate(), i.getAmount(), i.getTax(), i.getDiscount(),
                            i.getTotalAmount(), i.getStatus()));
            }
            case "payments" -> {
                csv.append("receipt_number,invoice_number,amount,method,transaction_reference,received_by,received_at\n");
                for (PaymentRecord p : payments.findAll())
                    csv.append(row(p.getReceiptNumber(), p.getInvoice() == null ? "" : p.getInvoice().getInvoiceNumber(),
                            p.getAmount(), p.getPaymentMethod(), p.getTransactionReference(), p.getReceivedBy(), p.getReceivedAt()));
            }
            default -> {
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN)
                        .body("Unsupported export dataset. Use patients, appointments, invoices or payments."
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        }
        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=careflow-" + dataset.toLowerCase() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    private String row(Object... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) line.append(',');
            Object value = values[i];
            String text = value == null ? "" : String.valueOf(value);
            if (!text.isEmpty() && "=+-@".indexOf(text.charAt(0)) >= 0) text = "'" + text;
            line.append('"').append(text.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ")).append('"');
        }
        return line.append('\n').toString();
    }
}
