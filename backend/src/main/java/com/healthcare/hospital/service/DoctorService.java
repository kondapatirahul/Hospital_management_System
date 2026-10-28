package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.Department;
import com.healthcare.hospital.entity.Doctor;
import com.healthcare.hospital.entity.User;
import com.healthcare.hospital.repository.DepartmentRepository;
import com.healthcare.hospital.repository.DoctorRepository;
import com.healthcare.hospital.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         DepartmentRepository departmentRepository,
                         UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentId(departmentId);
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        Optional<Doctor> byEmail = doctorRepository.findByEmailIgnoreCase(username.trim());
        if (byEmail.isPresent()) {
            return byEmail;
        }
        return userRepository.findByUsername(username.trim()).flatMap(doctorRepository::findByUser);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    @Transactional
    public void toggleDoctorAvailability(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));
        doctor.setAvailableToday(!doctor.isAvailableToday());
        doctorRepository.save(doctor);
    }

    @Transactional
    public void setDoctorAvailabilityToday(Doctor doctor, boolean availableToday) {
        doctor.setAvailableToday(availableToday);
        doctorRepository.save(doctor);
    }
}
