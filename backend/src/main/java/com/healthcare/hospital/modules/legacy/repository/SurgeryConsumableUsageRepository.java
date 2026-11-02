package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.SurgeryBooking;
import com.healthcare.hospital.modules.legacy.entity.SurgeryConsumableUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SurgeryConsumableUsageRepository extends JpaRepository<SurgeryConsumableUsage, Long> {

    List<SurgeryConsumableUsage> findBySurgeryBooking(SurgeryBooking surgeryBooking);

    List<SurgeryConsumableUsage> findBySurgeryBookingAndIsDeductedFalse(SurgeryBooking surgeryBooking);

    @Query("SELECT COALESCE(SUM(c.totalCost), 0) FROM SurgeryConsumableUsage c WHERE c.surgeryBooking = :surgeryBooking")
    BigDecimal calculateTotalCostForSurgery(@Param("surgeryBooking") SurgeryBooking surgeryBooking);
}
