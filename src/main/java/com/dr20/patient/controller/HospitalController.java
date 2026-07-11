package com.dr20.patient.controller;

import com.dr20.shared.model.Doctor;
import com.dr20.shared.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable String slug) {
        return ResponseEntity.ok(hospitalService.getHospitalBySlug(slug));
    }

    @GetMapping("/{slug}/doctors")
    public ResponseEntity<List<Doctor>> doctors(@PathVariable String slug) {
        return ResponseEntity.ok(hospitalService.getDoctorsBySlug(slug));
    }
}
