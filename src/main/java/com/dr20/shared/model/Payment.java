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
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String appointmentId;
    private String userId;
    private Double amount;

    private String status; // PENDING, SUCCESS, FAILED

    private String paymentMethod; // UPI, CARD, NETBANKING
    private String transactionId;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime paidAt;
}
