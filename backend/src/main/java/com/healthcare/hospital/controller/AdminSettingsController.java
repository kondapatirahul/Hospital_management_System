package com.healthcare.hospital.controller;

import com.healthcare.hospital.entity.SystemSetting;
import com.healthcare.hospital.repository.SystemSettingRepository;
import com.healthcare.hospital.service.AuditEventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {
    private final SystemSettingRepository settings;
    private final AuditEventService audit;
    public AdminSettingsController(SystemSettingRepository settings, AuditEventService audit) {
        this.settings = settings; this.audit = audit;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String category, Model model) {
        var all = category == null || category.isBlank()
                ? settings.findAllByOrderByCategoryAscSettingKeyAsc()
                : settings.findByCategoryIgnoreCaseOrderBySettingKeyAsc(category.trim());
        model.addAttribute("settings", all);
        model.addAttribute("categoryFilter", category == null ? "" : category);
        model.addAttribute("settingCount", settings.count());
        return "admin/settings";
    }

    @PostMapping
    public String create(@RequestParam String settingKey, @RequestParam String settingValue,
                         @RequestParam(defaultValue = "GENERAL") String category,
                         @RequestParam(required = false) String description,
                         Principal principal, RedirectAttributes flash) {
        String key = settingKey == null ? "" : settingKey.trim();
        String value = settingValue == null ? "" : settingValue.trim();
        String group = category == null ? "GENERAL" : category.trim().toUpperCase();
        if (!key.matches("[A-Za-z][A-Za-z0-9_.-]{1,99}")) {
            flash.addFlashAttribute("errorMessage", "Key must start with a letter and contain only letters, numbers, dots, underscores or hyphens.");
            return "redirect:/admin/settings";
        }
        if (value.isBlank() || value.length() > 1000 || group.isBlank() || group.length() > 80) {
            flash.addFlashAttribute("errorMessage", "A value and valid category are required.");
            return "redirect:/admin/settings";
        }
        if (settings.existsBySettingKeyIgnoreCase(key)) {
            flash.addFlashAttribute("errorMessage", "That setting key already exists. Edit the existing value instead.");
            return "redirect:/admin/settings";
        }
        SystemSetting setting = new SystemSetting();
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setCategory(group);
        setting.setDescription(description == null ? null : description.trim().substring(0, Math.min(description.trim().length(), 500)));
        setting.setUpdatedBy(principal == null ? "system" : principal.getName());
        setting.setUpdatedAt(LocalDateTime.now());
        settings.save(setting);
        audit.record(setting.getUpdatedBy(), "CREATE", "SystemSetting", setting.getId(), "Created setting " + key);
        flash.addFlashAttribute("successMessage", "Setting created.");
        return "redirect:/admin/settings";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam String settingValue,
                         @RequestParam(required = false) String description,
                         Principal principal, RedirectAttributes flash) {
        SystemSetting setting = settings.findById(id).orElse(null);
        if (setting == null) {
            flash.addFlashAttribute("errorMessage", "Setting was not found.");
            return "redirect:/admin/settings";
        }
        String value = settingValue == null ? "" : settingValue.trim();
        if (value.isBlank() || value.length() > 1000) {
            flash.addFlashAttribute("errorMessage", "Setting value is required and must be under 1000 characters.");
            return "redirect:/admin/settings";
        }
        setting.setSettingValue(value);
        setting.setDescription(description == null ? null : description.trim().substring(0, Math.min(description.trim().length(), 500)));
        setting.setUpdatedBy(principal == null ? "system" : principal.getName());
        setting.setUpdatedAt(LocalDateTime.now());
        settings.save(setting);
        audit.record(setting.getUpdatedBy(), "UPDATE", "SystemSetting", id, "Updated setting " + setting.getSettingKey());
        flash.addFlashAttribute("successMessage", "Setting updated.");
        return "redirect:/admin/settings";
    }
}
