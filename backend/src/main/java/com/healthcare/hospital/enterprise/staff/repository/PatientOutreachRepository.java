package com.healthcare.hospital.enterprise.staff.repository;

import com.healthcare.hospital.enterprise.staff.entity.PatientOutreach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Query boundary for outreach worklists, patient history and escalation queues. */
public interface PatientOutreachRepository extends JpaRepository<PatientOutreach, Long> {
    Optional<PatientOutreach> findByReferenceCode(String referenceCode);
    boolean existsByReferenceCode(String referenceCode);
    List<PatientOutreach> findTop300ByOrderByUpdatedAtDesc();
    List<PatientOutreach> findByPatientReferenceIgnoreCaseOrderByCreatedAtDesc(String patientReference);
    List<PatientOutreach> findByDischargeChecklistIdOrderByCreatedAtDesc(Long checklistId);
    long countByStatus(String status);
    long countByRedFlagSymptomsTrueAndStatusNot(String status);
    long countByNextContactDateAndStatusNot(LocalDate date, String status);

    @Query("select o from PatientOutreach o where " +
        "(:status is null or o.status=:status) and " +
        "(:team is null or lower(o.assignedTeam)=lower(:team)) and " +
        "(:priority is null or o.priority=:priority) and " +
        "(:escalation is null or o.escalationLevel=:escalation) and " +
        "(:fromDate is null or o.nextContactDate>=:fromDate) and " +
        "(:toDate is null or o.nextContactDate<=:toDate) and " +
        "(:term is null or lower(o.referenceCode) like lower(concat('%',:term,'%')) or " +
        "lower(o.patientReference) like lower(concat('%',:term,'%')) or " +
        "lower(o.patientName) like lower(concat('%',:term,'%')) or " +
        "lower(o.purpose) like lower(concat('%',:term,'%')) or " +
        "lower(o.assignedTo) like lower(concat('%',:term,'%'))) " +
        "order by o.redFlagSymptoms desc, o.supervisorReviewRequired desc, o.nextContactDate asc, o.updatedAt desc")
    List<PatientOutreach> search(@Param("status") String status, @Param("team") String team,
        @Param("priority") String priority, @Param("escalation") String escalation,
        @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
        @Param("term") String term);

    @Query("select o from PatientOutreach o where o.nextContactDate < :today and o.status not in ('CLOSED','CANCELLED') order by o.nextContactDate asc")
    List<PatientOutreach> overdue(@Param("today") LocalDate today);

    @Query("select o from PatientOutreach o where (o.redFlagSymptoms=true or o.clinicianHandoffRequired=true or o.supervisorReviewRequired=true or o.escalationLevel in ('PRIORITY','URGENT','EMERGENCY')) and o.status not in ('CLOSED','CANCELLED') order by o.redFlagSymptoms desc, o.updatedAt asc")
    List<PatientOutreach> safetyQueue();

    @Query("select o.status, count(o) from PatientOutreach o group by o.status")
    List<Object[]> statusCounts();
    @Query("select o.outcome, count(o) from PatientOutreach o group by o.outcome")
    List<Object[]> outcomeCounts();
    @Query("select o.assignedTeam, count(o) from PatientOutreach o where o.status not in ('CLOSED','CANCELLED') group by o.assignedTeam order by count(o) desc")
    List<Object[]> workloadByTeam();
}
