package com.dr20.shared.model;

import com.dr20.common.enums.UserRole;
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
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String phone;

    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String profileImage;

    private UserRole role = UserRole.PATIENT;
    private UserRole profession;
    private String linkedProfileId;

    private String otp;
    private LocalDateTime otpExpiry;
    private boolean phoneVerified = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
