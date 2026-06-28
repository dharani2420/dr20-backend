package com.dr20.shared.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "sms.provider", havingValue = "console", matchIfMissing = true)
public class DevConsoleSmsService implements SmsService {

    @Override
    public void sendOtp(String phone, String otp) {
        log.info("========== DEV OTP for {} : {} ==========", phone, otp);
        System.out.println("DEV OTP for " + phone + " : " + otp);
    }
}
