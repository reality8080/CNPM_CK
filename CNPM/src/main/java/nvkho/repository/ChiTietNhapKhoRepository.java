package nvkho.repository;

import nvkho.entity.ChiTietNhapKho;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChiTietNhapKhoRepository extends MongoRepository<ChiTietNhapKho, String> {
    List<ChiTietNhapKho> findByPhieuNhapKhoMaPNK(String maPNK);
    List<ChiTietNhapKho> findBySanPhamMaSP(String maSP);
}