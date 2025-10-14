package vn.iotstar.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.entity.SanPham;
import vn.iotstar.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<SanPham> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search")
    public List<SanPham> searchProducts(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return productService.getAllProducts();
        }
        return productService.searchProducts(keyword);
    }
    
    @GetMapping("/{maSP}")
    public ResponseEntity<SanPham> getProductDetail(@PathVariable String maSP) {
        Optional<SanPham> product = productService.getProductByMaSP(maSP);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{maDanhMuc}")
    public List<SanPham> getProductsByCategory(@PathVariable String maDanhMuc) {
        return productService.getProductsByCategory(maDanhMuc);
    }

    @PostMapping
    public ResponseEntity<SanPham> addProduct(@Valid @RequestBody SanPham sanPham) {
        System.out.println("Dữ liệu nhận từ request: " + (sanPham != null ? sanPham.toString() : "null"));
        try {
            SanPham savedProduct = productService.saveProduct(sanPham);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu sản phẩm: " + e.getMessage());
            return ResponseEntity.status(500).body(null); // Trả về lỗi 500 với thông báo
        }
    }
    
    
    @PutMapping("/{maSP}")
    public ResponseEntity<SanPham> updateProduct(@PathVariable String maSP, @Valid @RequestBody SanPham sanPham) {
        Optional<SanPham> existingProduct = productService.getProductByMaSP(maSP);
        if (existingProduct.isPresent()) {
            sanPham.setMaSP(maSP);
            SanPham updatedProduct = productService.saveProduct(sanPham);
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{maSP}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String maSP) {
        if (productService.getProductByMaSP(maSP).isPresent()) {
            productService.deleteProduct(maSP);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}