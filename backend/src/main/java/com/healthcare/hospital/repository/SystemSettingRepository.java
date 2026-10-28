package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    List<SystemSetting> findAllByOrderByCategoryAscSettingKeyAsc();
    List<SystemSetting> findByCategoryIgnoreCaseOrderBySettingKeyAsc(String category);
    Optional<SystemSetting> findBySettingKeyIgnoreCase(String settingKey);
    boolean existsBySettingKeyIgnoreCase(String settingKey);
}
