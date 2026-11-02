package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    List<SystemSetting> findAllByOrderByCategoryAscSettingKeyAsc();
    List<SystemSetting> findByCategoryIgnoreCaseOrderBySettingKeyAsc(String category);
    Optional<SystemSetting> findBySettingKeyIgnoreCase(String settingKey);
    boolean existsBySettingKeyIgnoreCase(String settingKey);
}
