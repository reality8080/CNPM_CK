package vn.iotstar.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import vn.iotstar.entity.DoiTraHang;

import java.util.Date;
import java.util.List;

public interface DoiTraHangRepository extends MongoRepository<DoiTraHang, String> {
    List<DoiTraHang> findByMaHD(String maHD);
    List<DoiTraHang> findByNgayDoiTraBetween(Date startDate, Date endDate);
}