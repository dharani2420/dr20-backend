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
@Document(collection = "staff_bank_details")
public class StaffBankDetails {

    @Id
    private String id;
    private String userId;
    private String bankName;
    private String accountNumber;
    private String maskedAccountNumber;
    private String ifscCode;
    private String status = "VERIFIED";
    private String documentUrl;
    private LocalDateTime updatedAt = LocalDateTime.now();
}
