package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.SurgeryBooking;
import com.healthcare.hospital.entity.SurgicalTeamMember;
import com.healthcare.hospital.entity.User;
import com.healthcare.hospital.enums.SurgeryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface SurgicalTeamMemberRepository extends JpaRepository<SurgicalTeamMember, Long> {

    List<SurgicalTeamMember> findBySurgeryBooking(SurgeryBooking surgeryBooking);

    List<SurgicalTeamMember> findByDoctor(Doctor doctor);

    List<SurgicalTeamMember> findByUser(User user);

    @Query("SELECT tm FROM SurgicalTeamMember tm WHERE tm.doctor = :doctor " +
           "AND tm.surgeryBooking.plannedDate = :plannedDate " +
           "AND tm.surgeryBooking.status NOT IN :excludedStatuses")
    List<SurgicalTeamMember> findConflictingDoctorAssignments(
            @Param("doctor") Doctor doctor,
            @Param("plannedDate") LocalDate plannedDate,
            @Param("excludedStatuses") Collection<SurgeryStatus> excludedStatuses);

    @Query("SELECT tm FROM SurgicalTeamMember tm WHERE tm.user = :user " +
           "AND tm.surgeryBooking.plannedDate = :plannedDate " +
           "AND tm.surgeryBooking.status NOT IN :excludedStatuses")
    List<SurgicalTeamMember> findConflictingUserAssignments(
            @Param("user") User user,
            @Param("plannedDate") LocalDate plannedDate,
            @Param("excludedStatuses") Collection<SurgeryStatus> excludedStatuses);
}
