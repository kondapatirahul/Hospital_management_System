package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.LabReport;
import com.healthcare.hospital.entity.MedicalRecord;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.Prescription;
import com.healthcare.hospital.repository.LabReportRepository;
import com.healthcare.hospital.repository.MedicalRecordRepository;
import com.healthcare.hospital.repository.PrescriptionRepository;
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
