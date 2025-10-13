package hcmute.edu.vn.web.Repository.Product;

import hcmute.edu.vn.web.Entity.Product.Supplier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends MongoRepository<Supplier, String> {
    List<Supplier> findBySupplierNameContainingIgnoreCase(String name);
    List<Supplier> findByStatus(String status);
}
