package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SurgicalTeamMemberDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.SurgeryStatus;
import com.healthcare.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class SurgicalTeamService {

    private static final Set<SurgeryStatus> INACTIVE_STATUSES = Set.of(SurgeryStatus.CANCELLED, SurgeryStatus.POSTPONED);

    private final SurgicalTeamMemberRepository teamMemberRepository;
    private final SurgeryBookingRepository bookingRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SurgeryAuditTrailRepository auditTrailRepository;

    public SurgicalTeamService(SurgicalTeamMemberRepository teamMemberRepository,
                               SurgeryBookingRepository bookingRepository,
                               DoctorRepository doctorRepository,
                               UserRepository userRepository,
                               SurgeryAuditTrailRepository auditTrailRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.bookingRepository = bookingRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.auditTrailRepository = auditTrailRepository;
    }

    public List<SurgicalTeamMember> getTeamForSurgery(SurgeryBooking surgeryBooking) {
        return teamMemberRepository.findBySurgeryBooking(surgeryBooking);
    }

    @Transactional
    public SurgicalTeamMember assignTeamMember(Long surgeryId, SurgicalTeamMemberDto dto, String username) {
        SurgeryBooking booking = bookingRepository.findById(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        if (booking.getStatus() == SurgeryStatus.COMPLETED || booking.getStatus() == SurgeryStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify team assignments for a surgery in status " + booking.getStatus());
        }

        LocalTime surgStart = booking.getPlannedStartTime();
        LocalTime surgEnd = booking.getPlannedEndTime();

        SurgicalTeamMember member;

        if (dto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + dto.getDoctorId()));

            // Check if already assigned to this surgery
            boolean alreadyAssigned = booking.getTeamMembers().stream()
                    .anyMatch(m -> m.getDoctor() != null && m.getDoctor().getId().equals(doctor.getId())
                            && m.getMemberRole().equalsIgnoreCase(dto.getMemberRole()));
            if (alreadyAssigned) {
                throw new IllegalStateException("Dr. " + doctor.getFullName() + " is already assigned to this surgery as " + dto.getMemberRole());
            }

            // Conflict detection across other surgeries on the same date
            List<SurgicalTeamMember> conflictingAssignments = teamMemberRepository.findConflictingDoctorAssignments(
                    doctor, booking.getPlannedDate(), INACTIVE_STATUSES);

            for (SurgicalTeamMember conf : conflictingAssignments) {
                SurgeryBooking otherSurg = conf.getSurgeryBooking();
                if (!otherSurg.getId().equals(booking.getId()) &&
                        isTimeOverlapping(surgStart, surgEnd, otherSurg.getPlannedStartTime(), otherSurg.getPlannedEndTime())) {
                    throw new IllegalStateException(String.format(
                            "Staff conflict: Dr. %s is already assigned to procedure '%s' (%s to %s) on %s.",
                            doctor.getFullName(), otherSurg.getProcedureName(),
                            otherSurg.getPlannedStartTime(), otherSurg.getPlannedEndTime(), booking.getPlannedDate()
                    ));
                }
            }

            member = new SurgicalTeamMember(booking, doctor, dto.getMemberRole().trim(), username);
        } else if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Staff user not found: " + dto.getUserId()));

            boolean alreadyAssigned = booking.getTeamMembers().stream()
                    .anyMatch(m -> m.getUser() != null && m.getUser().getId().equals(user.getId())
                            && m.getMemberRole().equalsIgnoreCase(dto.getMemberRole()));
            if (alreadyAssigned) {
                throw new IllegalStateException(user.getFullName() + " is already assigned to this surgery as " + dto.getMemberRole());
            }

            // Conflict detection across other surgeries on the same date
            List<SurgicalTeamMember> conflictingAssignments = teamMemberRepository.findConflictingUserAssignments(
                    user, booking.getPlannedDate(), INACTIVE_STATUSES);

            for (SurgicalTeamMember conf : conflictingAssignments) {
                SurgeryBooking otherSurg = conf.getSurgeryBooking();
                if (!otherSurg.getId().equals(booking.getId()) &&
                        isTimeOverlapping(surgStart, surgEnd, otherSurg.getPlannedStartTime(), otherSurg.getPlannedEndTime())) {
                    throw new IllegalStateException(String.format(
                            "Staff conflict: %s is already assigned to procedure '%s' (%s to %s) on %s.",
                            user.getFullName(), otherSurg.getProcedureName(),
                            otherSurg.getPlannedStartTime(), otherSurg.getPlannedEndTime(), booking.getPlannedDate()
                    ));
                }
            }

            member = new SurgicalTeamMember(booking, user, dto.getMemberRole().trim(), username);
        } else {
            throw new IllegalArgumentException("Either doctorId or userId must be provided for team assignment.");
        }

        SurgicalTeamMember saved = teamMemberRepository.save(member);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                booking, null, "TEAM_ASSIGNMENT", username,
                String.format("Assigned %s as %s to surgery %s",
                        saved.getMemberName(), saved.getMemberRole(), booking.getBookingNumber())
        );
        auditTrailRepository.save(audit);

        return saved;
    }

    @Transactional
    public void removeTeamMember(Long surgeryId, Long memberId, String username) {
        SurgeryBooking booking = bookingRepository.findById(surgeryId)
                .orElseThrow(() -> new IllegalArgumentException("Surgery booking not found: " + surgeryId));

        SurgicalTeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Team member not found: " + memberId));

        if (!member.getSurgeryBooking().getId().equals(booking.getId())) {
            throw new IllegalArgumentException("Team member does not belong to this surgery.");
        }

        if ("LEAD_SURGEON".equalsIgnoreCase(member.getMemberRole())) {
            throw new IllegalStateException("Lead surgeon cannot be removed from the surgical team. Update the booking directly if needed.");
        }

        String memberName = member.getMemberName();
        String role = member.getMemberRole();
        teamMemberRepository.delete(member);

        SurgeryAuditTrail audit = new SurgeryAuditTrail(
                booking, null, "TEAM_REMOVAL", username,
                String.format("Removed %s (%s) from surgery %s", memberName, role, booking.getBookingNumber())
        );
        auditTrailRepository.save(audit);
    }

    private boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
