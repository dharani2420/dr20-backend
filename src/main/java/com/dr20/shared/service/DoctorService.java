package com.dr20.shared.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.ReviewRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AvailabilityService availabilityService;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public List<Doctor> getAllDoctors(String gender, Double minRating, Boolean availableToday,
                                      Double maxFee, Double minFee, String clinicType,
                                      String specialization, String sort,
                                      Double latitude, Double longitude) {
        List<Doctor> doctors = doctorRepository.findAll().stream()
                .filter(d -> minRating == null || (d.getRating() != null && d.getRating() >= minRating))
                .filter(d -> availableToday == null || !availableToday || Boolean.TRUE.equals(d.getIsAvailable()))
                .filter(d -> gender == null || gender.isBlank() || matchesGender(d, gender))
                .filter(d -> maxFee == null || (d.getConsultationFee() != null && d.getConsultationFee() <= maxFee))
                .filter(d -> minFee == null || (d.getConsultationFee() != null && d.getConsultationFee() >= minFee))
                .filter(d -> clinicType == null || clinicType.isBlank()
                        || clinicType.equalsIgnoreCase(d.getClinicType()))
                .filter(d -> specialization == null || specialization.isBlank()
                        || (d.getSpecialization() != null
                        && d.getSpecialization().toLowerCase().contains(specialization.toLowerCase())))
                .collect(Collectors.toList());

        if (latitude != null && longitude != null) {
            doctors.forEach(d -> d.setDistanceKm(calculateDistanceKm(latitude, longitude, d)));
        }

        if ("nearest".equalsIgnoreCase(sort) && latitude != null && longitude != null) {
            doctors.sort(Comparator.comparing(Doctor::getDistanceKm, Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("topRated".equalsIgnoreCase(sort)) {
            doctors.sort(Comparator.comparing(Doctor::getRating, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        return doctors;
    }

    public List<Doctor> getConsultAt20Doctors() {
        return getAllDoctors(null, null, true, 20.0, null, "DR20_CLINIC", null, null, null, null);
    }

    private boolean matchesGender(Doctor doctor, String gender) {
        if (doctor.getUserId() == null) return false;
        return userRepository.findById(doctor.getUserId())
                .map(u -> gender.equalsIgnoreCase(u.getGender()))
                .orElse(false);
    }

    private double calculateDistanceKm(double lat, double lng, Doctor doctor) {
        if (doctor.getLatitude() == null || doctor.getLongitude() == null) return 0;
        double earthRadius = 6371;
        double dLat = Math.toRadians(doctor.getLatitude() - lat);
        double dLng = Math.toRadians(doctor.getLongitude() - lng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(doctor.getLatitude()))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return Math.round(earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 10.0) / 10.0;
    }

    public List<Doctor> getAllDoctors() {
        return getAllDoctors(null, null, null, null, null, null, null, null, null, null);
    }

    public Doctor getDoctorById(String id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    public Map<String, Object> getDoctorDetail(String id) {
        Doctor doctor = getDoctorById(id);
        var reviews = reviewRepository.findByDoctorIdOrderByCreatedAtDesc(id);
        Map<String, Object> detail = new HashMap<>();
        detail.put("doctor", doctor);
        detail.put("reviews", reviews);
        detail.put("totalReviews", reviews.isEmpty() ? doctor.getReviewCount() : reviews.size());
        if (reviews.isEmpty()) {
            detail.put("averageRating", doctor.getRating());
        } else {
            double avg = reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0);
            detail.put("averageRating", Math.round(avg * 10.0) / 10.0);
        }
        return detail;
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

    public List<Doctor> getTopDoctors() {
        return doctorRepository.findByIsAvailableTrue().stream()
                .sorted(Comparator.comparing(Doctor::getRating, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());
    }

    public Doctor createDoctor(Doctor doctor) {
        Doctor saved = doctorRepository.save(doctor);
        availabilityService.seedForDoctor(saved.getId(), 14);
        return saved;
    }
}
