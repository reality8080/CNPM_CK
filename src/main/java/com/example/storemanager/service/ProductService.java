package com.example.storemanager.service;

import com.example.storemanager.entity.Product;
import com.example.storemanager.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> findAll() { return repo.findAll(); }

    public Product save(Product p) { return repo.save(p); }

    public Product update(String id, Product newP) {
        return repo.findById(id).map(p -> {
            p.setName(newP.getName());
            p.setDescription(newP.getDescription());
            p.setQuantity(newP.getQuantity());
            p.setCategoryId(newP.getCategoryId());
            p.setImages(newP.getImages());
            return repo.save(p);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
    }

    public void delete(String id) { repo.deleteById(id); }
}
