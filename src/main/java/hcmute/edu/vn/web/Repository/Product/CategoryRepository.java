package hcmute.edu.vn.web.Repository.Product;

import hcmute.edu.vn.web.Entity.Product.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByCategoryNameContainingIgnoreCase(String name);
}
