package com.dr20.shared.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.shared.model.OtpLog;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.OtpLogRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.sms.SmsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private OtpLogRepository otpLogRepository;
    @Mock private SmsService smsService;
    @InjectMocks private OtpService otpService;

    @Test
    void sendOtp_generatesAndSaves() {
        when(otpLogRepository.findByPhone("9876543210")).thenReturn(Optional.empty());
        when(otpLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        otpService.sendOtp("9876543210", false);

        verify(smsService).sendOtp(eq("9876543210"), anyString());
    }

    @Test
    void verifyOtp_success() {
        User user = new User();
        user.setOtp("123456");
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = otpService.verifyOtp("9876543210", "123456");
        assertTrue(result.isPhoneVerified());
    }

    @Test
    void sendOtp_rateLimit() {
        OtpLog log = new OtpLog();
        log.setPhone("9876543210");
        log.setCount(3);
        log.setWindowStart(LocalDateTime.now());
        when(otpLogRepository.findByPhone("9876543210")).thenReturn(Optional.of(log));

        assertThrows(BadRequestException.class, () -> otpService.sendOtp("9876543210", false));
    }
}
