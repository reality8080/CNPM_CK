package nvkho.controller;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.SanPhamRequest;
import nvkho.dto.response.ApiResponse;
import nvkho.dto.response.SanPhamResponse;
import nvkho.service.SanPhamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/sanpham")
@RequiredArgsConstructor
public class SanPhamController {
    
    private final SanPhamService sanPhamService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<SanPhamResponse>> createSanPham(
            @RequestPart("data") SanPhamRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        SanPhamResponse response = sanPhamService.createSanPham(request, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo sản phẩm thành công", response));
    }
    
    @PutMapping("/{maSP}")
    public ResponseEntity<ApiResponse<SanPhamResponse>> updateSanPham(
            @PathVariable String maSP,
            @RequestPart("data") SanPhamRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        SanPhamResponse response = sanPhamService.updateSanPham(maSP, request, file);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật sản phẩm thành công", response));
    }
    
    @DeleteMapping("/{maSP}")
    public ResponseEntity<ApiResponse<Void>> deleteSanPham(@PathVariable String maSP) {
        sanPhamService.deleteSanPham(maSP);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa sản phẩm thành công", null));
    }
    
    @GetMapping("/{maSP}")
    public ResponseEntity<ApiResponse<SanPhamResponse>> getSanPhamById(@PathVariable String maSP) {
        SanPhamResponse response = sanPhamService.getSanPhamById(maSP);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin sản phẩm thành công", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SanPhamResponse>>> getAllSanPham() {
        List<SanPhamResponse> response = sanPhamService.getAllSanPham();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách sản phẩm thành công", response));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SanPhamResponse>>> searchSanPham(@RequestParam String keyword) {
        List<SanPhamResponse> response = sanPhamService.searchSanPham(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm sản phẩm thành công", response));
    }
    
    @GetMapping("/danhmuc/{maDM}")
    public ResponseEntity<ApiResponse<List<SanPhamResponse>>> getSanPhamByDanhMuc(@PathVariable String maDM) {
        List<SanPhamResponse> response = sanPhamService.getSanPhamByDanhMuc(maDM);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy sản phẩm theo danh mục thành công", response));
    }
}