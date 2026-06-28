package com.dr20.patient.controller;

import com.dr20.shared.model.Specialization;
import com.dr20.shared.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping
    public ResponseEntity<List<Specialization>> getAll() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @PostMapping
    public ResponseEntity<Specialization> add(@RequestBody Specialization spec) {
        return ResponseEntity.ok(specializationService.addSpecialization(spec));
    }
}
