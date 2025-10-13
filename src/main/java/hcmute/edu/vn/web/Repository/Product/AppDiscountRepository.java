package hcmute.edu.vn.web.Repository.Product;

import hcmute.edu.vn.web.Entity.Product.AppDiscount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppDiscountRepository extends MongoRepository<AppDiscount, String> {
    List<AppDiscount> findByShopId(String shopId);
    List<AppDiscount> findByStatus(String status);
}
