package com.dr20.shared.repository;

import com.dr20.shared.model.User;
import com.dr20.common.enums.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByPhone(String phone);
    List<User> findByRole(UserRole role);
}
