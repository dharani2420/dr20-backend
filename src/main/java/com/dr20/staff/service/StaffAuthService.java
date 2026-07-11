package com.dr20.staff.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.model.User;
import com.dr20.common.enums.UserRole;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.common.security.JwtUtil;
import com.dr20.shared.service.AvailabilityService;
import com.dr20.shared.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StaffAuthService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final JwtUtil jwtUtil;
    private final AvailabilityService availabilityService;

    public Map<String, Object> register(Map<String, String> body) {
        String phone = body.get("phone");
        String professionStr = body.get("profession");
        if (phone == null || professionStr == null) {
            throw new BadRequestException("phone and profession are required");
        }

        UserRole profession = UserRole.valueOf(professionStr.toUpperCase());
        if (profession == UserRole.PATIENT || profession == UserRole.ADMIN) {
            throw new BadRequestException("Invalid staff profession");
        }

        if (userRepository.findByPhone(phone).isPresent()) {
            throw new BadRequestException("Phone already registered");
        }

        User user = new User();
        user.setPhone(phone);
        user.setFirstName(body.get("firstName"));
        user.setLastName(body.get("lastName"));
        user.setEmail(body.get("email"));
        user.setRole(profession);
        user.setProfession(profession);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUserId(user.getId());
        doctor.setName("Dr. " + (user.getFirstName() != null ? user.getFirstName() : "Staff"));
        doctor.setSpecialization(profession.name());
        doctor.setEmail(user.getEmail() != null ? user.getEmail() : phone + "@dr20.com");
        doctor.setPhone(phone);
        doctor.setConsultationFee(500.0);
        doctor.setIsAvailable(true);
        doctorRepository.save(doctor);

        user.setLinkedProfileId(doctor.getId());
        userRepository.save(user);

        availabilityService.seedForDoctor(doctor.getId(), 14);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Staff registered. Send OTP to login.");
        res.put("userId", user.getId());
        res.put("doctorId", doctor.getId());
        return res;
    }

    public Map<String, Object> sendOtp(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("Register first or contact admin"));
        if (!user.getRole().isStaff()) {
            throw new BadRequestException("Not a staff account");
        }
        otpService.sendOtp(phone, false);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OTP sent");
        return res;
    }

    public Map<String, Object> resendOtp(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("Register first or contact admin"));
        if (!user.getRole().isStaff()) {
            throw new BadRequestException("Not a staff account");
        }
        otpService.sendOtp(phone, true);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OTP resent");
        return res;
    }

    public Map<String, Object> verifyOtp(String phone, String otp) {
        User user = otpService.verifyOtp(phone, otp);
        if (!user.getRole().isStaff()) {
            throw new BadRequestException("Not a staff account");
        }

        String token = jwtUtil.generateToken(
                user.getId(), phone, user.getRole(), user.getLinkedProfileId());

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Welcome, " + user.getProfession());
        res.put("token", token);
        res.put("userId", user.getId());
        res.put("role", user.getRole().name());
        res.put("linkedProfileId", user.getLinkedProfileId());
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
        user.setAddress(updated.getAddress());
        user.setLanguages(updated.getLanguages());
        user.setRegistrationNumber(updated.getRegistrationNumber());
        user.setProfileImage(updated.getProfileImage());
        return userRepository.save(user);
    }
}
