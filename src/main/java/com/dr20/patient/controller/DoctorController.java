package com.dr20.patient.controller;

import com.dr20.shared.model.Doctor;
import com.dr20.shared.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAll(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean availableToday,
            @RequestParam(required = false) Double maxFee,
            @RequestParam(required = false) Double minFee,
            @RequestParam(required = false) String clinicType,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        return ResponseEntity.ok(doctorService.getAllDoctors(
                gender, minRating, availableToday, maxFee, minFee, clinicType,
                specialization, sort, latitude, longitude));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Doctor>> top() {
        return ResponseEntity.ok(doctorService.getTopDoctors());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> search(@RequestParam String q) {
        return ResponseEntity.ok(doctorService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable String id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable String id) {
        return ResponseEntity.ok(doctorService.getDoctorDetail(id));
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
