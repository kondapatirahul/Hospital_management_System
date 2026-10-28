package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.DoctorAvailabilityDto;
import com.healthcare.hospital.entity.Appointment;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.DoctorAvailability;
import com.healthcare.hospital.repository.AppointmentRepository;
import com.healthcare.hospital.repository.DoctorAvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorAvailabilityService(DoctorAvailabilityRepository availabilityRepository,
                                   AppointmentRepository appointmentRepository) {
        this.availabilityRepository = availabilityRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<DoctorAvailability> getDoctorUpcomingAvailability(Doctor doctor) {
        return availabilityRepository.findByDoctorAndAvailableDateGreaterThanEqualOrderByAvailableDateAscStartTimeAsc(
                doctor, LocalDate.now());
    }

    public List<DoctorAvailability> getDoctorAvailabilityByDate(Doctor doctor, LocalDate date) {
        return availabilityRepository.findByDoctorAndAvailableDateOrderByStartTimeAsc(doctor, date);
    }

    @Transactional
    public DoctorAvailability addAvailabilitySlot(Doctor doctor, DoctorAvailabilityDto dto) {
        LocalDate date = dto.getAvailableDate();
        LocalTime start = dto.getStartTime();
        LocalTime end = dto.getEndTime();

        if (date == null) {
            throw new IllegalArgumentException("Availability date is required.");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        // Prevent overlapping slots for the same doctor on the same date
        List<DoctorAvailability> existingSlots = availabilityRepository.findByDoctorAndAvailableDateOrderByStartTimeAsc(doctor, date);
        for (DoctorAvailability slot : existingSlots) {
            // Overlap condition: start < slot.endTime AND end > slot.startTime
            if (start.isBefore(slot.getEndTime()) && end.isAfter(slot.getStartTime())) {
                throw new IllegalArgumentException(String.format(
                        "Conflicting time slot: an existing schedule exists from %s to %s on %s.",
                        slot.getStartTime(), slot.getEndTime(), date));
            }
        }

        DoctorAvailability availability = new DoctorAvailability(
                doctor,
                date,
                start,
                end,
                dto.getStatus() != null && !dto.getStatus().isBlank() ? dto.getStatus() : "AVAILABLE",
                dto.getSlotNotes()
        );

        return availabilityRepository.save(availability);
    }

    @Transactional
    public void deleteAvailabilitySlot(Doctor doctor, Long slotId) {
        DoctorAvailability slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Availability slot not found: " + slotId));

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Unauthorized attempt to delete another doctor's availability slot.");
        }

        availabilityRepository.delete(slot);
    }

    /**
     * Returns formatted available slots for a given doctor and date,
     * taking into account configured DoctorAvailability slots and existing booked appointments.
     */
    public List<String> getAvailableSlotsForDoctorAndDate(Doctor doctor, LocalDate date) {
        List<DoctorAvailability> customSlots = availabilityRepository.findByDoctorAndAvailableDateOrderByStartTimeAsc(doctor, date);
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorAndAppointmentDate(doctor, date);

        List<String> bookedTimeStrings = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        for (Appointment appt : bookedAppointments) {
            if (appt.getAppointmentSlot() != null) {
                bookedTimeStrings.add(appt.getAppointmentSlot().trim().toUpperCase());
            } else if (appt.getAppointmentTime() != null) {
                bookedTimeStrings.add(appt.getAppointmentTime().format(timeFormatter).toUpperCase());
            }
        }

        List<String> result = new ArrayList<>();

        if (!customSlots.isEmpty()) {
            for (DoctorAvailability slot : customSlots) {
                if ("AVAILABLE".equalsIgnoreCase(slot.getStatus())) {
                    // Generate 30-minute intervals between start and end
                    LocalTime current = slot.getStartTime();
                    while (current.plusMinutes(30).isBefore(slot.getEndTime()) || current.plusMinutes(30).equals(slot.getEndTime())) {
                        String slotStr = current.format(timeFormatter).toUpperCase();
                        if (!bookedTimeStrings.contains(slotStr)) {
                            result.add(slotStr);
                        }
                        current = current.plusMinutes(30);
                    }
                }
            }
        }

        // If no custom slots configured for this date, fall back to standard clinic slots minus booked appointments
        if (result.isEmpty() && customSlots.isEmpty()) {
            List<String> standardSlots = List.of(
                    "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM",
                    "11:30 AM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM"
            );
            for (String slot : standardSlots) {
                if (!bookedTimeStrings.contains(slot.toUpperCase())) {
                    result.add(slot);
                }
            }
        }

        return result;
    }
}
