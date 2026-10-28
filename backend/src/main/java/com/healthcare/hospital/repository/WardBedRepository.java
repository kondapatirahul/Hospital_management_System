package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.WardBed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WardBedRepository extends JpaRepository<WardBed, Long> {
    List<WardBed> findAllByOrderByWardNameAscBedNumberAsc();
    List<WardBed> findByWardNameIgnoreCaseOrderByBedNumberAsc(String wardName);
    List<WardBed> findByStatusOrderByWardNameAscBedNumberAsc(String status);
    boolean existsByWardNameIgnoreCaseAndBedNumberIgnoreCase(String wardName, String bedNumber);
    boolean existsByWardNameIgnoreCaseAndBedNumberIgnoreCaseAndIdNot(String wardName, String bedNumber, Long id);
    long countByStatus(String status);
}
