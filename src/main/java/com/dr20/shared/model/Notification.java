package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;
    private String userId;
    private String title;
    private String message;
    private String type; // APPOINTMENT, PAYMENT, REMINDER, GENERAL
    private String relatedId;
    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
