package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.OperationTheatre;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.SurgeryBooking;
import com.healthcare.hospital.enums.SurgeryPriority;
import com.healthcare.hospital.enums.SurgeryStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurgeryBookingRepository extends JpaRepository<SurgeryBooking, Long> {

    Optional<SurgeryBooking> findByBookingNumber(String bookingNumber);

    Optional<SurgeryBooking> findTopByOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sb FROM SurgeryBooking sb WHERE sb.id = :id")
    Optional<SurgeryBooking> findByIdWithLock(@Param("id") Long id);

    List<SurgeryBooking> findByTheatreAndPlannedDateAndStatusNotIn(
            OperationTheatre theatre, LocalDate plannedDate, Collection<SurgeryStatus> excludedStatuses);

    List<SurgeryBooking> findByLeadSurgeonAndPlannedDateAndStatusNotIn(
            Doctor leadSurgeon, LocalDate plannedDate, Collection<SurgeryStatus> excludedStatuses);

    List<SurgeryBooking> findByPlannedDateOrderByPlannedStartTimeAsc(LocalDate date);

    List<SurgeryBooking> findByPlannedDateBetweenOrderByPlannedDateAscPlannedStartTimeAsc(
            LocalDate startDate, LocalDate endDate);

    List<SurgeryBooking> findByPatientOrderByPlannedDateDesc(Patient patient);

    List<SurgeryBooking> findByLeadSurgeonOrderByPlannedDateDesc(Doctor doctor);

    long countByStatus(SurgeryStatus status);

    long countByPlannedDate(LocalDate plannedDate);

    @Query("SELECT sb FROM SurgeryBooking sb WHERE " +
           "(:search IS NULL OR LOWER(sb.bookingNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(sb.procedureName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(sb.patient.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(sb.leadSurgeon.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(sb.theatre.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:theatreId IS NULL OR sb.theatre.id = :theatreId) " +
           "AND (:surgeonId IS NULL OR sb.leadSurgeon.id = :surgeonId) " +
           "AND (:status IS NULL OR sb.status = :status) " +
           "AND (:priority IS NULL OR sb.priority = :priority) " +
           "AND (:startDate IS NULL OR sb.plannedDate >= :startDate) " +
           "AND (:endDate IS NULL OR sb.plannedDate <= :endDate)")
    Page<SurgeryBooking> searchSurgeries(@Param("search") String search,
                                         @Param("theatreId") Long theatreId,
                                         @Param("surgeonId") Long surgeonId,
                                         @Param("status") SurgeryStatus status,
                                         @Param("priority") SurgeryPriority priority,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         Pageable pageable);
}
