package com.dr20.patient.controller;

import com.dr20.shared.model.FamilyMember;
import com.dr20.patient.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService FamilyService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FamilyMember>> list(@PathVariable String userId) {
        return ResponseEntity.ok(FamilyService.getByUser(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<FamilyMember> add(@PathVariable String userId, @RequestBody FamilyMember member) {
        return ResponseEntity.ok(FamilyService.add(userId, member));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> remove(@PathVariable String memberId) {
        FamilyService.remove(memberId);
        return ResponseEntity.ok("Family member removed");
    }
}
