package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.QueueEntry;
import com.healthcare.hospital.enums.AppointmentStatus;
import com.healthcare.hospital.enums.QueueStatus;
import com.healthcare.hospital.repository.AppointmentRepository;
import com.healthcare.hospital.repository.QueueEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private final QueueEntryRepository queueEntryRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    public QueueService(QueueEntryRepository queueEntryRepository,
                        AppointmentRepository appointmentRepository,
                        NotificationService notificationService) {
        this.queueEntryRepository = queueEntryRepository;
        this.appointmentRepository = appointmentRepository;
        this.notificationService = notificationService;
    }

    public synchronized String generateNextQueueNumber(LocalDate date) {
        long count = queueEntryRepository.countByQueueDate(date);
        return String.format("Q-%03d", 101 + count);
    }

    public List<QueueEntry> getTodayQueue() {
        return queueEntryRepository.findByQueueDateOrderByCheckInTimeAsc(LocalDate.now());
    }

    public List<QueueEntry> getTodayQueueByStatus(QueueStatus status) {
        return queueEntryRepository.findByQueueDateAndStatusOrderByCheckInTimeAsc(LocalDate.now(), status);
    }

    public long getWaitingCountToday() {
        return queueEntryRepository.countByQueueDateAndStatus(LocalDate.now(), QueueStatus.WAITING);
    }

    public long getInConsultationCountToday() {
        return queueEntryRepository.countByQueueDateAndStatus(LocalDate.now(), QueueStatus.IN_CONSULTATION);
    }

    public long getCompletedCountToday() {
        return queueEntryRepository.countByQueueDateAndStatus(LocalDate.now(), QueueStatus.COMPLETED);
    }

    @Transactional
    public QueueEntry checkInPatient(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        // Check if already in queue
        Optional<QueueEntry> existing = queueEntryRepository.findByAppointment(appointment);
        if (existing.isPresent()) {
            return existing.get();
        }

        LocalDate today = LocalDate.now();
        String queueNum = generateNextQueueNumber(today);

        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);

        QueueEntry entry = new QueueEntry(
                queueNum,
                appointment,
                appointment.getPatient(),
                appointment.getDoctor(),
                today,
                QueueStatus.WAITING
        );
        entry.setCheckInTime(LocalDateTime.now());
        QueueEntry saved = queueEntryRepository.save(entry);

        if (appointment.getPatient().getUser() != null) {
            notificationService.createNotification(
                    appointment.getPatient().getUser(),
                    "Checked In - Queue Number Issued",
                    String.format("You are checked in! Your Queue Token is %s for Dr. %s (Room %s). Please take a seat in the waiting area.",
                            queueNum, appointment.getDoctor().getFullName(), appointment.getDoctor().getRoomNumber()),
                    "QUEUE_UPDATE"
            );
        }

        return saved;
    }

    @Transactional
    public QueueEntry callNextPatient(Long queueId) {
        QueueEntry entry = queueEntryRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueId));

        entry.setStatus(QueueStatus.IN_CONSULTATION);
        entry.setCalledTime(LocalDateTime.now());
        queueEntryRepository.save(entry);

        Appointment appointment = entry.getAppointment();
        appointment.setStatus(AppointmentStatus.IN_CONSULTATION);
        appointmentRepository.save(appointment);

        if (entry.getPatient().getUser() != null) {
            notificationService.createNotification(
                    entry.getPatient().getUser(),
                    "It's Your Turn!",
                    String.format("Dr. %s is ready to see you in Room %s now.",
                            entry.getDoctor().getFullName(), entry.getDoctor().getRoomNumber()),
                    "QUEUE_UPDATE"
            );
        }

        return entry;
    }

    @Transactional
    public QueueEntry completeConsultation(Long queueId) {
        QueueEntry entry = queueEntryRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueId));

        entry.setStatus(QueueStatus.COMPLETED);
        entry.setCompletedTime(LocalDateTime.now());
        queueEntryRepository.save(entry);

        Appointment appointment = entry.getAppointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        if (entry.getPatient().getUser() != null) {
            notificationService.createNotification(
                    entry.getPatient().getUser(),
                    "Visit Completed",
                    String.format("Your consultation with Dr. %s has finished. Prescription and invoice are available in your portal.",
                            entry.getDoctor().getFullName()),
                    "QUEUE_UPDATE"
            );
        }

        return entry;
    }

    @Transactional
    public QueueEntry checkOutDirectly(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        Optional<QueueEntry> entryOpt = queueEntryRepository.findByAppointment(appointment);
        if (entryOpt.isPresent()) {
            QueueEntry entry = entryOpt.get();
            entry.setStatus(QueueStatus.COMPLETED);
            entry.setCompletedTime(LocalDateTime.now());
            return queueEntryRepository.save(entry);
        }

        return null;
    }

    @Transactional
    public QueueEntry skipPatient(Long queueId) {
        QueueEntry entry = queueEntryRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Queue entry not found: " + queueId));

        entry.setStatus(QueueStatus.SKIPPED);
        return queueEntryRepository.save(entry);
    }
}
