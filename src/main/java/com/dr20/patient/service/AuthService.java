package com.dr20.patient.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.User;
import com.dr20.common.enums.UserRole;
import com.dr20.shared.repository.UserRepository;
import com.dr20.common.security.JwtUtil;
import com.dr20.shared.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public Map<String, Object> sendOtp(String phone) {
        otpService.sendOtp(phone, false);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OTP sent successfully");
        return res;
    }

    public Map<String, Object> resendOtp(String phone) {
        otpService.sendOtp(phone, true);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OTP resent successfully");
        return res;
    }

    public Map<String, Object> verifyOtp(String phone, String otp) {
        User user = otpService.verifyOtp(phone, otp);
        if (user.getRole() != null && user.getRole().isStaff()) {
            throw new IllegalStateException("Use staff login for this account");
        }
        user.setRole(UserRole.PATIENT);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), phone, UserRole.PATIENT, null);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OTP verified");
        res.put("token", token);
        res.put("userId", user.getId());
        res.put("isProfileComplete", user.getFirstName() != null);
        return res;
    }

    public Map<String, Object> completeProfile(String userId, User profile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstName(profile.getFirstName());
        user.setLastName(profile.getLastName());
        user.setEmail(profile.getEmail());
        user.setDateOfBirth(profile.getDateOfBirth());
        user.setGender(profile.getGender());
        user.setBloodGroup(profile.getBloodGroup());
        userRepository.save(user);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Profile updated");
        res.put("user", user);
        return res;
    }

    public User getProfile(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateProfile(String userId, User updated) {
        User user = getProfile(userId);
        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setEmail(updated.getEmail());
        user.setDateOfBirth(updated.getDateOfBirth());
        user.setGender(updated.getGender());
        user.setBloodGroup(updated.getBloodGroup());
        return userRepository.save(user);
    }
}
