package nvkho.repository;

import nvkho.entity.SanPham;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends MongoRepository<SanPham, String> {
    List<SanPham> findByDanhMucMaDM(String maDM);
    List<SanPham> findBySoLuongTonLessThan(Integer soLuong);
    List<SanPham> findByTenSPContainingIgnoreCase(String tenSP);
    Optional<SanPham> findByTenSP(String tenSP);
}