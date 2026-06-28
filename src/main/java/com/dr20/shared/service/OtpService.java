package com.dr20.shared.service;

import com.dr20.common.config.AppConstants;
import com.dr20.common.exception.BadRequestException;
import com.dr20.shared.model.OtpLog;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.OtpLogRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.sms.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final OtpLogRepository otpLogRepository;
    private final SmsService smsService;
    private final SecureRandom random = new SecureRandom();

    public String sendOtp(String phone, boolean isResend) {
        validatePhone(phone);
        OtpLog log = otpLogRepository.findByPhone(phone).orElse(new OtpLog());
        LocalDateTime now = LocalDateTime.now();

        if (log.getPhone() == null) {
            log.setPhone(phone);
            log.setWindowStart(now);
            log.setCount(0);
        }

        if (log.getWindowStart() != null &&
                ChronoUnit.MINUTES.between(log.getWindowStart(), now) >= AppConstants.OTP_WINDOW_MINUTES) {
            log.setWindowStart(now);
            log.setCount(0);
        }

        if (log.getCount() >= AppConstants.OTP_MAX_PER_WINDOW) {
            throw new BadRequestException("Too many OTP requests. Try again later.");
        }

        if (isResend && log.getLastSentAt() != null &&
                ChronoUnit.SECONDS.between(log.getLastSentAt(), now) < AppConstants.OTP_RESEND_COOLDOWN_SECONDS) {
            throw new BadRequestException("Please wait before requesting a new OTP.");
        }

        String otp = String.format("%06d", random.nextInt(1_000_000));
        User user = userRepository.findByPhone(phone).orElse(new User());
        user.setPhone(phone);
        user.setOtp(otp);
        user.setOtpExpiry(now.plusMinutes(AppConstants.OTP_EXPIRY_MINUTES));
        userRepository.save(user);

        log.setCount(log.getCount() + 1);
        log.setLastSentAt(now);
        otpLogRepository.save(log);

        smsService.sendOtp(phone, otp);
        return otp;
    }

    public User verifyOtp(String phone, String otp) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("User not found. Send OTP first."));

        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new BadRequestException("OTP expired. Request a new one.");
        }
        if (!user.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        user.setPhoneVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        return userRepository.save(user);
    }

    private void validatePhone(String phone) {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new BadRequestException("Enter valid 10-digit phone number");
        }
    }
}
