package com.dr20.shared.repository;

import com.dr20.shared.model.WorkingHoursSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkingHoursSettingsRepository extends MongoRepository<WorkingHoursSettings, String> {
    Optional<WorkingHoursSettings> findByDoctorId(String doctorId);
}
