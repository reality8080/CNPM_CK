package com.example.storemanager.controller;

import com.example.storemanager.dto.ProductDTO;
import com.example.storemanager.entity.Product;
import com.example.storemanager.repository.ProductRepository;
import com.example.storemanager.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/manager/products")
@RequiredArgsConstructor
@CrossOrigin // nếu frontend chạy port khác
public class ProductController {

    private final ProductRepository productRepo;
    private final CloudinaryService cloudService;

    @GetMapping
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        return productRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Product> addProduct(
            @RequestPart("product") ProductDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        List<String> urls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                try {
                    String url = cloudService.uploadImage(file);
                    urls.add(url);
                } catch (IOException e) {
                    return ResponseEntity.status(500).<Product>build();
                }
            }
        }

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .quantity(dto.getQuantity())
                .categoryId(dto.getCategoryId())
                .images(urls)
                .price(dto.getPrice())
                .build();

        return ResponseEntity.ok(productRepo.save(product));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> update(
            @PathVariable String id,
            @RequestPart("product") ProductDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return productRepo.findById(id).map(existing -> {
            List<String> urls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                try {
                    for (MultipartFile file : images) {
                        String url = cloudService.uploadImage(file);
                        urls.add(url);
                    }
                } catch (IOException e) {
                    return ResponseEntity.status(500).<Product>build();
                }
            }

            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setQuantity(dto.getQuantity());
            existing.setCategoryId(dto.getCategoryId());
            existing.setPrice(dto.getPrice());
            if (!urls.isEmpty()) {
                List<String> merged = new ArrayList<>();
                if (existing.getImages() != null) merged.addAll(existing.getImages());
                merged.addAll(urls);
                existing.setImages(merged);
            }
            return ResponseEntity.ok(productRepo.save(existing));
        }).orElseGet(() -> ResponseEntity.status(404).<Product>build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!productRepo.existsById(id)) return ResponseEntity.notFound().build();
        productRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
