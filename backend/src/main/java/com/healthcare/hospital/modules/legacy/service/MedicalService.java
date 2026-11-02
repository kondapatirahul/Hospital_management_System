package com.healthcare.hospital.modules.legacy.service;

import com.healthcare.hospital.modules.legacy.entity.LabReport;
import com.healthcare.hospital.modules.legacy.entity.MedicalRecord;
import com.healthcare.hospital.modules.legacy.entity.Patient;
import com.healthcare.hospital.modules.legacy.entity.Prescription;
import com.healthcare.hospital.modules.legacy.repository.LabReportRepository;
import com.healthcare.hospital.modules.legacy.repository.MedicalRecordRepository;
import com.healthcare.hospital.modules.legacy.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabReportRepository labReportRepository;

    public MedicalService(MedicalRecordRepository medicalRecordRepository,
                          PrescriptionRepository prescriptionRepository,
                          LabReportRepository labReportRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.labReportRepository = labReportRepository;
    }

    public List<MedicalRecord> getMedicalRecords(Patient patient) {
        return medicalRecordRepository.findByPatientOrderByVisitDateDesc(patient);
    }

    public List<Prescription> getPrescriptions(Patient patient) {
        return prescriptionRepository.findByPatientOrderByPrescriptionDateDesc(patient);
    }

    public List<LabReport> getLabReports(Patient patient) {
        return labReportRepository.findByPatientOrderByRequestedDateDesc(patient);
    }
}
