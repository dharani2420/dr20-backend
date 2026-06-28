package com.dr20.patient.controller;

import com.dr20.shared.model.Appointment;
import com.dr20.common.security.AuthHelper;
import com.dr20.patient.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> book(@RequestBody Appointment appointment) {
        appointment.setUserId(AuthHelper.currentUserId());
        return ResponseEntity.ok(appointmentService.book(appointment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @GetMapping("/{id}/pass")
    public ResponseEntity<Map<String, Object>> getPass(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getPass(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Appointment>> getUserAppointments(@PathVariable String userId) {
        return ResponseEntity.ok(appointmentService.getUserAppointments(userId));
    }

    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<Appointment>> upcoming(@PathVariable String userId) {
        return ResponseEntity.ok(appointmentService.getUpcoming(userId));
    }

    @GetMapping("/user/{userId}/past")
    public ResponseEntity<List<Appointment>> past(@PathVariable String userId) {
        return ResponseEntity.ok(appointmentService.getPast(userId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancel(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.cancel(id, AuthHelper.currentUserId()));
    }
}
