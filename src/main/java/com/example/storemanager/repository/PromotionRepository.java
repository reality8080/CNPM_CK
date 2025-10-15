package com.example.storemanager.repository;

import com.example.storemanager.entity.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository cho các chương trình khuyến mãi.
 */
public interface PromotionRepository extends MongoRepository<Promotion, String> {
}
