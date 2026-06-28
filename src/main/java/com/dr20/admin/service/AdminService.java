package com.dr20.admin.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.model.User;
import com.dr20.common.enums.UserRole;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AvailabilityService availabilityService;

    public Map<String, Object> createStaff(Map<String, String> body) {
        String phone = body.get("phone");
        String professionStr = body.getOrDefault("profession", "DOCTOR");
        UserRole profession = UserRole.valueOf(professionStr.toUpperCase());

        if (profession == UserRole.PATIENT) {
            throw new BadRequestException("Cannot create PATIENT via admin staff endpoint");
        }

        User user = userRepository.findByPhone(phone).orElse(new User());
        user.setPhone(phone);
        user.setFirstName(body.get("firstName"));
        user.setLastName(body.get("lastName"));
        user.setEmail(body.get("email"));
        user.setRole(profession);
        user.setProfession(profession);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUserId(user.getId());
        doctor.setName(body.getOrDefault("name", "Dr. " + user.getFirstName()));
        doctor.setDegree(body.getOrDefault("degree", "MBBS"));
        doctor.setSpecialization(body.getOrDefault("specialization", profession.name()));
        doctor.setEmail(user.getEmail() != null ? user.getEmail() : phone + "@dr20.com");
        doctor.setPhone(phone);
        doctor.setConsultationFee(Double.parseDouble(body.getOrDefault("consultationFee", "600")));
        doctor.setClinicName(body.get("clinicName"));
        doctor.setClinicAddress(body.get("clinicAddress"));
        doctor.setIsAvailable(true);
        doctorRepository.save(doctor);

        user.setLinkedProfileId(doctor.getId());
        userRepository.save(user);
        availabilityService.seedForDoctor(doctor.getId(), 14);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("userId", user.getId());
        res.put("doctorId", doctor.getId());
        res.put("phone", phone);
        return res;
    }
}
