package com.example.storemanager.repository;

import com.example.storemanager.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * Repository cho sản phẩm.
 */
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
}
