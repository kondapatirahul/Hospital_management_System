package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.SurgeryBooking;
import com.healthcare.hospital.entity.SurgeryChecklistItem;
import com.healthcare.hospital.enums.ChecklistPhase;
import com.healthcare.hospital.enums.ChecklistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurgeryChecklistItemRepository extends JpaRepository<SurgeryChecklistItem, Long> {

    List<SurgeryChecklistItem> findBySurgeryBookingOrderByPhaseAscIdAsc(SurgeryBooking surgeryBooking);

    List<SurgeryChecklistItem> findBySurgeryBookingAndPhaseOrderByIdAsc(SurgeryBooking surgeryBooking, ChecklistPhase phase);

    long countBySurgeryBookingAndPhaseAndIsMandatoryTrueAndStatusNot(
            SurgeryBooking surgeryBooking, ChecklistPhase phase, ChecklistStatus status);
}
