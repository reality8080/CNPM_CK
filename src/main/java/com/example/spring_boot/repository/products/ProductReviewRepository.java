package com.example.spring_boot.repository.products;



import com.example.spring_boot.domains.products.ProductReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {

    @Query("{ 'productId': ?0, 'deletedAt': null }")
    List<ProductReview> findActiveByProductId(Object productId);

    long countByProductId(Object productId);
}