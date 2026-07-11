package com.dr20.shared.model;

import com.dr20.common.enums.AppointmentStatus;
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
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    private String userId;
    private String doctorId;
    private String doctorName;
    private String specialization;

    private String appointmentDate;
    private String appointmentTime;
    private String consultationType;

    private AppointmentStatus status = AppointmentStatus.UPCOMING;

    private Double consultationFee;
    private Double platformFee;
    private Double totalFee;
    private String paymentStatus;
    private String paymentId;

    @Indexed(unique = true, sparse = true)
    private String qrData;

    @Indexed(unique = true, sparse = true)
    private String tokenNumber;

    private String patientName;
    private String patientAge;
    private String symptoms;
    private String familyMemberId;
    private String consultationNotes;
    private String prescription;

    private String serviceAddress;
    private Double serviceLatitude;
    private Double serviceLongitude;
    private String patientGender;
    private String patientBloodGroup;

    private LocalDateTime arrivedAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
