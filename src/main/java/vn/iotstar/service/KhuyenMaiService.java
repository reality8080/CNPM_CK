package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.KhuyenMai;
import vn.iotstar.entity.SanPham;
import vn.iotstar.repository.KhuyenMaiRepository;
import vn.iotstar.repository.SanPhamRepository;

@Service
public class KhuyenMaiService {
    @Autowired
    private KhuyenMaiRepository repository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    public List<KhuyenMai> getAll() {
        return repository.findAll();
    }

    public List<KhuyenMai> searchByMaGiamGia(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        return repository.findByMaGiamGiaContaining(keyword);
    }

    public Optional<KhuyenMai> getById(String maKM) {
        return repository.findById(maKM);
    }

    public KhuyenMai save(KhuyenMai khuyenMai) {
        return repository.save(khuyenMai);
    }

    public void deleteById(String maKM) {
        repository.deleteById(maKM);
    }
    public List<SanPham> getProductsByMaSPList(List<String> maSPList) {
        return sanPhamRepository.findAllById(maSPList);
    }
}