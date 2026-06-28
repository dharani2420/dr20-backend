package com.dr20.shared.service.payment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "payment.provider", havingValue = "razorpay")
public class RazorpayPaymentGateway implements PaymentGateway {

    @Override
    public Map<String, Object> createOrder(String appointmentId, String userId) {
        // TODO: Razorpay Orders API integration
        throw new UnsupportedOperationException("Configure razorpay.key-id and razorpay.key-secret");
    }

    @Override
    public Map<String, Object> verifyPayment(String orderId, String paymentId, String signature) {
        // TODO: Razorpay signature verification
        throw new UnsupportedOperationException("Razorpay verify not configured");
    }

    @Override
    public Map<String, Object> handleWebhook(Map<String, String> payload) {
        // TODO: Razorpay webhook handler
        Map<String, Object> res = new HashMap<>();
        res.put("status", "webhook placeholder");
        return res;
    }
}
