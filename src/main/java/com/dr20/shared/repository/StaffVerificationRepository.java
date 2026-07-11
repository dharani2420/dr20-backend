package com.dr20.shared.repository;

import com.dr20.shared.model.StaffVerification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffVerificationRepository extends MongoRepository<StaffVerification, String> {
    Optional<StaffVerification> findByUserId(String userId);
}
