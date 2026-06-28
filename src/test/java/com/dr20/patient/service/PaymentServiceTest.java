package com.dr20.patient.service;

import com.dr20.shared.model.Appointment;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.service.payment.PaymentGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentGateway paymentGateway;
    @Mock private AppointmentRepository appointmentRepository;
    @InjectMocks private PaymentService paymentService;

    @Test
    void getSummary_returnsFees() {
        Appointment appt = new Appointment();
        appt.setTotalFee(620.0);
        when(appointmentRepository.findById("a1")).thenReturn(Optional.of(appt));

        assertEquals(620.0, paymentService.getSummary("a1").get("totalPayable"));
    }

    @Test
    void createOrder_delegatesToGateway() {
        when(paymentGateway.createOrder("a1", "u1")).thenReturn(Map.of("orderId", "ORDER-123"));
        assertEquals("ORDER-123", paymentService.createOrder("a1", "u1").get("orderId"));
    }
}
