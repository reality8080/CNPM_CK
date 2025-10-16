package nvkho.repository;

import nvkho.entity.KiemKe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface KiemKeRepository extends MongoRepository<KiemKe, String> {
    List<KiemKe> findByTrangThai(String trangThai);
    List<KiemKe> findByNgayKiemKeBetween(Date start, Date end);
    List<KiemKe> findByMaNV(String maNV);
}