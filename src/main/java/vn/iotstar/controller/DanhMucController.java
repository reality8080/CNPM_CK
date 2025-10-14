package vn.iotstar.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.entity.DanhMuc;
import vn.iotstar.service.DanhMucService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class DanhMucController {
    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public List<DanhMuc> getAllCategories() {
        return danhMucService.getAllCategories();
    }

    @GetMapping("/{maDM}")
    public ResponseEntity<DanhMuc> getCategoryById(@PathVariable String maDM) {
        Optional<DanhMuc> category = danhMucService.getCategoryById(maDM);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DanhMuc> addCategory(@Valid @RequestBody DanhMuc danhMuc) {
        DanhMuc savedCategory = danhMucService.saveCategory(danhMuc);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{maDM}")
    public ResponseEntity<DanhMuc> updateCategory(@PathVariable String maDM, @Valid @RequestBody DanhMuc danhMuc) {
        Optional<DanhMuc> existingCategory = danhMucService.getCategoryById(maDM);
        if (existingCategory.isPresent()) {
            danhMuc.setMaDM(maDM); // Đảm bảo giữ nguyên maDM
            DanhMuc updatedCategory = danhMucService.saveCategory(danhMuc);
            return ResponseEntity.ok(updatedCategory);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{maDM}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String maDM) {
        if (danhMucService.getCategoryById(maDM).isPresent()) {
            danhMucService.deleteCategory(maDM);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}