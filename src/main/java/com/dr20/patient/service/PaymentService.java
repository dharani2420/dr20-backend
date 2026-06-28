package com.dr20.patient.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.service.payment.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final AppointmentRepository appointmentRepository;

    public Map<String, Object> getSummary(String appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        Map<String, Object> summary = new HashMap<>();
        summary.put("consultationFee", appt.getConsultationFee());
        summary.put("platformFee", appt.getPlatformFee());
        summary.put("totalPayable", appt.getTotalFee());
        return summary;
    }

    public Map<String, Object> createOrder(String appointmentId, String userId) {
        return paymentGateway.createOrder(appointmentId, userId);
    }

    public Map<String, Object> verify(String orderId, String paymentId, String signature) {
        return paymentGateway.verifyPayment(orderId, paymentId, signature);
    }

    public Map<String, Object> webhook(Map<String, String> payload) {
        return paymentGateway.handleWebhook(payload);
    }
}
