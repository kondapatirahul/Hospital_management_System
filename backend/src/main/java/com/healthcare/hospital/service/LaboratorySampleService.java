package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.LabReport;
import com.healthcare.hospital.entity.LabSample;
import com.healthcare.hospital.repository.LabReportRepository;
import com.healthcare.hospital.repository.LabSampleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/** Enforces a predictable sample lifecycle: ORDERED -> COLLECTED -> RECEIVED -> PROCESSING. */
@Service
public class LaboratorySampleService {
    private static final List<String> ALLOWED_SAMPLE_TYPES =
            List.of("BLOOD", "URINE", "STOOL", "SWAB", "TISSUE", "SPUTUM", "OTHER");
    private final LabSampleRepository samples;
    private final LabReportRepository reports;

    public LaboratorySampleService(LabSampleRepository samples, LabReportRepository reports) {
        this.samples = samples;
        this.reports = reports;
    }

    @Transactional
    public LabSample create(Long reportId, String sampleCode, String sampleType) {
        LabReport report = reports.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Laboratory order was not found."));
        String code = clean(sampleCode, 32, "Sample code");
        String type = clean(sampleType, 40, "Sample type").toUpperCase(Locale.ROOT);
        if (!ALLOWED_SAMPLE_TYPES.contains(type)) {
            throw new IllegalArgumentException("Choose a supported sample type.");
        }
        if (samples.existsBySampleCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("That sample code already exists.");
        }
        return samples.save(new LabSample(code, report, type));
    }

    @Transactional
    public LabSample collect(Long id, String collectedBy, String notes) {
        LabSample sample = require(id);
        requireStatus(sample, "ORDERED");
        sample.setCollectedBy(clean(collectedBy, 100, "Collected by"));
        sample.setCollectedAt(LocalDateTime.now());
        sample.setHandlingNotes(optional(notes, 1000));
        sample.setStatus("COLLECTED");
        return samples.save(sample);
    }

    @Transactional
    public LabSample receive(Long id) {
        LabSample sample = require(id);
        requireStatus(sample, "COLLECTED");
        sample.setReceivedAt(LocalDateTime.now());
        sample.setStatus("RECEIVED");
        return samples.save(sample);
    }

    @Transactional
    public LabSample startProcessing(Long id) {
        LabSample sample = require(id);
        requireStatus(sample, "RECEIVED");
        sample.setStatus("PROCESSING");
        return samples.save(sample);
    }

    @Transactional
    public LabSample reject(Long id, String reason) {
        LabSample sample = require(id);
        if (List.of("REJECTED", "COMPLETED").contains(sample.getStatus())) {
            throw new IllegalStateException("A rejected or completed sample cannot be rejected again.");
        }
        sample.setStatus("REJECTED");
        sample.setRejectedAt(LocalDateTime.now());
        sample.setRejectionReason(clean(reason, 255, "Rejection reason"));
        return samples.save(sample);
    }

    @Transactional(readOnly = true)
    public List<LabSample> all() { return samples.findAllByOrderByCreatedAtDesc(); }

    @Transactional(readOnly = true)
    public long count(String status) { return samples.countByStatus(status); }

    private LabSample require(Long id) {
        return samples.findById(id).orElseThrow(() -> new IllegalArgumentException("Sample was not found."));
    }
    private void requireStatus(LabSample sample, String expected) {
        if (!expected.equals(sample.getStatus())) {
            throw new IllegalStateException("Sample must be " + expected + " before this action.");
        }
    }
    private String clean(String value, int max, String label) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required.");
        String cleaned = value.trim();
        if (cleaned.length() > max) throw new IllegalArgumentException(label + " is too long.");
        return cleaned;
    }
    private String optional(String value, int max) {
        if (value == null || value.isBlank()) return null;
        String cleaned = value.trim();
        if (cleaned.length() > max) throw new IllegalArgumentException("Notes are too long.");
        return cleaned;
    }
}
