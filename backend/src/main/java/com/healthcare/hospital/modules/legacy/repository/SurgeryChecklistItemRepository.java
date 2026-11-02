package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.SurgeryBooking;
import com.healthcare.hospital.modules.legacy.entity.SurgeryChecklistItem;
import com.healthcare.hospital.shared.enums.ChecklistPhase;
import com.healthcare.hospital.shared.enums.ChecklistStatus;
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
