package vn.iotstar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.iotstar.entity.ChiTietHoaDon;

import java.util.List;

public interface ChiTietHoaDonRepository extends MongoRepository<ChiTietHoaDon, String> {

    List<ChiTietHoaDon> findByMaHD(String maHD);
}