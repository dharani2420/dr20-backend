package com.dr20.staff.controller;

import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Availability;
import com.dr20.shared.model.User;
import com.dr20.common.security.AuthHelper;
import com.dr20.shared.service.AvailabilityService;
import com.dr20.staff.service.StaffAppointmentService;
import com.dr20.staff.service.StaffAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffAppointmentService staffAppointmentService;
    private final StaffAuthService staffAuthService;
    private final AvailabilityService availabilityService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(staffAppointmentService.dashboard(AuthHelper.currentUserId()));
    }

    @GetMapping("/appointments/upcoming")
    public ResponseEntity<List<Appointment>> upcoming() {
        return ResponseEntity.ok(staffAppointmentService.upcoming(AuthHelper.currentUserId()));
    }

    @GetMapping("/appointments/past")
    public ResponseEntity<List<Appointment>> past() {
        return ResponseEntity.ok(staffAppointmentService.past(AuthHelper.currentUserId()));
    }

    @GetMapping("/appointments/cancelled")
    public ResponseEntity<List<Appointment>> cancelled() {
        return ResponseEntity.ok(staffAppointmentService.cancelled(AuthHelper.currentUserId()));
    }

    @GetMapping("/appointments/search")
    public ResponseEntity<List<Appointment>> search(@RequestParam String q) {
        return ResponseEntity.ok(staffAppointmentService.search(AuthHelper.currentUserId(), q));
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable String id) {
        return ResponseEntity.ok(staffAppointmentService.getById(AuthHelper.currentUserId(), id));
    }

    @PostMapping("/appointments/verify-qr")
    public ResponseEntity<Appointment> verifyQr(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(staffAppointmentService.verifyQr(
                AuthHelper.currentUserId(), body.get("qrData")));
    }

    @PostMapping("/appointments/verify-token")
    public ResponseEntity<Appointment> verifyToken(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(staffAppointmentService.verifyToken(
                AuthHelper.currentUserId(), body.get("tokenNumber")));
    }

    @PostMapping("/appointments/{id}/start")
    public ResponseEntity<Appointment> start(@PathVariable String id) {
        return ResponseEntity.ok(staffAppointmentService.start(AuthHelper.currentUserId(), id));
    }

    @PostMapping("/appointments/{id}/complete")
    public ResponseEntity<Appointment> complete(@PathVariable String id) {
        return ResponseEntity.ok(staffAppointmentService.complete(AuthHelper.currentUserId(), id));
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<Availability>> schedule(
            @RequestParam String from, @RequestParam String to) {
        var user = staffAuthService.getProfile(AuthHelper.currentUserId());
        return ResponseEntity.ok(availabilityService.getSchedule(user.getLinkedProfileId(), from, to));
    }

    @PostMapping("/schedule")
    public ResponseEntity<Availability> addSlots(@RequestBody Map<String, Object> body) {
        var user = staffAuthService.getProfile(AuthHelper.currentUserId());
        @SuppressWarnings("unchecked")
        List<String> times = (List<String>) body.get("times");
        return ResponseEntity.ok(availabilityService.addSlots(
                user.getLinkedProfileId(), (String) body.get("date"), times));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> profile() {
        return ResponseEntity.ok(staffAuthService.getProfile(AuthHelper.currentUserId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user) {
        return ResponseEntity.ok(staffAuthService.updateProfile(AuthHelper.currentUserId(), user));
    }
}
