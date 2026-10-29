package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.SurgeryAuditTrail;
import com.healthcare.hospital.entity.SurgeryBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurgeryAuditTrailRepository extends JpaRepository<SurgeryAuditTrail, Long> {

    List<SurgeryAuditTrail> findBySurgeryBookingOrderByTimestampDesc(SurgeryBooking surgeryBooking);
}
