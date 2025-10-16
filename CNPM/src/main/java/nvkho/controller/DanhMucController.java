package nvkho.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nvkho.dto.response.ApiResponse;
import nvkho.entity.DanhMuc;
import nvkho.repository.DanhMucRepository;

@RestController
@RequestMapping("/api/danhmuc")
@RequiredArgsConstructor
public class DanhMucController {
    private final DanhMucRepository danhMucRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<DanhMuc>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", danhMucRepository.findAll()));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<DanhMuc>> create(@RequestBody DanhMuc danhMuc) {
        DanhMuc saved = danhMucRepository.save(danhMuc);
        return ResponseEntity.ok(new ApiResponse<>(true, "Thêm danh mục thành công", saved));
    }

    @PutMapping("/{maDM}")
    public ResponseEntity<ApiResponse<DanhMuc>> update(@PathVariable String maDM, @RequestBody DanhMuc danhMuc) {
        danhMuc.setMaDM(maDM);
        DanhMuc updated = danhMucRepository.save(danhMuc);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật danh mục thành công", updated));
    }

    @DeleteMapping("/{maDM}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String maDM) {
        danhMucRepository.deleteById(maDM);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa danh mục thành công", null));
    }
}	