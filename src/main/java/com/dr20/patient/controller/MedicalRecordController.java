package com.dr20.patient.controller;

import com.dr20.shared.model.MedicalRecord;
import com.dr20.patient.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MedicalRecord>> list(@PathVariable String userId) {
        return ResponseEntity.ok(medicalRecordService.getByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> get(@PathVariable String id) {
        return ResponseEntity.ok(medicalRecordService.getById(id));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<MedicalRecord> add(@PathVariable String userId, @RequestBody MedicalRecord record) {
        return ResponseEntity.ok(medicalRecordService.add(userId, record));
    }
}
