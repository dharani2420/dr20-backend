package com.dr20.staff.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.StaffVerification;
import com.dr20.shared.repository.StaffVerificationRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StaffVerificationService {

    private final StaffVerificationRepository verificationRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getStatus(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StaffVerification v = verificationRepository.findByUserId(userId)
                .orElseGet(() -> createDefault(userId));

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("overallStatus", v.getOverallStatus());
        res.put("steps", Map.of(
                "personalInformation", step("Personal Information", "your personal details are verified", v.getPersonalInfoStatus()),
                "professionalInformation", step("Professional Information", "your professional details are verified", v.getProfessionalInfoStatus()),
                "documentsUploaded", step("Documents Uploaded", "All documents have been received", v.getDocumentsStatus()),
                "adminReview", step("Admin Review", "Our team is reviewing your profile", v.getAdminReviewStatus())
        ));
        return res;
    }

    public StaffVerification submitProfile(String userId) {
        StaffVerification v = verificationRepository.findByUserId(userId)
                .orElseGet(() -> createDefault(userId));
        v.setOverallStatus("SUBMITTED");
        v.setSubmittedAt(LocalDateTime.now());
        verificationRepository.save(v);
        userRepository.findById(userId).ifPresent(u -> {
            u.setVerificationStatus("SUBMITTED");
            userRepository.save(u);
        });
        return v;
    }

    private StaffVerification createDefault(String userId) {
        StaffVerification v = new StaffVerification();
        v.setUserId(userId);
        v.setOverallStatus("IN_PROGRESS");
        v.setAdminReviewStatus("IN_PROGRESS");
        return verificationRepository.save(v);
    }

    private Map<String, String> step(String title, String subtitle, String status) {
        return Map.of("title", title, "subtitle", subtitle, "status", status);
    }
}
