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
@Document(collection = "staff_verifications")
public class StaffVerification {

    @Id
    private String id;
    private String userId;
    private String overallStatus; // SUBMITTED, IN_PROGRESS, APPROVED
    private String personalInfoStatus = "COMPLETED";
    private String professionalInfoStatus = "COMPLETED";
    private String documentsStatus = "COMPLETED";
    private String adminReviewStatus = "IN_PROGRESS";
    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime approvedAt;
}
