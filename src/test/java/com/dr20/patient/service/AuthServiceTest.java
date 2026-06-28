package com.dr20.patient.service;

import com.dr20.common.enums.UserRole;
import com.dr20.common.security.JwtUtil;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private OtpService otpService;
    @Mock private UserRepository userRepository;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    @Test
    void verifyOtp_returnsJwtForPatient() {
        User user = new User();
        user.setId("u1");
        user.setPhone("9876543210");
        user.setRole(UserRole.PATIENT);
        when(otpService.verifyOtp("9876543210", "123456")).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(jwtUtil.generateToken("u1", "9876543210", UserRole.PATIENT, null)).thenReturn("token-abc");

        Map<String, Object> result = authService.verifyOtp("9876543210", "123456");

        assertTrue((Boolean) result.get("success"));
        assertEquals("token-abc", result.get("token"));
    }

    @Test
    void sendOtp_delegatesToOtpService() {
        when(otpService.sendOtp("9876543210", false)).thenReturn("123456");
        Map<String, Object> result = authService.sendOtp("9876543210");
        assertTrue((Boolean) result.get("success"));
    }
}
