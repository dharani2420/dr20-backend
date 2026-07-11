package com.dr20.patient.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Notification;
import com.dr20.shared.repository.NotificationRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<Notification> getByUser(String userId) {
        validateUser(userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Map<String, Object> getUnreadCount(String userId) {
        validateUser(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("unreadCount", notificationRepository.countByUserIdAndReadFalse(userId));
        return res;
    }

    public Notification markRead(String id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        n.setRead(true);
        return notificationRepository.save(n);
    }

    public Map<String, Object> markAllRead(String userId) {
        validateUser(userId);
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
        return Map.of("success", true, "message", "All notifications marked read");
    }

    public Notification create(Notification notification) {
        validateUser(notification.getUserId());
        return notificationRepository.save(notification);
    }

    private void validateUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
