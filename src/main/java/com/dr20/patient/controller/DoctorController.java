package com.dr20.patient.controller;

import com.dr20.shared.model.Doctor;
import com.dr20.shared.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAll() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> search(@RequestParam String q) {
        return ResponseEntity.ok(doctorService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable String id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<String>> slots(@PathVariable String id, @RequestParam String date) {
        return ResponseEntity.ok(doctorService.getSlots(id, date));
    }

    @GetMapping("/specialization/{name}")
    public ResponseEntity<List<Doctor>> bySpecialization(@PathVariable String name) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(name));
    }
}
