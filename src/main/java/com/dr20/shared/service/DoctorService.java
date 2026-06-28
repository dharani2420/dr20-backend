package com.dr20.shared.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AvailabilityService availabilityService;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(String id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
    }

    public List<Doctor> search(String query) {
        var results = new LinkedHashSet<Doctor>();
        results.addAll(doctorRepository.findByNameContainingIgnoreCase(query));
        results.addAll(doctorRepository.findBySpecializationContainingIgnoreCase(query));
        return new ArrayList<>(results);
    }

    public List<String> getSlots(String doctorId, String date) {
        return availabilityService.getAvailableSlots(doctorId, date);
    }

    public Doctor createDoctor(Doctor doctor) {
        Doctor saved = doctorRepository.save(doctor);
        availabilityService.seedForDoctor(saved.getId(), 14);
        return saved;
    }
}
