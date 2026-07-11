package com.dr20.shared.repository;

import com.dr20.shared.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByDoctorIdOrderByCreatedAtDesc(String doctorId);
    Optional<Review> findByAppointmentId(String appointmentId);
}
