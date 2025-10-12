package nvkho.repository;

import nvkho.entity.DoiTraHang;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoiTraHangRepository extends MongoRepository<DoiTraHang, String> {
    List<DoiTraHang> findBySanPhamMaSP(String maSP);
}