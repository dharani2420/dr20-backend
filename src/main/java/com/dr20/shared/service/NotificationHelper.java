package com.dr20.shared.service;

import com.dr20.shared.model.Notification;
import com.dr20.shared.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationHelper {

    private final NotificationRepository notificationRepository;

    public void notify(String userId, String title, String message, String type, String relatedId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setRelatedId(relatedId);
        notificationRepository.save(n);
    }
}
