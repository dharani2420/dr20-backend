package com.dr20.shared.repository;

import com.dr20.shared.model.ServiceCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceCategoryRepository extends MongoRepository<ServiceCategory, String> {
    List<ServiceCategory> findByType(String type);
}
