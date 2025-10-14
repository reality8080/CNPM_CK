package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import vn.iotstar.entity.HoaDonBanHang;

import java.util.Date;
import java.util.List;

public interface HoaDonBanHangRepository extends MongoRepository<HoaDonBanHang, String> {

    Page<HoaDonBanHang> findByNgayBanBetween(Date startDate, Date endDate, Pageable pageable);
    Page<HoaDonBanHang> findByTrangThai(String trangThai, Pageable pageable);
    Page<HoaDonBanHang> findByNgayBanBetweenAndTrangThai(Date startDate, Date endDate, String trangThai, Pageable pageable);
    List<HoaDonBanHang> findByNgayBanBetween(Date startDate, Date endDate);
}