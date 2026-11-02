package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.MedicineCategory;
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
