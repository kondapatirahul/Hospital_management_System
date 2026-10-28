package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.ConsultationDto;
import com.healthcare.hospital.dto.PrescriptionItemDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.AppointmentStatus;
import com.healthcare.hospital.enums.QueueStatus;
import com.healthcare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultationService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final NotificationService notificationService;

    public ConsultationService(MedicalRecordRepository medicalRecordRepository,
                               PrescriptionRepository prescriptionRepository,
                               AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               QueueEntryRepository queueEntryRepository,
                               NotificationService notificationService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.notificationService = notificationService;
    }

    public List<MedicalRecord> getDoctorConsultations(Doctor doctor) {
        return medicalRecordRepository.findByDoctorOrderByVisitDateDesc(doctor);
    }

    public Optional<MedicalRecord> getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    public List<MedicalRecord> getPatientHistoryForDoctor(Doctor doctor, Patient patient) {
        // Clinical history of patient visible to treating doctor
        return medicalRecordRepository.findByPatientOrderByVisitDateDesc(patient);
    }

    public List<Prescription> getDoctorPrescriptions(Doctor doctor) {
        return prescriptionRepository.findByDoctorOrderByPrescriptionDateDesc(doctor);
    }

    public List<Prescription> getPrescriptionsByMedicalRecord(MedicalRecord record) {
        return prescriptionRepository.findByMedicalRecord(record);
    }

    public Optional<MedicalRecord> getConsultationForAppointment(Appointment appointment) {
        return medicalRecordRepository.findByAppointment(appointment);
    }

    @Transactional
    public MedicalRecord saveConsultation(Doctor doctor, ConsultationDto dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + dto.getAppointmentId()));

        // Security check: Appointment must belong to this doctor
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Unauthorized: This appointment is not assigned to you.");
        }

        Patient patient = appointment.getPatient();
        if (patient == null || !patient.getId().equals(dto.getPatientId())) {
            throw new IllegalArgumentException("Patient information does not match appointment record.");
        }

        if (dto.getDiagnosis() == null || dto.getDiagnosis().trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty.");
        }

        if (dto.getClinicalNotes() == null || dto.getClinicalNotes().trim().isEmpty()) {
            throw new IllegalArgumentException("Clinical notes cannot be empty.");
        }

        // Check if consultation note already exists for this appointment (update or create)
        Optional<MedicalRecord> existingOpt = medicalRecordRepository.findByAppointment(appointment);
        MedicalRecord medicalRecord;

        LocalDate today = LocalDate.now();

        if (existingOpt.isPresent()) {
            medicalRecord = existingOpt.get();
            // Verify doctor ownership of existing record
            if (!medicalRecord.getDoctor().getId().equals(doctor.getId())) {
                throw new SecurityException("Unauthorized: You cannot modify another doctor's consultation notes.");
            }
            medicalRecord.setSymptoms(dto.getSymptoms());
            medicalRecord.setDiagnosis(dto.getDiagnosis().trim());
            medicalRecord.setClinicalNotes(dto.getClinicalNotes().trim());
            medicalRecord.setVitalSigns(dto.getVitalSigns());
            medicalRecord.setFollowUpInstructions(dto.getFollowUpInstructions());
        } else {
            medicalRecord = new MedicalRecord(
                    patient,
                    doctor,
                    appointment,
                    today,
                    dto.getSymptoms(),
                    dto.getDiagnosis().trim(),
                    dto.getClinicalNotes().trim(),
                    dto.getVitalSigns(),
                    dto.getFollowUpInstructions()
            );
        }

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

        // Process Prescriptions if provided
        if (dto.getPrescriptions() != null && !dto.getPrescriptions().isEmpty()) {
            for (PrescriptionItemDto rxDto : dto.getPrescriptions()) {
                if (rxDto.getMedicationName() != null && !rxDto.getMedicationName().trim().isEmpty()) {
                    Prescription prescription = new Prescription(
                            patient,
                            doctor,
                            appointment,
                            savedRecord,
                            today,
                            rxDto.getMedicationName().trim(),
                            rxDto.getDosage() != null ? rxDto.getDosage().trim() : "As directed",
                            rxDto.getFrequency() != null ? rxDto.getFrequency().trim() : "Once daily",
                            rxDto.getDuration() != null ? rxDto.getDuration().trim() : "5 days",
                            rxDto.getInstructions() != null ? rxDto.getInstructions().trim() : "",
                            "Active"
                    );
                    prescriptionRepository.save(prescription);
                }
            }
        }

        // Update appointment status to COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // Update queue entry if present
        Optional<QueueEntry> queueEntryOpt = queueEntryRepository.findByAppointment(appointment);
        if (queueEntryOpt.isPresent()) {
            QueueEntry queueEntry = queueEntryOpt.get();
            queueEntry.setStatus(QueueStatus.COMPLETED);
            queueEntry.setCompletedTime(LocalDateTime.now());
            queueEntryRepository.save(queueEntry);
        }

        // Notify patient
        if (patient.getUser() != null) {
            notificationService.createNotification(
                    patient.getUser(),
                    "Consultation Completed",
                    String.format("Dr. %s has completed your consultation. Diagnosis notes and any prescriptions are now ready in your patient portal.",
                            doctor.getFullName()),
                    "CLINICAL_UPDATE"
            );
        }

        return savedRecord;
    }

    @Transactional
    public Prescription issueDirectPrescription(Doctor doctor, Long patientId, PrescriptionItemDto rxDto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        if (rxDto.getMedicationName() == null || rxDto.getMedicationName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medication name is required.");
        }

        Prescription prescription = new Prescription(
                patient,
                doctor,
                null,
                null,
                LocalDate.now(),
                rxDto.getMedicationName().trim(),
                rxDto.getDosage() != null && !rxDto.getDosage().trim().isEmpty() ? rxDto.getDosage().trim() : "Standard dosage",
                rxDto.getFrequency() != null && !rxDto.getFrequency().trim().isEmpty() ? rxDto.getFrequency().trim() : "As directed",
                rxDto.getDuration() != null ? rxDto.getDuration().trim() : "",
                rxDto.getInstructions() != null ? rxDto.getInstructions().trim() : "",
                "Active"
        );

        Prescription saved = prescriptionRepository.save(prescription);

        if (patient.getUser() != null) {
            notificationService.createNotification(
                    patient.getUser(),
                    "New Prescription Issued",
                    String.format("Dr. %s has prescribed %s (%s). Check your prescriptions tab for dosage instructions.",
                            doctor.getFullName(), saved.getMedicationName(), saved.getDosage()),
                    "PRESCRIPTION_ISSUED"
            );
        }

        return saved;
    }

    @Transactional
    public MedicalRecord updateConsultationNotes(Doctor doctor, Long recordId, String notes, String followUp) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found: " + recordId));

        if (!record.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Unauthorized: You cannot edit another doctor's consultation notes.");
        }

        if (notes == null || notes.trim().isEmpty()) {
            throw new IllegalArgumentException("Clinical notes cannot be empty.");
        }

        record.setClinicalNotes(notes.trim());
        if (followUp != null) {
            record.setFollowUpInstructions(followUp.trim());
        }

        return medicalRecordRepository.save(record);
    }
}
