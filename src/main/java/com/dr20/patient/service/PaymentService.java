package com.dr20.patient.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.PaymentRepository;
import com.dr20.shared.service.NotificationHelper;
import com.dr20.shared.service.payment.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.dr20.shared.repository.PaymentRepository;
import com.dr20.shared.model.Payment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationHelper notificationHelper;

    public Map<String, Object> getSummary(String appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        Map<String, Object> summary = new HashMap<>();
        summary.put("itemTotal", appt.getConsultationFee());
        summary.put("consultationFee", appt.getConsultationFee());
        summary.put("platformFee", appt.getPlatformFee());
        summary.put("platformFeeOriginal", 5.0);
        summary.put("platformFeeWaived", appt.getPlatformFee() != null && appt.getPlatformFee() == 0);
        summary.put("totalPayable", appt.getTotalFee());
        return summary;
    }

    public Map<String, Object> createOrder(String appointmentId, String userId) {
        return paymentGateway.createOrder(appointmentId, userId);
    }

    public Map<String, Object> verify(String orderId, String paymentId, String signature) {
        Map<String, Object> result = paymentGateway.verifyPayment(orderId, paymentId, signature);
        if (result.get("appointmentId") != null) {
            appointmentRepository.findById(result.get("appointmentId").toString()).ifPresent(appt ->
                    notificationHelper.notify(appt.getUserId(), "Payment Successful",
                            "Your appointment with " + appt.getDoctorName() + " is confirmed. Token: "
                                    + appt.getTokenNumber(), "PAYMENT", appt.getId()));
        }
        return result;
    }

    public Map<String, Object> webhook(Map<String, String> payload) {
        return paymentGateway.handleWebhook(payload);
    }

    public List<Payment> getHistory(String userId) {
        return paymentRepository.findByUserId(userId);
    }
}
