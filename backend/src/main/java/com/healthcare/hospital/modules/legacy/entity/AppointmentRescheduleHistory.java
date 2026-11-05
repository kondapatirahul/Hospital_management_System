package com.healthcare.hospital.modules.legacy.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointment_reschedule_history", indexes = {
        @Index(name = "idx_reschedule_history_appointment", columnList = "appointment_id, changed_at")
})
public class AppointmentRescheduleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "previous_date", nullable = false)
    private LocalDate previousDate;

    @Column(name = "previous_time", nullable = false)
    private LocalTime previousTime;

    @Column(name = "previous_slot", length = 30)
    private String previousSlot;

    @Column(name = "new_date", nullable = false)
    private LocalDate newDate;

    @Column(name = "new_time", nullable = false)
    private LocalTime newTime;

    @Column(name = "new_slot", nullable = false, length = 30)
    private String newSlot;

    @Column(length = 500)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    protected AppointmentRescheduleHistory() {
    }

    public AppointmentRescheduleHistory(Appointment appointment, LocalDate previousDate,
                                        LocalTime previousTime, String previousSlot,
                                        LocalDate newDate, LocalTime newTime, String newSlot,
                                        String reason) {
        this.appointment = appointment;
        this.previousDate = previousDate;
        this.previousTime = previousTime;
        this.previousSlot = previousSlot;
        this.newDate = newDate;
        this.newTime = newTime;
        this.newSlot = newSlot;
        this.reason = reason;
        this.changedAt = LocalDateTime.now();
    }
}
