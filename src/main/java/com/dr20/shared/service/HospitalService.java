package com.dr20.shared.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final DoctorRepository doctorRepository;

    public List<Map<String, Object>> getAllHospitals() {
        return doctorRepository.findAll().stream()
                .filter(d -> d.getClinicName() != null && !d.getClinicName().isBlank())
                .collect(Collectors.groupingBy(Doctor::getClinicName, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(e -> toHospital(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getHospitalBySlug(String slug) {
        return getAllHospitals().stream()
                .filter(h -> slug.equals(h.get("id")))
                .findFirst()
                .map(h -> {
                    String clinicName = (String) h.get("name");
                    return getHospital(clinicName);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
    }

    public List<Doctor> getDoctorsBySlug(String slug) {
        return getAllHospitals().stream()
                .filter(h -> slug.equals(h.get("id")))
                .findFirst()
                .map(h -> getDoctorsAtHospital((String) h.get("name")))
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
    }

    public Map<String, Object> getHospital(String clinicName) {
        List<Doctor> doctors = doctorRepository.findAll().stream()
                .filter(d -> clinicName.equalsIgnoreCase(d.getClinicName()))
                .collect(Collectors.toList());
        if (doctors.isEmpty()) {
            throw new ResourceNotFoundException("Hospital not found");
        }
        Map<String, Object> hospital = toHospital(clinicName, doctors);
        hospital.put("doctors", doctors);
        return hospital;
    }

    public List<Doctor> getDoctorsAtHospital(String clinicName) {
        List<Doctor> doctors = doctorRepository.findAll().stream()
                .filter(d -> clinicName.equalsIgnoreCase(d.getClinicName()))
                .collect(Collectors.toList());
        if (doctors.isEmpty()) {
            throw new ResourceNotFoundException("Hospital not found");
        }
        return doctors;
    }

    private Map<String, Object> toHospital(String clinicName, List<Doctor> doctors) {
        Doctor first = doctors.get(0);
        Map<String, Object> hospital = new HashMap<>();
        hospital.put("id", slugify(clinicName));
        hospital.put("name", clinicName);
        hospital.put("address", first.getClinicAddress());
        hospital.put("latitude", first.getLatitude());
        hospital.put("longitude", first.getLongitude());
        hospital.put("doctorCount", doctors.size());
        return hospital;
    }

    private String slugify(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
