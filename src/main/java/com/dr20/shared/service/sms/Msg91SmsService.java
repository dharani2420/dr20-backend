package com.dr20.shared.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "sms.provider", havingValue = "msg91")
public class Msg91SmsService implements SmsService {

    @Value("${sms.api-key:}")
    private String apiKey;

    @Value("${sms.sender:DR20APP}")
    private String sender;

    @Override
    public void sendOtp(String phone, String otp) {
        // TODO: integrate MSG91 HTTP API when keys are configured
        log.info("MSG91 OTP to {} from {} (configure sms.api-key in prod)", phone, sender);
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("MSG91 API key not configured");
        }
        // HttpClient call to MSG91 would go here
    }
}
