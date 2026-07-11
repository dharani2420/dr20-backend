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
@Document(collection = "reviews")
public class Review {

    @Id
    private String id;
    private String doctorId;
    private String userId;
    private String appointmentId;
    private String patientName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
}
