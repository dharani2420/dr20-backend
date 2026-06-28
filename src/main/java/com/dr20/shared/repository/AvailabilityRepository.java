package com.dr20.shared.repository;

import com.dr20.shared.model.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailabilityRepository extends MongoRepository<Availability, String> {
    Optional<Availability> findByDoctorIdAndDate(String doctorId, String date);
}
