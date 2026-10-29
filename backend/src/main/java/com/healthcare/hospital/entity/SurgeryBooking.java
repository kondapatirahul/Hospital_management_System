package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.SurgeryPriority;
import com.healthcare.hospital.enums.SurgeryStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Core Surgery Booking and Workflow entity for Operation Theatre management.
 */
@Entity
@Table(name = "surgery_bookings", indexes = {
    @Index(name = "idx_surg_booking_num", columnList = "booking_number", unique = true),
    @Index(name = "idx_surg_theatre_date", columnList = "theatre_id, planned_date"),
    @Index(name = "idx_surg_doctor_date", columnList = "lead_surgeon_id, planned_date"),
    @Index(name = "idx_surg_patient", columnList = "patient_id"),
    @Index(name = "idx_surg_status", columnList = "status")
})
public class SurgeryBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_number", nullable = false, unique = true, length = 32)
    private String bookingNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theatre_id", nullable = false)
    private OperationTheatre theatre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lead_surgeon_id", nullable = false)
    private Doctor leadSurgeon;

    @Column(name = "procedure_name", nullable = false, length = 200)
    private String procedureName;

    @Column(name = "procedure_category", length = 80)
    private String procedureCategory = "General";

    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    @Column(name = "planned_start_time", nullable = false)
    private LocalTime plannedStartTime;

    @Column(name = "expected_duration_minutes", nullable = false)
    private Integer expectedDurationMinutes = 120;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurgeryPriority priority = SurgeryPriority.ROUTINE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SurgeryStatus status = SurgeryStatus.SCHEDULED;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_bed_id")
    private WardBed wardBed;

    @Column(name = "clinical_indication", length = 1000)
    private String clinicalIndication;

    @Column(name = "preoperative_diagnosis", length = 1000)
    private String preoperativeDiagnosis;

    @Column(name = "postoperative_diagnosis", length = 1000)
    private String postoperativeDiagnosis;

    @Column(name = "operative_notes", columnDefinition = "TEXT")
    private String operativeNotes;

    @Column(name = "anesthesia_notes", columnDefinition = "TEXT")
    private String anesthesiaNotes;

    @Column(name = "recovery_destination", length = 150)
    private String recoveryDestination;

    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    @Column(name = "postponed_reason", length = 1000)
    private String postponedReason;

    @Column(name = "booked_by", length = 100)
    private String bookedBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @OneToMany(mappedBy = "surgeryBooking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurgicalTeamMember> teamMembers = new ArrayList<>();

    @OneToMany(mappedBy = "surgeryBooking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurgeryChecklistItem> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "surgeryBooking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurgeryConsumableUsage> consumables = new ArrayList<>();

    @OneToMany(mappedBy = "surgeryBooking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurgeryAuditTrail> auditTrails = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    private Long version;

    public SurgeryBooking() {
    }

    public SurgeryBooking(String bookingNumber, Patient patient, OperationTheatre theatre, Doctor leadSurgeon,
                          String procedureName, LocalDate plannedDate, LocalTime plannedStartTime,
                          Integer expectedDurationMinutes, SurgeryPriority priority, String bookedBy) {
        this.bookingNumber = bookingNumber;
        this.patient = patient;
        this.theatre = theatre;
        this.leadSurgeon = leadSurgeon;
        this.procedureName = procedureName;
        this.plannedDate = plannedDate;
        this.plannedStartTime = plannedStartTime;
        this.expectedDurationMinutes = expectedDurationMinutes != null ? expectedDurationMinutes : 120;
        this.priority = priority != null ? priority : SurgeryPriority.ROUTINE;
        this.status = SurgeryStatus.SCHEDULED;
        this.bookedBy = bookedBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalTime getPlannedEndTime() {
        if (plannedStartTime == null || expectedDurationMinutes == null) {
            return plannedStartTime;
        }
        return plannedStartTime.plusMinutes(expectedDurationMinutes);
    }

    // Helper to manage relations
    public void addTeamMember(SurgicalTeamMember member) {
        teamMembers.add(member);
        member.setSurgeryBooking(this);
    }

    public void addChecklistItem(SurgeryChecklistItem item) {
        checklistItems.add(item);
        item.setSurgeryBooking(this);
    }

    public void addConsumable(SurgeryConsumableUsage consumable) {
        consumables.add(consumable);
        consumable.setSurgeryBooking(this);
    }

    public void addAuditTrail(SurgeryAuditTrail audit) {
        auditTrails.add(audit);
        audit.setSurgeryBooking(this);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public OperationTheatre getTheatre() {
        return theatre;
    }

    public void setTheatre(OperationTheatre theatre) {
        this.theatre = theatre;
    }

    public Doctor getLeadSurgeon() {
        return leadSurgeon;
    }

    public void setLeadSurgeon(Doctor leadSurgeon) {
        this.leadSurgeon = leadSurgeon;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProcedureCategory() {
        return procedureCategory;
    }

    public void setProcedureCategory(String procedureCategory) {
        this.procedureCategory = procedureCategory;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public LocalTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public Integer getExpectedDurationMinutes() {
        return expectedDurationMinutes;
    }

    public void setExpectedDurationMinutes(Integer expectedDurationMinutes) {
        this.expectedDurationMinutes = expectedDurationMinutes;
    }

    public SurgeryPriority getPriority() {
        return priority;
    }

    public void setPriority(SurgeryPriority priority) {
        this.priority = priority;
    }

    public SurgeryStatus getStatus() {
        return status;
    }

    public void setStatus(SurgeryStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public WardBed getWardBed() {
        return wardBed;
    }

    public void setWardBed(WardBed wardBed) {
        this.wardBed = wardBed;
    }

    public String getClinicalIndication() {
        return clinicalIndication;
    }

    public void setClinicalIndication(String clinicalIndication) {
        this.clinicalIndication = clinicalIndication;
    }

    public String getPreoperativeDiagnosis() {
        return preoperativeDiagnosis;
    }

    public void setPreoperativeDiagnosis(String preoperativeDiagnosis) {
        this.preoperativeDiagnosis = preoperativeDiagnosis;
    }

    public String getPostoperativeDiagnosis() {
        return postoperativeDiagnosis;
    }

    public void setPostoperativeDiagnosis(String postoperativeDiagnosis) {
        this.postoperativeDiagnosis = postoperativeDiagnosis;
    }

    public String getOperativeNotes() {
        return operativeNotes;
    }

    public void setOperativeNotes(String operativeNotes) {
        this.operativeNotes = operativeNotes;
    }

    public String getAnesthesiaNotes() {
        return anesthesiaNotes;
    }

    public void setAnesthesiaNotes(String anesthesiaNotes) {
        this.anesthesiaNotes = anesthesiaNotes;
    }

    public String getRecoveryDestination() {
        return recoveryDestination;
    }

    public void setRecoveryDestination(String recoveryDestination) {
        this.recoveryDestination = recoveryDestination;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getPostponedReason() {
        return postponedReason;
    }

    public void setPostponedReason(String postponedReason) {
        this.postponedReason = postponedReason;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public List<SurgicalTeamMember> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<SurgicalTeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public List<SurgeryChecklistItem> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<SurgeryChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public List<SurgeryConsumableUsage> getConsumables() {
        return consumables;
    }

    public void setConsumables(List<SurgeryConsumableUsage> consumables) {
        this.consumables = consumables;
    }

    public List<SurgeryAuditTrail> getAuditTrails() {
        return auditTrails;
    }

    public void setAuditTrails(List<SurgeryAuditTrail> auditTrails) {
        this.auditTrails = auditTrails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}
