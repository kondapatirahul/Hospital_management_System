package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.AppointmentBookingDto;
import com.healthcare.hospital.dto.AppointmentFilterDto;
import com.healthcare.hospital.dto.AppointmentRescheduleDto;
import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Department;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.enums.AppointmentStatus;
import com.healthcare.hospital.repository.AppointmentRepository;
import com.healthcare.hospital.repository.DepartmentRepository;
import com.healthcare.hospital.repository.DoctorRepository;
import com.healthcare.hospital.repository.PatientRepository;
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

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              DepartmentRepository departmentRepository,
                              NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.notificationService = notificationService;
    }

    public List<Appointment> getPatientUpcomingAppointments(Patient patient) {
        return appointmentRepository.findByPatientAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(
                patient, LocalDate.now());
    }

    public List<Appointment> getPatientPastAppointments(Patient patient) {
        return appointmentRepository.findByPatientAndAppointmentDateLessThanOrderByAppointmentDateDescAppointmentTimeDesc(
                patient, LocalDate.now());
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
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + dto.getDoctorId()));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + dto.getDepartmentId()));

        LocalTime parsedTime = parseTimeSlot(dto.getAppointmentSlot());

        Appointment appointment = new Appointment(
                patient,
                doctor,
                department,
                dto.getAppointmentDate(),
                parsedTime,
                dto.getAppointmentSlot(),
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
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        appointment.setAppointmentDate(dto.getNewDate());
        appointment.setAppointmentSlot(dto.getNewSlot());
        appointment.setAppointmentTime(parseTimeSlot(dto.getNewSlot()));
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
            return LocalTime.of(9, 0);
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
        } catch (Exception e) {
            return LocalTime.of(9, 0);
        }
    }
}
