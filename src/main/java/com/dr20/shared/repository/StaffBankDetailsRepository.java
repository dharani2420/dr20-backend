package com.dr20.shared.repository;

import com.dr20.shared.model.StaffBankDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffBankDetailsRepository extends MongoRepository<StaffBankDetails, String> {
    Optional<StaffBankDetails> findByUserId(String userId);
}
