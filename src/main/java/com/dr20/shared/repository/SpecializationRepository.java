package com.dr20.shared.repository;

import com.dr20.shared.model.Specialization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends MongoRepository<Specialization, String> {
}
