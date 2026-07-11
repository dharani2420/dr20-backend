package com.dr20.patient.controller;

import com.dr20.patient.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dr20.shared.model.Payment;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/summary/{appointmentId}")
    public ResponseEntity<Map<String, Object>> summary(@PathVariable String appointmentId) {
        return ResponseEntity.ok(paymentService.getSummary(appointmentId));
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(paymentService.createOrder(
                body.get("appointmentId"), body.get("userId")));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(paymentService.verify(
                body.get("orderId"), body.get("paymentId"), body.get("signature")));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> webhook(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(paymentService.webhook(body));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> history(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getHistory(userId));
    }
}
