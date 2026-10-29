package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SurgeryBookingDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.SurgeryPriority;
import com.healthcare.hospital.enums.SurgeryStatus;
import com.healthcare.hospital.enums.TheatreStatus;
import com.healthcare.hospital.repository.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SurgerySchedulingService {

    private static final Set<SurgeryStatus> INACTIVE_STATUSES = Set.of(SurgeryStatus.CANCELLED, SurgeryStatus.POSTPONED);

    private final SurgeryBookingRepository bookingRepository;
    private final OperationTheatreRepository theatreRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final WardBedRepository wardBedRepository;
    private final SurgeryAuditTrailRepository auditTrailRepository;
    private final SurgeryWorkflowService workflowService;
    private final AuditEventService auditEventService;

    public SurgerySchedulingService(SurgeryBookingRepository bookingRepository,
                                    OperationTheatreRepository theatreRepository,
                                    PatientRepository patientRepository,
                                    DoctorRepository doctorRepository,
                                    AppointmentRepository appointmentRepository,
                                    WardBedRepository wardBedRepository,
                                    SurgeryAuditTrailRepository auditTrailRepository,
                                    @Lazy SurgeryWorkflowService workflowService,
                                    AuditEventService auditEventService) {
        this.bookingRepository = bookingRepository;
        this.theatreRepository = theatreRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.wardBedRepository = wardBedRepository;
        this.auditTrailRepository = auditTrailRepository;
        this.workflowService = workflowService;
        this.auditEventService = auditEventService;
    }

    public synchronized String generateNextBookingNumber() {
        int currentYear = LocalDate.now().getYear();
        long nextNum = 1;
        Optional<SurgeryBooking> topBooking = bookingRepository.findTopByOrderByIdDesc();
        if (topBooking.isPresent() && topBooking.get().getId() != null) {
            nextNum = topBooking.get().getId() + 1;
        }
        return String.format("SURG-%d-%04d", currentYear, nextNum);
    }

    public Optional<SurgeryBooking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<SurgeryBooking> getBookingByNumber(String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber);
    }

    public List<SurgeryBooking> getScheduleForDate(LocalDate date) {
        return bookingRepository.findByPlannedDateOrderByPlannedStartTimeAsc(date);
    }

    public List<SurgeryBooking> getScheduleForDateRange(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByPlannedDateBetweenOrderByPlannedDateAscPlannedStartTimeAsc(startDate, endDate);
    }

    public Page<SurgeryBooking> searchSurgeries(String search, Long theatreId, Long surgeonId,
                                               SurgeryStatus status, SurgeryPriority priority,
                                               LocalDate startDate, LocalDate endDate,
                                               int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by(sortDir != null && sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortField != null && !sortField.isBlank() ? sortField : "plannedDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookingRepository.searchSurgeries(search, theatreId, surgeonId, status, priority, startDate, endDate, pageable);
    }

    /**
     * Concurrency-safe scheduling with pessimistic write locking on the theatre and repeatable-read isolation.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SurgeryBooking scheduleSurgery(SurgeryBookingDto dto, String username) {
        // 1. Validate Theatre
        OperationTheatre theatre = theatreRepository.findByIdWithLock(dto.getTheatreId())
                .orElseThrow(() -> new IllegalArgumentException("Operation theatre not found: " + dto.getTheatreId()));

        if (!theatre.isActive()) {
            throw new IllegalStateException("Theatre " + theatre.getTheatreCode() + " is inactive and cannot be booked.");
        }
        if (theatre.getStatus() == TheatreStatus.MAINTENANCE) {
            throw new IllegalStateException("Theatre " + theatre.getTheatreCode() + " is currently under MAINTENANCE and cannot be scheduled.");
        }

        // 2. Validate Patient
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + dto.getPatientId()));

        // 3. Validate Doctor
        Doctor leadSurgeon = doctorRepository.findById(dto.getLeadSurgeonId())
                .orElseThrow(() -> new IllegalArgumentException("Lead surgeon not found: " + dto.getLeadSurgeonId()));

        // 4. Validate time window
        LocalTime newStart = dto.getPlannedStartTime();
        int duration = dto.getExpectedDurationMinutes() != null ? dto.getExpectedDurationMinutes() : 120;
        LocalTime newEnd = newStart.plusMinutes(duration);

        // 5. Conflict detection: Theatre overlapping bookings
        List<SurgeryBooking> existingTheatreBookings = bookingRepository.findByTheatreAndPlannedDateAndStatusNotIn(
                theatre, dto.getPlannedDate(), INACTIVE_STATUSES);

        for (SurgeryBooking existing : existingTheatreBookings) {
            if (isTimeOverlapping(newStart, newEnd, existing.getPlannedStartTime(), existing.getPlannedEndTime())) {
                throw new IllegalStateException(String.format(
                        "Theatre conflict: %s is already booked for procedure '%s' (%s to %s) on %s.",
                        theatre.getName(), existing.getProcedureName(),
                        existing.getPlannedStartTime(), existing.getPlannedEndTime(), dto.getPlannedDate()
                ));
            }
        }

        // 6. Conflict detection: Lead Surgeon overlapping bookings
        List<SurgeryBooking> existingSurgeonBookings = bookingRepository.findByLeadSurgeonAndPlannedDateAndStatusNotIn(
                leadSurgeon, dto.getPlannedDate(), INACTIVE_STATUSES);

        for (SurgeryBooking existing : existingSurgeonBookings) {
            if (isTimeOverlapping(newStart, newEnd, existing.getPlannedStartTime(), existing.getPlannedEndTime())) {
                throw new IllegalStateException(String.format(
                        "Surgeon conflict: Dr. %s is already scheduled for procedure '%s' (%s to %s) on %s.",
                        leadSurgeon.getFullName(), existing.getProcedureName(),
                        existing.getPlannedStartTime(), existing.getPlannedEndTime(), dto.getPlannedDate()
                ));
            }
        }

        // 7. Create booking
        String bookingNumber = generateNextBookingNumber();
        SurgeryBooking booking = new SurgeryBooking(
                bookingNumber, patient, theatre, leadSurgeon,
                dto.getProcedureName().trim(), dto.getPlannedDate(),
                newStart, duration, dto.getPriority(), username
        );
        booking.setProcedureCategory(dto.getProcedureCategory() != null ? dto.getProcedureCategory().trim() : "General");
        booking.setClinicalIndication(dto.getClinicalIndication());
        booking.setPreoperativeDiagnosis(dto.getPreoperativeDiagnosis());

        // Optional links
        if (dto.getAppointmentId() != null) {
            appointmentRepository.findById(dto.getAppointmentId()).ifPresent(booking::setAppointment);
        }
        if (dto.getWardBedId() != null) {
            wardBedRepository.findById(dto.getWardBedId()).ifPresent(booking::setWardBed);
        }

        // Save booking
        SurgeryBooking saved = bookingRepository.save(booking);

        // 8. Auto-assign lead surgeon as first team member
        SurgicalTeamMember leadMember = new SurgicalTeamMember(saved, leadSurgeon, "LEAD_SURGEON", username);
        saved.addTeamMember(leadMember);

        // 9. Initialize standard surgical checklist items (Preoperative, WHO Sign-In, Time-Out, Sign-Out, Post-op Handover)
        workflowService.initializeChecklistForSurgery(saved);

        // 10. Audit log
        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                saved, null, SurgeryStatus.SCHEDULED.name(), username,
                String.format("Scheduled %s (%s) in %s on %s at %s",
                        saved.getProcedureName(), saved.getBookingNumber(),
                        theatre.getTheatreCode(), saved.getPlannedDate(), saved.getPlannedStartTime())
        );
        auditTrailRepository.save(audit);

        auditEventService.record(username, "SCHEDULE_SURGERY", "SurgeryBooking", saved.getId(),
                "Scheduled surgery " + saved.getBookingNumber() + " for patient " + patient.getFullName());

        return saved;
    }

    /**
     * Reschedules an existing surgery with conflict re-validation and audit trail.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SurgeryBooking rescheduleSurgery(Long bookingId, LocalDate newDate, LocalTime newStartTime,
                                            Integer newDuration, Long newTheatreId, String reason, String username) {
        SurgeryBooking booking = bookingRepository.findByIdWithLock(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + bookingId));

        if (booking.getStatus() == SurgeryStatus.COMPLETED || booking.getStatus() == SurgeryStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reschedule a surgery in status " + booking.getStatus());
        }

        OperationTheatre theatre = booking.getTheatre();
        if (newTheatreId != null && !newTheatreId.equals(theatre.getId())) {
            theatre = theatreRepository.findByIdWithLock(newTheatreId)
                    .orElseThrow(() -> new IllegalArgumentException("Operation theatre not found: " + newTheatreId));
            if (!theatre.isActive() || theatre.getStatus() == TheatreStatus.MAINTENANCE) {
                throw new IllegalStateException("Selected theatre is not available for booking.");
            }
        }

        LocalDate targetDate = newDate != null ? newDate : booking.getPlannedDate();
        LocalTime targetStart = newStartTime != null ? newStartTime : booking.getPlannedStartTime();
        int targetDuration = newDuration != null ? newDuration : booking.getExpectedDurationMinutes();
        LocalTime targetEnd = targetStart.plusMinutes(targetDuration);

        // Validate theatre conflicts excluding current booking
        List<SurgeryBooking> existingTheatreBookings = bookingRepository.findByTheatreAndPlannedDateAndStatusNotIn(
                theatre, targetDate, INACTIVE_STATUSES);
        for (SurgeryBooking existing : existingTheatreBookings) {
            if (!existing.getId().equals(booking.getId()) &&
                    isTimeOverlapping(targetStart, targetEnd, existing.getPlannedStartTime(), existing.getPlannedEndTime())) {
                throw new IllegalStateException(String.format(
                        "Theatre conflict: %s is already booked for procedure '%s' (%s to %s) on %s.",
                        theatre.getName(), existing.getProcedureName(),
                        existing.getPlannedStartTime(), existing.getPlannedEndTime(), targetDate
                ));
            }
        }

        // Validate surgeon conflicts excluding current booking
        List<SurgeryBooking> existingSurgeonBookings = bookingRepository.findByLeadSurgeonAndPlannedDateAndStatusNotIn(
                booking.getLeadSurgeon(), targetDate, INACTIVE_STATUSES);
        for (SurgeryBooking existing : existingSurgeonBookings) {
            if (!existing.getId().equals(booking.getId()) &&
                    isTimeOverlapping(targetStart, targetEnd, existing.getPlannedStartTime(), existing.getPlannedEndTime())) {
                throw new IllegalStateException(String.format(
                        "Surgeon conflict: Dr. %s is already scheduled for procedure '%s' (%s to %s) on %s.",
                        booking.getLeadSurgeon().getFullName(), existing.getProcedureName(),
                        existing.getPlannedStartTime(), existing.getPlannedEndTime(), targetDate
                ));
            }
        }

        String details = String.format("Rescheduled from %s %s to %s %s. Reason: %s",
                booking.getPlannedDate(), booking.getPlannedStartTime(), targetDate, targetStart,
                reason != null ? reason : "None provided");

        booking.setTheatre(theatre);
        booking.setPlannedDate(targetDate);
        booking.setPlannedStartTime(targetStart);
        booking.setExpectedDurationMinutes(targetDuration);
        booking.setPostponedReason(reason);
        if (booking.getStatus() == SurgeryStatus.POSTPONED) {
            booking.setStatus(SurgeryStatus.SCHEDULED);
        }

        SurgeryBooking saved = bookingRepository.save(booking);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(saved, booking.getStatus().name(), saved.getStatus().name(), username, details);
        auditTrailRepository.save(audit);

        auditEventService.record(username, "RESCHEDULE_SURGERY", "SurgeryBooking", saved.getId(), details);
        return saved;
    }

    /**
     * Cancels an existing surgery with mandatory reason and audit record.
     */
    @Transactional
    public SurgeryBooking cancelSurgery(Long bookingId, String reason, String username) {
        SurgeryBooking booking = bookingRepository.findByIdWithLock(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + bookingId));

        if (booking.getStatus() == SurgeryStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel an already completed surgery.");
        }
        if (booking.getStatus() == SurgeryStatus.CANCELLED) {
            return booking;
        }

        if (reason == null || reason.trim().isBlank()) {
            throw new IllegalArgumentException("Cancellation reason is mandatory.");
        }

        SurgeryStatus previousStatus = booking.getStatus();
        booking.setStatus(SurgeryStatus.CANCELLED);
        booking.setCancellationReason(reason.trim());

        // Release theatre if it was reserved/in use
        if (booking.getTheatre().getStatus() == TheatreStatus.RESERVED || booking.getTheatre().getStatus() == TheatreStatus.IN_USE) {
            booking.getTheatre().setStatus(TheatreStatus.AVAILABLE);
            theatreRepository.save(booking.getTheatre());
        }

        SurgeryBooking saved = bookingRepository.save(booking);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                saved, previousStatus.name(), SurgeryStatus.CANCELLED.name(), username,
                "Cancelled: " + reason.trim()
        );
        auditTrailRepository.save(audit);

        auditEventService.record(username, "CANCEL_SURGERY", "SurgeryBooking", saved.getId(),
                "Cancelled surgery " + saved.getBookingNumber() + ". Reason: " + reason.trim());

        return saved;
    }

    /**
     * Helper to detect overlapping time intervals [start1, end1] and [start2, end2].
     */
    public boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
