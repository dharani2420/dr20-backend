package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "otp_logs")
public class OtpLog {

    @Id
    private String id;

    @Indexed(unique = true)
    private String phone;

    private int count;
    private LocalDateTime windowStart;
    private LocalDateTime lastSentAt;
}
