package com.dr20.shared.service.payment;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Payment;
import com.dr20.common.enums.AppointmentStatus;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "payment.provider", havingValue = "mock", matchIfMissing = true)
@RequiredArgsConstructor
public class DevMockPaymentGateway implements PaymentGateway {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public Map<String, Object> createOrder(String appointmentId, String userId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        Payment payment = new Payment();
        payment.setAppointmentId(appointmentId);
        payment.setUserId(userId);
        payment.setAmount(appt.getTotalFee());
        payment.setStatus("PENDING");
        payment.setPaymentMethod("MOCK");
        payment.setTransactionId("ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        paymentRepository.save(payment);

        Map<String, Object> res = new HashMap<>();
        res.put("orderId", payment.getTransactionId());
        res.put("paymentId", payment.getId());
        res.put("amount", payment.getAmount());
        res.put("provider", "mock");
        return res;
    }

    @Override
    public Map<String, Object> verifyPayment(String orderId, String paymentId, String signature) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        payment.setStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Appointment appt = appointmentRepository.findById(payment.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appt.setPaymentStatus("PAID");
        appt.setPaymentId(payment.getId());
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Payment successful!");
        res.put("transactionId", payment.getTransactionId());
        res.put("appointmentId", appt.getId());
        return res;
    }

    @Override
    public Map<String, Object> handleWebhook(Map<String, String> payload) {
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Mock webhook — use verify endpoint in dev");
        return res;
    }
}
