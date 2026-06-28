package com.dr20.staff.service;

import com.dr20.common.enums.UserRole;
import com.dr20.common.exception.BadRequestException;
import com.dr20.common.security.JwtUtil;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.AvailabilityService;
import com.dr20.shared.service.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffAuthServiceTest {

    @Mock private OtpService otpService;
    @Mock private UserRepository userRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private AvailabilityService availabilityService;
    @InjectMocks private StaffAuthService staffAuthService;

    @Test
    void verifyOtp_returnsStaffJwt() {
        User user = new User();
        user.setId("s1");
        user.setPhone("9123456781");
        user.setRole(UserRole.DOCTOR);
        user.setProfession(UserRole.DOCTOR);
        user.setLinkedProfileId("d1");
        when(otpService.verifyOtp("9123456781", "123456")).thenReturn(user);
        when(jwtUtil.generateToken("s1", "9123456781", UserRole.DOCTOR, "d1")).thenReturn("staff-token");

        Map<String, Object> result = staffAuthService.verifyOtp("9123456781", "123456");

        assertEquals("staff-token", result.get("token"));
    }

    @Test
    void sendOtp_rejectsPatientAccount() {
        User user = new User();
        user.setRole(UserRole.PATIENT);
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> staffAuthService.sendOtp("9876543210"));
    }
}
