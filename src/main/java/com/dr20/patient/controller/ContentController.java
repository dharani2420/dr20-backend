package com.dr20.patient.controller;

import com.dr20.patient.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentController {

    private final HomeService homeService;

    @GetMapping("/banners")
    public ResponseEntity<?> banners() {
        return ResponseEntity.ok(homeService.getBanners());
    }

    @GetMapping("/categories")
    public ResponseEntity<?> categories(@RequestParam(required = false) String type) {
        if (type == null || type.isBlank()) {
            return ResponseEntity.ok(List.of(
                    Map.of("type", "CONSULTATION", "items", homeService.getCategoriesByType("CONSULTATION")),
                    Map.of("type", "CARE", "items", homeService.getCategoriesByType("CARE")),
                    Map.of("type", "SYMPTOM", "items", homeService.getCategoriesByType("SYMPTOM"))
            ));
        }
        return ResponseEntity.ok(homeService.getCategoriesByType(type));
    }
}
