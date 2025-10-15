package com.example.storemanager.controller;

import com.example.storemanager.entity.Category;
import com.example.storemanager.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/categories")
public class CategoryController {
    private final CategoryRepository categoryRepo;

    public CategoryController(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Category cat) {
        if (cat.getName() == null || cat.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
        }
        return ResponseEntity.ok(categoryRepo.save(cat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Category cat) {
        if (cat.getName() == null || cat.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
        }
        return categoryRepo.findById(id)
                .map(c -> {
                    c.setName(cat.getName());
                    c.setDescription(cat.getDescription());
                    return ResponseEntity.ok(categoryRepo.save(c));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return categoryRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!categoryRepo.existsById(id)) return ResponseEntity.notFound().build();
        categoryRepo.deleteById(id);
        return ResponseEntity.ok("Đã xóa");
    }

    

}
