package nvkho.repository;

import nvkho.entity.SanPham;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamRepository extends MongoRepository<SanPham, String> {
    List<SanPham> findByDanhMucMaDM(String maDM);
    List<SanPham> findBySoLuongTonLessThan(Integer soLuong);
    List<SanPham> findByTenSPContainingIgnoreCase(String tenSP);
}
