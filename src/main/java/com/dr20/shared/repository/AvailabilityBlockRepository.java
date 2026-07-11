package com.dr20.shared.repository;

import com.dr20.shared.model.AvailabilityBlock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityBlockRepository extends MongoRepository<AvailabilityBlock, String> {
    List<AvailabilityBlock> findByDoctorId(String doctorId);
    Optional<AvailabilityBlock> findByDoctorIdAndDate(String doctorId, String date);
}
