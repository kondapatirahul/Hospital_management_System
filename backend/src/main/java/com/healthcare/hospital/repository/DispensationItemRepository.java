package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.DispensationItem;
import com.healthcare.hospital.entity.PrescriptionDispensation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DispensationItemRepository extends JpaRepository<DispensationItem, Long> {

    List<DispensationItem> findByDispensation(PrescriptionDispensation dispensation);

    @Query("SELECT di.medicine.medicineCode, di.medicine.name, di.medicine.dosageForm, " +
           "SUM(di.quantityDispensed), SUM(di.totalPrice) " +
           "FROM DispensationItem di " +
           "WHERE (:startDate IS NULL OR di.dispensation.dispensationDate >= :startDate) " +
           "AND (:endDate IS NULL OR di.dispensation.dispensationDate <= :endDate) " +
           "GROUP BY di.medicine.medicineCode, di.medicine.name, di.medicine.dosageForm " +
           "ORDER BY SUM(di.quantityDispensed) DESC")
    List<Object[]> getMedicineDispensingSummaries(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
