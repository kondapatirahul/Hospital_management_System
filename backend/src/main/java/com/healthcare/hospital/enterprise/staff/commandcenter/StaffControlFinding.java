package com.healthcare.hospital.enterprise.staff.commandcenter;

/** Result of evaluating one staff operational control. */
public record StaffControlFinding(String ruleId, String domain, String title, String status, String action, int priority, String evidence) { }
