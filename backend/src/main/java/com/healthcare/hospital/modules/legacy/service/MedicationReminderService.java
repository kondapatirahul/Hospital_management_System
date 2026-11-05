package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.modules.legacy.entity.MedicationReminder;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.modules.legacy.entity.Prescription;
import com.healthcare.hospital.modules.legacy.repository.MedicationReminderRepository;
import com.healthcare.hospital.modules.legacy.repository.PrescriptionRepository;
import com.healthcare.hospital.shared.dto.MedicationScheduleDto;
import com.healthcare.hospital.shared.enums.MedicationReminderStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MedicationReminderService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    private static final Pattern DURATION_PATTERN =
            Pattern.compile("(?i)\\b(\\d+)\\s*(day|days|week|weeks|month|months)\\b");

    private final PrescriptionRepository prescriptionRepository;
    private final MedicationReminderRepository reminderRepository;

    public MedicationReminderService(PrescriptionRepository prescriptionRepository,
                                    MedicationReminderRepository reminderRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.reminderRepository = reminderRepository;
    }

    @Transactional
    public List<MedicationReminder> getPatientReminders(Patient patient) {
        createRemindersForPatient(patient, LocalDate.now());
        return reminderRepository.findTop100ByPatientOrderByScheduledAtDesc(patient).stream()
                .filter(reminder -> reminder.getStatus() != MedicationReminderStatus.PENDING
                        || reminder.getScheduledAt().toLocalDate().isEqual(LocalDate.now()))
                .toList();
    }

    @Transactional
    @Scheduled(cron = "0 5 0 * * *")
    public void createDailyReminders() {
        LocalDate today = LocalDate.now();
        for (Prescription prescription : prescriptionRepository.findByStatusIgnoreCase("Active")) {
            createRemindersForPrescription(prescription, today);
        }
    }

    @Transactional
    public MedicationReminder recordReminder(Long reminderId, Patient patient, MedicationReminderStatus status) {
        if (status != MedicationReminderStatus.TAKEN && status != MedicationReminderStatus.SKIPPED) {
            throw new IllegalArgumentException("A reminder can only be marked as taken or skipped.");
        }
        MedicationReminder reminder = reminderRepository.findByIdAndPatientForUpdate(reminderId, patient)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found."));
        if (reminder.getStatus() != MedicationReminderStatus.PENDING) {
            throw new IllegalArgumentException("This reminder has already been recorded.");
        }
        if (reminder.getScheduledAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("This medication reminder is not due yet.");
        }
        reminder.record(status, LocalDateTime.now());
        return reminderRepository.save(reminder);
    }

    public List<MedicationScheduleDto> getSchedules(List<Prescription> prescriptions) {
        return prescriptions.stream().map(prescription -> {
            LocalDate startDate = prescription.getPrescriptionDate();
            LocalDate endDate = calculateEndDate(prescription);
            List<String> times = scheduledTimes(prescription.getFrequency()).stream()
                    .map(time -> TIME_FORMAT.format(time))
                    .toList();
            return new MedicationScheduleDto(startDate, endDate, times);
        }).toList();
    }

    static List<LocalTime> scheduledTimes(String frequency) {
        if (frequency == null || frequency.isBlank()) return List.of();
        String normalized = frequency.toLowerCase(Locale.ROOT);
        if (normalized.contains("prn") || normalized.contains("as needed")) return List.of();

        LinkedHashSet<LocalTime> explicitTimes = new LinkedHashSet<>();
        if (containsAny(normalized, "morning", "breakfast")) explicitTimes.add(LocalTime.of(8, 0));
        if (containsAny(normalized, "afternoon", "lunch", "noon")) explicitTimes.add(LocalTime.of(13, 0));
        if (containsAny(normalized, "evening", "night", "bedtime", "dinner", "supper")) {
            explicitTimes.add(LocalTime.of(20, 0));
        }
        if (!explicitTimes.isEmpty()) return List.copyOf(explicitTimes);

        if (containsAny(normalized, "four times", "four times a day", "qid")) {
            return List.of(LocalTime.of(8, 0), LocalTime.of(13, 0), LocalTime.of(17, 0), LocalTime.of(21, 0));
        }
        if (containsAny(normalized, "three times", "thrice", "tid")) {
            return List.of(LocalTime.of(8, 0), LocalTime.of(13, 0), LocalTime.of(20, 0));
        }
        if (containsAny(normalized, "twice", "two times", "bid")) {
            return List.of(LocalTime.of(8, 0), LocalTime.of(20, 0));
        }
        if (containsAny(normalized, "once daily", "once a day", "one time daily", "daily", "every day", "qd", "od")) {
            return List.of(LocalTime.of(8, 0));
        }
        return List.of();
    }

    static LocalDate calculateEndDate(Prescription prescription) {
        LocalDate start = prescription.getPrescriptionDate();
        if (start == null || prescription.getDuration() == null) return null;
        Matcher matcher = DURATION_PATTERN.matcher(prescription.getDuration());
        if (!matcher.find()) return null;
        long amount;
        try {
            amount = Long.parseLong(matcher.group(1));
        } catch (NumberFormatException exception) {
            return null;
        }
        if (amount < 1) return null;
        String unit = matcher.group(2).toLowerCase(Locale.ROOT);
        return switch (unit) {
            case "day", "days" -> start.plusDays(amount - 1);
            case "week", "weeks" -> start.plusWeeks(amount).minusDays(1);
            case "month", "months" -> start.plusMonths(amount).minusDays(1);
            default -> null;
        };
    }

    private void createRemindersForPatient(Patient patient, LocalDate date) {
        for (Prescription prescription : prescriptionRepository.findByPatientOrderByPrescriptionDateDesc(patient)) {
            createRemindersForPrescription(prescription, date);
        }
    }

    private void createRemindersForPrescription(Prescription candidate, LocalDate date) {
        if (!isActiveForDate(candidate, date) || scheduledTimes(candidate.getFrequency()).isEmpty()) return;

        Prescription prescription = prescriptionRepository.findByIdWithLock(candidate.getId())
                .orElseThrow(() -> new IllegalStateException("Prescription disappeared while creating reminders."));
        if (!isActiveForDate(prescription, date)) return;
        for (LocalTime time : scheduledTimes(prescription.getFrequency())) {
            LocalDateTime scheduledAt = LocalDateTime.of(date, time);
            if (!reminderRepository.existsByPrescriptionAndScheduledAt(prescription, scheduledAt)) {
                reminderRepository.save(new MedicationReminder(prescription.getPatient(), prescription, scheduledAt));
            }
        }
    }

    private boolean isActiveForDate(Prescription prescription, LocalDate date) {
        if (prescription.getPrescriptionDate() == null
                || prescription.getPrescriptionDate().isAfter(date)
                || !"active".equalsIgnoreCase(prescription.getStatus())) {
            return false;
        }
        LocalDate endDate = calculateEndDate(prescription);
        return endDate == null || !endDate.isBefore(date);
    }

    private static boolean containsAny(String value, String... options) {
        for (String option : options) {
            if (value.contains(option)) return true;
        }
        return false;
    }
}
