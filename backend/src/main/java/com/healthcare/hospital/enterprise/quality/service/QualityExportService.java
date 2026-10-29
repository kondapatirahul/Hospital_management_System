package com.healthcare.hospital.enterprise.quality.service;

import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.repository.QualityReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/** Export-focused read operations; keeps export selection and CSV serialization in one place. */
@Service
@Transactional(readOnly=true)
public class QualityExportService {
    private final QualityReviewRepository repository;
    private final QualityReviewService service;

    public QualityExportService(QualityReviewRepository repository, QualityReviewService service) {
        this.repository=repository;
        this.service=service;
    }

    public String exportCurrentRegister() {
        List<QualityReview> records=repository.findTop200ByOrderByUpdatedAtDesc();
        return service.csvExport(records);
    }

    public String exportOverdueRegister() {
        return service.csvExport(service.overdueCases());
    }

    public String exportPatientHistory(String patientReference) {
        return service.csvExport(service.patientHistory(patientReference));
    }

    public String exportDepartmentRegister(String department) {
        String normalized=department==null?"":department.trim();
        if(normalized.isBlank()) throw new IllegalArgumentException("Department is required.");
        List<QualityReview> rows=repository.findTop200ByOrderByUpdatedAtDesc().stream()
            .filter(r->r.getDepartment()!=null&&r.getDepartment().equalsIgnoreCase(normalized)).toList();
        return service.csvExport(rows);
    }

    public String exportDateWindow(LocalDate from, LocalDate to) {
        if(from==null||to==null) throw new IllegalArgumentException("Both start and end dates are required.");
        if(from.isAfter(to)) throw new IllegalArgumentException("Start date must be on or before end date.");
        List<QualityReview> rows=repository.findTop200ByOrderByUpdatedAtDesc().stream()
            .filter(r->r.getEventDate()!=null&&!r.getEventDate().isBefore(from)&&!r.getEventDate().isAfter(to)).toList();
        return service.csvExport(rows);
    }
}
