package com.dr20.shared.service.payment;

import java.util.Map;

public interface PaymentGateway {

    Map<String, Object> createOrder(String appointmentId, String userId);

    Map<String, Object> verifyPayment(String orderId, String paymentId, String signature);

    Map<String, Object> handleWebhook(Map<String, String> payload);
}
