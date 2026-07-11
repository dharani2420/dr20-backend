package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "doctors")
public class Doctor {

    @Id
    private String id;

    private String userId;

    @NotBlank(message = "Name is required")
    private String name;

    private String degree; // e.g. MBBS, MD

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String hospital;

    // New fields from Figma
    private Double rating = 4.5;
    private Integer experience;         // years of experience
    private Integer totalConsultations;
    private Double consultationFee;
    private String profileImage;
    private String about;
    private List<String> availableDays; // ["Monday","Tuesday",...]
    private List<String> availableSlots; // ["9:00 AM","10:00 AM",...]
    private String clinicAddress;
    private String clinicName;
    private String clinicType; // DR20_CLINIC, PRIVATE_CLINIC
    private String consultationMode; // In-person, Online
    private Integer travelRadiusKm;
    private List<String> languages;
    private String registrationNumber;
    private Double latitude;
    private Double longitude;
    private Boolean isAvailable = true;
    private Boolean isVerified = true;
    private Boolean isClinicVerified = true;
    private Integer reviewCount;
    private List<String> expertise;
    private String clinicHours;

    @Transient
    private Double distanceKm;
}
