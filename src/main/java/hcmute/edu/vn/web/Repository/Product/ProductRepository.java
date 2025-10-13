package hcmute.edu.vn.web.Repository.Product;

import hcmute.edu.vn.web.Entity.Product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
    List<Product> findBySupplierId(String supplierId);
    List<Product> findByStatus(String status);
}
