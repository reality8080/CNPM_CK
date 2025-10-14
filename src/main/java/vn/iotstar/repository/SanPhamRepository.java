package vn.iotstar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.iotstar.entity.SanPham;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepository extends MongoRepository<SanPham, String> {
	List<SanPham> findByMaDanhMuc(String maDanhMuc);
	List<SanPham> findByMaSPContainingOrTenSPContaining(String maSP, String tenSP);
	Optional<SanPham> findByMaSP(String maSP);
    List<SanPham> findAllById(Iterable<String> maSPList);

    
}