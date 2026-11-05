package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.modules.legacy.entity.Appointment;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.modules.legacy.entity.PatientFeedback;
import com.healthcare.hospital.modules.legacy.repository.AppointmentRepository;
import com.healthcare.hospital.modules.legacy.repository.PatientFeedbackRepository;
import com.healthcare.hospital.shared.dto.PatientFeedbackDto;
import com.healthcare.hospital.shared.enums.AppointmentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PatientFeedbackService {

    private final AppointmentRepository appointmentRepository;
    private final PatientFeedbackRepository feedbackRepository;

    public PatientFeedbackService(AppointmentRepository appointmentRepository,
                                  PatientFeedbackRepository feedbackRepository) {
        this.appointmentRepository = appointmentRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional(readOnly = true)
    public Map<Long, PatientFeedback> getForPatientAppointments(Patient patient, List<Appointment> appointments) {
        if (appointments.isEmpty()) return Map.of();
        return feedbackRepository.findByPatientAndAppointmentIn(patient, appointments).stream()
                .collect(Collectors.toMap(feedback -> feedback.getAppointment().getId(), Function.identity()));
    }

    @Transactional
    public PatientFeedback submit(Long appointmentId, Patient patient, PatientFeedbackDto request) {
        Appointment appointment = appointmentRepository.findByIdForUpdate(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (appointment.getPatient().getId() == null
                || !appointment.getPatient().getId().equals(patient.getId())) {
            throw new SecurityException("Appointment does not belong to the authenticated patient.");
        }
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Feedback can only be submitted for completed appointments.");
        }
        if (feedbackRepository.existsByAppointment(appointment)) {
            throw new IllegalArgumentException("Feedback has already been submitted for this appointment.");
        }
        return feedbackRepository.save(new PatientFeedback(
                patient, appointment, request.getDoctorRating(), request.getServiceRating(), request.getComments()));
    }
}
