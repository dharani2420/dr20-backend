package com.dr20.shared.repository;

import com.dr20.shared.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByNameContainingIgnoreCase(String name);
    List<Doctor> findByIsAvailableTrue();
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
