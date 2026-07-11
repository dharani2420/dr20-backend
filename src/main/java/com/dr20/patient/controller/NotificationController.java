package com.dr20.patient.controller;

import com.dr20.shared.model.Notification;
import com.dr20.patient.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> list(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getByUser(userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Object>> unreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.markRead(id));
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Object>> markAllRead(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.markAllRead(userId));
    }
}
