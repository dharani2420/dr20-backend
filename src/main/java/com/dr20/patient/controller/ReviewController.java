package com.dr20.patient.controller;

import com.dr20.common.security.AuthHelper;
import com.dr20.shared.model.Review;
import com.dr20.patient.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> submit(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(reviewService.submit(
                AuthHelper.currentUserId(),
                (String) body.get("appointmentId"),
                body.get("rating") instanceof Integer ? (Integer) body.get("rating")
                        : Integer.parseInt(body.get("rating").toString()),
                (String) body.get("comment")));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Review>> byDoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(reviewService.getByDoctor(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/summary")
    public ResponseEntity<Map<String, Object>> summary(@PathVariable String doctorId) {
        return ResponseEntity.ok(reviewService.getDoctorReviewSummary(doctorId));
    }
}
