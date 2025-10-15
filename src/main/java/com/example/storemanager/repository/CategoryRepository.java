// CategoryRepository.java
package com.example.storemanager.repository;

import com.example.storemanager.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {}
