package nvkho.repository;

import nvkho.entity.ChiTietXuatKho;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChiTietXuatKhoRepository extends MongoRepository<ChiTietXuatKho, String> {
    List<ChiTietXuatKho> findByPhieuXuatKhoMaPXK(String maPXK);
    List<ChiTietXuatKho> findBySanPhamMaSP(String maSP);
}