package vn.iotstar.repository;



import org.springframework.data.mongodb.repository.MongoRepository;
import vn.iotstar.entity.KhachHang;

import java.util.List;

public interface KhachHangRepository extends MongoRepository<KhachHang, String> {
    List<KhachHang> findByHoTenContainingIgnoreCase(String hoTen);
}