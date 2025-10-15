package com.example.storemanager.repository;

import com.example.storemanager.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repository cho người dùng (Manager)
 */
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
