package com.healthcare.hospital.shared.dto;

import java.time.LocalDate;
import java.util.List;

public record MedicationScheduleDto(LocalDate startDate, LocalDate endDate, List<String> times) {
}
