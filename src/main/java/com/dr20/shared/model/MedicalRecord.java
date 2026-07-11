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
@Document(collection = "medical_records")
public class MedicalRecord {

    @Id
    private String id;
    private String userId;
    private String appointmentId;
    private String doctorName;
    private String title;
    private String type; // PRESCRIPTION, LAB_REPORT, VISIT_SUMMARY, OTHER
    private String fileUrl;
    private String notes;
    private LocalDateTime recordDate = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();
}
