package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineCategoryRepository extends JpaRepository<MedicineCategory, Long> {
    Optional<MedicineCategory> findByCodeIgnoreCase(String code);
    Optional<MedicineCategory> findByNameIgnoreCase(String name);
    List<MedicineCategory> findByActiveTrueOrderByNameAsc();
    boolean existsByCodeIgnoreCase(String code);
}
