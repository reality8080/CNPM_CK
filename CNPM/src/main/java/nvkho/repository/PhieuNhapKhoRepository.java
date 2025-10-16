package nvkho.repository;

import nvkho.entity.PhieuNhapKho;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface PhieuNhapKhoRepository extends MongoRepository<PhieuNhapKho, String> {
    List<PhieuNhapKho> findByTrangThai(String trangThai);
    List<PhieuNhapKho> findByNgayNhapBetween(Date start, Date end);
    List<PhieuNhapKho> findByMaNV(String maNV);
}
