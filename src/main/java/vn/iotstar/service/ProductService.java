package vn.iotstar.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.SanPham;
import vn.iotstar.repository.SanPhamRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
	@Autowired
    private SanPhamRepository repository;

    public List<SanPham> getAllProducts() {
        return repository.findAll();
    }

    public List<SanPham> searchProducts(String keyword) {
        return repository.findByMaSPContainingOrTenSPContaining(keyword, keyword);
    }

    public SanPham saveProduct(SanPham sanPham) {
        return repository.save(sanPham);
    }
    
    public Optional<SanPham> getProductByMaSP(String maSP) {
        return repository.findByMaSP(maSP);
    }
    
    public List<SanPham> getProductsByCategory(String maDanhMuc) {
        return repository.findByMaDanhMuc(maDanhMuc);
    }
    
    public void deleteProduct(String maSP) {
        repository.deleteById(maSP);
    }
}