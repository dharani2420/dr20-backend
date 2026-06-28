package com.dr20.admin.controller;

import com.dr20.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/staff")
    public ResponseEntity<Map<String, Object>> createStaff(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.createStaff(body));
    }
}
