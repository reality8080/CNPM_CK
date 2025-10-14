package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.iotstar.entity.KhuyenMai;

public interface KhuyenMaiRepository extends MongoRepository<KhuyenMai, String> {
    List<KhuyenMai> findByMaGiamGiaContaining(String maGiamGia);
}