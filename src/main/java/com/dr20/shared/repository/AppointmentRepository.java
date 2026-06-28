package com.dr20.shared.repository;

import com.dr20.shared.model.Appointment;
import com.dr20.common.enums.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Appointment> findByDoctorIdAndAppointmentDate(String doctorId, String date);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(String doctorId);

    Optional<Appointment> findByQrData(String qrData);

    Optional<Appointment> findByTokenNumber(String tokenNumber);

    long countByDoctorIdAndAppointmentDateAndStatusIn(
            String doctorId, String date, List<AppointmentStatus> statuses);
}
