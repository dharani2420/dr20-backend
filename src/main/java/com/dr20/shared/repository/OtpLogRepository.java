package com.dr20.shared.repository;

import com.dr20.shared.model.OtpLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpLogRepository extends MongoRepository<OtpLog, String> {
    Optional<OtpLog> findByPhone(String phone);
}
