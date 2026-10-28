package com.healthcare.hospital.enterprise.receptionist.repository;

import com.healthcare.hospital.enterprise.receptionist.entity.AppointmentWaitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/** Persistence queries for appointment waitlist management. */
public interface AppointmentWaitlistRepository extends JpaRepository<AppointmentWaitlist, Long> {
    List<AppointmentWaitlist> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<AppointmentWaitlist> findByAssignedToIgnoreCaseOrderByDueDateAsc(String assignedTo);
    List<AppointmentWaitlist> findByReferenceCodeContainingIgnoreCaseOrderByCreatedAtDesc(String referenceCode);
    List<AppointmentWaitlist> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date, String status);
    List<AppointmentWaitlist> findTop100ByOrderByCreatedAtDesc();

    @Query("select count(e) from AppointmentWaitlist e where upper(e.status) = upper(:status)")
    long countByStatus(@Param("status") String status);

    @Query("select e from AppointmentWaitlist e where " +
           "(:status is null or upper(e.status) = upper(:status)) and " +
           "(:assignee is null or lower(e.assignedTo) like lower(concat('%', :assignee, '%'))) " +
           "order by e.updatedAt desc")
    List<AppointmentWaitlist> search(@Param("status") String status, @Param("assignee") String assignee);
}
