package com.dr20.patient.controller;

import com.dr20.shared.model.User;
import com.dr20.common.security.AuthHelper;
import com.dr20.patient.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.sendOtp(body.get("phone")));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, Object>> resendOtp(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.resendOtp(body.get("phone")));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.verifyOtp(body.get("phone"), body.get("otp")));
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<Map<String, Object>> completeProfile(@RequestBody User profile) {
        return ResponseEntity.ok(authService.completeProfile(AuthHelper.currentUserId(), profile));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(authService.getProfile(userId));
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<User> updateProfile(@PathVariable String userId, @RequestBody User user) {
        return ResponseEntity.ok(authService.updateProfile(userId, user));
    }
}
