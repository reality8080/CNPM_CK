package nvkho.repository;

import nvkho.entity.PhieuXuatKho;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface PhieuXuatKhoRepository extends MongoRepository<PhieuXuatKho, String> {
    List<PhieuXuatKho> findByTrangThai(String trangThai);
    List<PhieuXuatKho> findByNgayXuatBetween(Date start, Date end);
    List<PhieuXuatKho> findByMaHD(String maHD);
    List<PhieuXuatKho> findByMaNV(String maNV);
}