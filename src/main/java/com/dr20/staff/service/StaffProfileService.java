package com.dr20.staff.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StaffProfileService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public Map<String, Object> getProfileSummary(String userId) {
        User user = getUser(userId);
        Doctor doctor = getDoctor(user);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("staffName", StaffResponseHelper.staffDisplayName(doctor, user.getFirstName(), user.getLastName()));
        res.put("specialization", doctor.getSpecialization());
        res.put("profileImage", firstNonNull(user.getProfileImage(), doctor.getProfileImage()));
        res.put("verificationStatus", user.getVerificationStatus());
        res.put("isVerified", "APPROVED".equalsIgnoreCase(user.getVerificationStatus()));
        res.put("verificationBadge", "APPROVED".equalsIgnoreCase(user.getVerificationStatus()) ? "Verified" : "Pending");
        res.put("phone", user.getPhone());
        return res;
    }

    public Map<String, Object> getPersonalProfile(String userId) {
        User user = getUser(userId);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("firstName", user.getFirstName());
        res.put("lastName", user.getLastName());
        res.put("fullName", buildFullName(user));
        res.put("email", user.getEmail());
        res.put("gender", user.getGender());
        res.put("dateOfBirth", user.getDateOfBirth());
        res.put("address", user.getAddress());
        res.put("profileImage", user.getProfileImage());
        res.put("phone", user.getPhone());
        return res;
    }

    public User updatePersonalProfile(String userId, User updated) {
        User user = getUser(userId);
        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setEmail(updated.getEmail());
        user.setDateOfBirth(updated.getDateOfBirth());
        user.setGender(updated.getGender());
        user.setAddress(updated.getAddress());
        if (updated.getProfileImage() != null) {
            user.setProfileImage(updated.getProfileImage());
        }
        userRepository.save(user);

        doctorRepository.findById(user.getLinkedProfileId()).ifPresent(doctor -> {
            doctor.setName("Dr. " + buildFullName(user));
            if (updated.getProfileImage() != null) {
                doctor.setProfileImage(updated.getProfileImage());
            }
            doctorRepository.save(doctor);
        });
        return user;
    }

    public User updateProfilePhoto(String userId, String fileUrl) {
        User user = getUser(userId);
        user.setProfileImage(fileUrl);
        userRepository.save(user);
        doctorRepository.findById(user.getLinkedProfileId()).ifPresent(doctor -> {
            doctor.setProfileImage(fileUrl);
            doctorRepository.save(doctor);
        });
        return user;
    }

    public Map<String, Object> getProfessionalProfile(String userId) {
        User user = getUser(userId);
        Doctor doctor = getDoctor(user);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("serviceCategory", formatServiceCategory(user.getProfession()));
        res.put("serviceCategoryReadOnly", true);
        res.put("specialization", doctor.getSpecialization());
        res.put("specializationReadOnly", true);
        res.put("experience", doctor.getExperience());
        res.put("languages", doctor.getLanguages() != null ? doctor.getLanguages()
                : (user.getLanguages() != null ? user.getLanguages() : List.of()));
        res.put("clinicName", doctor.getClinicName());
        res.put("clinicAddress", doctor.getClinicAddress());
        res.put("registrationNumber", firstNonNull(doctor.getRegistrationNumber(), user.getRegistrationNumber()));
        res.put("consultationMode", doctor.getConsultationMode());
        res.put("travelRadiusKm", doctor.getTravelRadiusKm());
        return res;
    }

    public Map<String, Object> updateProfessionalProfile(String userId, Map<String, Object> body) {
        User user = getUser(userId);
        Doctor doctor = getDoctor(user);

        if (body.get("experience") != null) {
            doctor.setExperience(Integer.valueOf(body.get("experience").toString()));
        }
        if (body.get("languages") instanceof List<?> langs) {
            @SuppressWarnings("unchecked")
            List<String> languages = (List<String>) langs;
            doctor.setLanguages(languages);
            user.setLanguages(languages);
        }
        if (body.get("clinicName") != null) {
            doctor.setClinicName(body.get("clinicName").toString());
        }
        if (body.get("clinicAddress") != null) {
            doctor.setClinicAddress(body.get("clinicAddress").toString());
        }
        if (body.get("registrationNumber") != null) {
            String reg = body.get("registrationNumber").toString();
            doctor.setRegistrationNumber(reg);
            user.setRegistrationNumber(reg);
        }
        if (body.get("consultationMode") != null) {
            doctor.setConsultationMode(body.get("consultationMode").toString());
        }
        if (body.get("travelRadiusKm") != null) {
            doctor.setTravelRadiusKm(Integer.valueOf(body.get("travelRadiusKm").toString()));
        }

        userRepository.save(user);
        doctorRepository.save(doctor);
        return getProfessionalProfile(userId);
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Doctor getDoctor(User user) {
        if (user.getLinkedProfileId() == null) {
            throw new ResourceNotFoundException("Staff profile not linked");
        }
        return doctorRepository.findById(user.getLinkedProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
    }

    private String buildFullName(User user) {
        return ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                + (user.getLastName() != null ? user.getLastName() : "")).trim();
    }

    private String formatServiceCategory(com.dr20.common.enums.UserRole profession) {
        if (profession == null) return "Doctor";
        return switch (profession) {
            case DOCTOR -> "Doctor";
            case NURSE -> "Nurse";
            case PHYSIOTHERAPIST -> "Physiotherapist";
            case LAB_TECH -> "Lab Technician";
            case ELDER_CARE -> "Elder Care";
            default -> profession.name();
        };
    }

    private String firstNonNull(String primary, String fallback) {
        return primary != null ? primary : fallback;
    }
}
