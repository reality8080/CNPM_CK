package hcmute.edu.vn.web.Repository.Product;

import hcmute.edu.vn.web.Entity.Product.PromotionProgram;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionProgramRepository extends MongoRepository<PromotionProgram, String> {
    List<PromotionProgram> findByPromotionType(String promotionType);
    List<PromotionProgram> findByStatus(String status);
}