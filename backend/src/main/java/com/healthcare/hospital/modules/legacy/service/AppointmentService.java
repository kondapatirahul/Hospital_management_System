package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.shared.dto.AppointmentBookingDto;
import com.healthcare.hospital.shared.dto.AppointmentFilterDto;
import com.healthcare.hospital.shared.dto.AppointmentRescheduleDto;
import com.healthcare.hospital.modules.legacy.entity.Appointment;
import com.healthcare.hospital.modules.legacy.entity.AppointmentRescheduleHistory;
import com.healthcare.hospital.modules.legacy.entity.Department;
import com.healthcare.hospital.modules.legacy.entity.Doctor;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.shared.enums.AppointmentStatus;
import com.healthcare.hospital.modules.legacy.repository.AppointmentRepository;
import com.healthcare.hospital.modules.legacy.repository.AppointmentRescheduleHistoryRepository;
import com.healthcare.hospital.modules.legacy.repository.DepartmentRepository;
import com.healthcare.hospital.modules.legacy.repository.DoctorRepository;
import com.healthcare.hospital.modules.legacy.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final NotificationService notificationService;
    private final DoctorAvailabilityService doctorAvailabilityService;
    private final AppointmentRescheduleHistoryRepository rescheduleHistoryRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              DepartmentRepository departmentRepository,
                              NotificationService notificationService,
                              DoctorAvailabilityService doctorAvailabilityService,
                              AppointmentRescheduleHistoryRepository rescheduleHistoryRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.notificationService = notificationService;
        this.doctorAvailabilityService = doctorAvailabilityService;
        this.rescheduleHistoryRepository = rescheduleHistoryRepository;
    }

    public List<Appointment> getPatientUpcomingAppointments(Patient patient) {
        return appointmentRepository.findByPatientAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(
                patient, LocalDate.now());
    }

    public List<Appointment> getPatientPastAppointments(Patient patient) {
        return appointmentRepository.findPatientPastOrCompletedAppointments(
                patient, LocalDate.now(), AppointmentStatus.COMPLETED);
    }

    public List<Appointment> getPatientAllAppointments(Patient patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDescAppointmentTimeDesc(patient);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getTodayAppointments() {
        return appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate.now());
    }

    public List<Appointment> filterAppointments(AppointmentFilterDto filter) {
        LocalDate date = filter.getDate();
        AppointmentStatus status = filter.getStatus();
        Long doctorId = filter.getDoctorId();
        Long departmentId = filter.getDepartmentId();
        String keyword = filter.getKeyword();

        return appointmentRepository.filterAppointments(date, status, doctorId, departmentId, keyword);
    }

    public long countAppointmentsToday() {
        return appointmentRepository.countByAppointmentDate(LocalDate.now());
    }

    public long countAppointmentsByStatusToday(AppointmentStatus status) {
        return appointmentRepository.countByAppointmentDateAndStatus(LocalDate.now(), status);
    }

    @Transactional
    public Appointment bookAppointment(AppointmentBookingDto dto, Patient patient) {
        Doctor doctor = doctorRepository.findByIdForUpdate(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + dto.getDoctorId()));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + dto.getDepartmentId()));

        LocalTime parsedTime = parseTimeSlot(dto.getAppointmentSlot());
        String normalizedSlot = formatTimeSlot(parsedTime);
        validateFutureDate(dto.getAppointmentDate());
        if (!doctor.getDepartment().getId().equals(department.getId())) {
            throw new IllegalArgumentException("Selected doctor does not belong to the selected department.");
        }
        if (!doctorAvailabilityService.getAvailableSlotsForDoctorAndDate(doctor, dto.getAppointmentDate())
                .contains(normalizedSlot)) {
            throw new IllegalArgumentException("The selected appointment slot is no longer available.");
        }

        Appointment appointment = new Appointment(
                patient,
                doctor,
                department,
                dto.getAppointmentDate(),
                parsedTime,
                normalizedSlot,
                dto.getReasonForVisit(),
                AppointmentStatus.SCHEDULED
        );

        Appointment saved = appointmentRepository.save(appointment);

        if (patient.getUser() != null) {
            notificationService.createNotification(
                    patient.getUser(),
                    "Appointment Scheduled",
                    String.format("Your appointment with Dr. %s (%s) is confirmed for %s at %s.",
                            doctor.getFullName(), department.getName(), dto.getAppointmentDate(), dto.getAppointmentSlot()),
                    "APPOINTMENT_REMINDER"
            );
        }

        return saved;
    }

    @Transactional
    public Appointment rescheduleAppointment(Long appointmentId, AppointmentRescheduleDto dto) {
        return rescheduleAppointment(appointmentId, dto, null);
    }

    @Transactional
    public Appointment rescheduleAppointment(Long appointmentId, AppointmentRescheduleDto dto, Patient requestingPatient) {
        Appointment appointment = appointmentRepository.findByIdForUpdate(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (requestingPatient != null && !appointment.getPatient().getId().equals(requestingPatient.getId())) {
            throw new SecurityException("Appointment does not belong to the authenticated patient.");
        }
        validateReschedulable(appointment);

        Doctor doctor = doctorRepository.findByIdForUpdate(appointment.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + appointment.getDoctor().getId()));
        validateFutureDate(dto.getNewDate());
        LocalTime newTime = parseTimeSlot(dto.getNewSlot());
        String normalizedSlot = formatTimeSlot(newTime);
        if (!doctorAvailabilityService.getAvailableSlotsForDoctorAndDate(doctor, dto.getNewDate(), appointmentId)
                .contains(normalizedSlot)) {
            throw new IllegalArgumentException("The selected appointment slot is no longer available.");
        }

        rescheduleHistoryRepository.save(new AppointmentRescheduleHistory(
                appointment,
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getAppointmentSlot(),
                dto.getNewDate(),
                newTime,
                normalizedSlot,
                dto.getRescheduleReason()
        ));

        appointment.setAppointmentDate(dto.getNewDate());
        appointment.setAppointmentSlot(normalizedSlot);
        appointment.setAppointmentTime(newTime);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);

        Appointment updated = appointmentRepository.save(appointment);

        if (appointment.getPatient().getUser() != null) {
            notificationService.createNotification(
                    appointment.getPatient().getUser(),
                    "Appointment Rescheduled",
                    String.format("Your appointment with Dr. %s has been rescheduled to %s at %s.",
                            appointment.getDoctor().getFullName(), dto.getNewDate(), dto.getNewSlot()),
                    "APPOINTMENT_REMINDER"
            );
        }

        return updated;
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableSlotsForReschedule(Long appointmentId, Patient requestingPatient, LocalDate date) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
        if (requestingPatient != null && !appointment.getPatient().getId().equals(requestingPatient.getId())) {
            throw new SecurityException("Appointment does not belong to the authenticated patient.");
        }
        validateReschedulable(appointment);
        validateFutureDate(date);
        return doctorAvailabilityService.getAvailableSlotsForDoctorAndDate(
                appointment.getDoctor(), date, appointmentId);
    }

    @Transactional
    public Appointment cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason != null && !reason.trim().isEmpty() ? reason : "Cancelled by user");

        Appointment updated = appointmentRepository.save(appointment);

        if (appointment.getPatient().getUser() != null) {
            notificationService.createNotification(
                    appointment.getPatient().getUser(),
                    "Appointment Cancelled",
                    String.format("Your appointment with Dr. %s on %s was cancelled. Reason: %s",
                            appointment.getDoctor().getFullName(), appointment.getAppointmentDate(), appointment.getCancellationReason()),
                    "APPOINTMENT_REMINDER"
            );
        }

        return updated;
    }

    private LocalTime parseTimeSlot(String slot) {
        if (slot == null || slot.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment time slot is required.");
        }
        try {
            // Support "09:30 AM" or "9:30 AM" or "09:30"
            String cleaned = slot.trim().toUpperCase();
            if (cleaned.contains("AM") || cleaned.contains("PM")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");
                return LocalTime.parse(cleaned, dtf);
            } else {
                return LocalTime.parse(cleaned);
            }
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Appointment time slot is invalid.", e);
        }
    }

    private String formatTimeSlot(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase();
    }

    private void validateFutureDate(LocalDate date) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date must be today or in the future.");
        }
    }

    private void validateReschedulable(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED
                && appointment.getStatus() != AppointmentStatus.RESCHEDULED) {
            throw new IllegalArgumentException("Only scheduled appointments can be rescheduled.");
        }
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())
                || (appointment.getAppointmentDate().isEqual(LocalDate.now())
                && appointment.getAppointmentTime() != null
                && appointment.getAppointmentTime().isBefore(LocalTime.now()))) {
            throw new IllegalArgumentException("Past appointments cannot be rescheduled.");
        }
    }
}
