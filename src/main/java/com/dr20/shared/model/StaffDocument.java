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
@Document(collection = "staff_documents")
public class StaffDocument {

    @Id
    private String id;
    private String userId;
    private String type; // IDENTITY, PROFESSIONAL_CERT, PROFILE_PHOTO, BANK, CHEQUE
    private String title;
    private String fileUrl;
    private String status = "VERIFIED";
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
