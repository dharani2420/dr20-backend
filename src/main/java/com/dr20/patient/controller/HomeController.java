package com.dr20.patient.controller;

import com.dr20.patient.service.HomeService;
import com.dr20.patient.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final SearchService searchService;

    @GetMapping("/api/home/{userId}")
    public ResponseEntity<Map<String, Object>> home(@PathVariable String userId) {
        return ResponseEntity.ok(homeService.getHomeData(userId));
    }

    @GetMapping("/api/services")
    public ResponseEntity<Map<String, Object>> services() {
        return ResponseEntity.ok(homeService.getServicesScreen());
    }

    @GetMapping("/api/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        return ResponseEntity.ok(searchService.search(q));
    }
}
