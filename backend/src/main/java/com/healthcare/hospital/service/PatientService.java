package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.PatientProfileDto;
import com.healthcare.hospital.dto.PatientRegistrationDto;
import com.healthcare.hospital.entity.Patient;
import com.healthcare.hospital.entity.User;
import com.healthcare.hospital.enums.RoleType;
import com.healthcare.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;

    public PatientService(PatientRepository patientRepository, UserService userService) {
        this.patientRepository = patientRepository;
        this.userService = userService;
    }

    public synchronized String generateNextPatientId() {
        int currentYear = LocalDate.now().getYear();
        long nextNum = 1;
        Optional<Patient> topPatient = patientRepository.findTopByOrderByIdDesc();
        if (topPatient.isPresent() && topPatient.get().getId() != null) {
            nextNum = topPatient.get().getId() + 1;
        }
        return String.format("PAT-%d-%04d", currentYear, nextNum);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAllByOrderByIdDesc();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    public Optional<Patient> getPatientByUsername(String username) {
        return patientRepository.findByUserUsername(username);
    }

    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.searchPatients(keyword.trim());
    }

    @Transactional
    public Patient registerPatient(PatientRegistrationDto dto) {
        User user = null;
        if (dto.isCreateAccount() && dto.getEmail() != null && !dto.getEmail().trim().isEmpty() && dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user = userService.registerUser(dto.getEmail().trim(), dto.getPassword(), dto.getFullName().trim(), RoleType.ROLE_PATIENT);
        }

        String patientId = generateNextPatientId();
        Patient patient = new Patient(
                patientId,
                user,
                dto.getFullName().trim(),
                dto.getEmail() != null ? dto.getEmail().trim() : null,
                dto.getPhone().trim(),
                dto.getDateOfBirth(),
                dto.getGender(),
                dto.getBloodGroup(),
                dto.getAddress(),
                dto.getEmergencyContactName(),
                dto.getEmergencyContactPhone(),
                dto.getAllergies(),
                dto.getChronicConditions()
        );

        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatientProfile(Long patientId, PatientProfileDto dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + patientId));

        patient.setFullName(dto.getFullName());
        patient.setPhone(dto.getPhone());
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            patient.setEmail(dto.getEmail().trim());
        }
        patient.setAddress(dto.getAddress());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setAllergies(dto.getAllergies());
        patient.setChronicConditions(dto.getChronicConditions());

        if (patient.getUser() != null) {
            patient.getUser().setFullName(dto.getFullName());
        }

        return patientRepository.save(patient);
    }
}
