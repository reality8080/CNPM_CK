package nvkho.controller;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.PhieuXuatKhoRequest;
import nvkho.dto.response.ApiResponse;
import nvkho.dto.response.PhieuXuatKhoResponse;
import nvkho.service.PhieuXuatKhoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/xuatkho")
@RequiredArgsConstructor
public class PhieuXuatKhoController {
    
    private final PhieuXuatKhoService phieuXuatKhoService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<PhieuXuatKhoResponse>> createPhieuXuat(@RequestBody PhieuXuatKhoRequest request) {
        PhieuXuatKhoResponse response = phieuXuatKhoService.createPhieuXuat(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo phiếu xuất kho thành công", response));
    }
    
    @PutMapping("/{maPXK}/trangthai")
    public ResponseEntity<ApiResponse<PhieuXuatKhoResponse>> updateTrangThai(
            @PathVariable String maPXK,
            @RequestParam String trangThai) {
        PhieuXuatKhoResponse response = phieuXuatKhoService.updateTrangThai(maPXK, trangThai);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật trạng thái thành công", response));
    }
    
    @GetMapping("/{maPXK}")
    public ResponseEntity<ApiResponse<PhieuXuatKhoResponse>> getPhieuXuatById(@PathVariable String maPXK) {
        PhieuXuatKhoResponse response = phieuXuatKhoService.getPhieuXuatById(maPXK);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phiếu xuất thành công", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PhieuXuatKhoResponse>>> getAllPhieuXuat() {
        List<PhieuXuatKhoResponse> response = phieuXuatKhoService.getAllPhieuXuat();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phiếu xuất thành công", response));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<PhieuXuatKhoResponse>>> getPhieuXuatByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<PhieuXuatKhoResponse> response = phieuXuatKhoService.getPhieuXuatByDateRange(start, end);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy phiếu xuất theo khoảng thời gian thành công", response));
    }
    
    @GetMapping("/trangthai/{trangThai}")
    public ResponseEntity<ApiResponse<List<PhieuXuatKhoResponse>>> getPhieuXuatByTrangThai(@PathVariable String trangThai) {
        List<PhieuXuatKhoResponse> response = phieuXuatKhoService.getPhieuXuatByTrangThai(trangThai);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy phiếu xuất theo trạng thái thành công", response));
    }
}