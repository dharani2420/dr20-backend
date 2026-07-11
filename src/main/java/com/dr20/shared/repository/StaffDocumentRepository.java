package com.dr20.shared.repository;

import com.dr20.shared.model.StaffDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffDocumentRepository extends MongoRepository<StaffDocument, String> {
    List<StaffDocument> findByUserId(String userId);
}
