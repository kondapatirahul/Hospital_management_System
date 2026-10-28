package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Persisted operational setting editable by administrators. Secrets must never be stored here. */
@Entity
@Table(name = "system_settings", uniqueConstraints = @UniqueConstraint(name = "uk_setting_key", columnNames = "setting_key"),
       indexes = @Index(name = "idx_setting_category", columnList = "category"))
public class SystemSetting {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;
    @Column(name = "setting_value", nullable = false, length = 1000)
    private String settingValue;
    @Column(nullable = false, length = 80)
    private String category = "GENERAL";
    @Column(length = 500)
    private String description;
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Version
    private Long version;

    public SystemSetting() {}
    public Long getId() { return id; }
    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String v) { settingKey = v; }
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String v) { settingValue = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { category = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String v) { updatedBy = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { updatedAt = v; }
    public Long getVersion() { return version; }
}
