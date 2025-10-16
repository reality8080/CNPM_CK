package nvkho.controller;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.PhieuNhapKhoRequest;
import nvkho.dto.response.ApiResponse;
import nvkho.dto.response.PhieuNhapKhoResponse;
import nvkho.service.PhieuNhapKhoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/nhapkho")
@RequiredArgsConstructor
public class PhieuNhapKhoController {
    
    private final PhieuNhapKhoService phieuNhapKhoService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<PhieuNhapKhoResponse>> createPhieuNhap(@RequestBody PhieuNhapKhoRequest request) {
        PhieuNhapKhoResponse response = phieuNhapKhoService.createPhieuNhap(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo phiếu nhập kho thành công", response));
    }
    
    @PutMapping("/{maPNK}/trangthai")
    public ResponseEntity<ApiResponse<PhieuNhapKhoResponse>> updateTrangThai(
            @PathVariable String maPNK,
            @RequestParam String trangThai) {
        PhieuNhapKhoResponse response = phieuNhapKhoService.updateTrangThai(maPNK, trangThai);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật trạng thái thành công", response));
    }
    
    @GetMapping("/{maPNK}")
    public ResponseEntity<ApiResponse<PhieuNhapKhoResponse>> getPhieuNhapById(@PathVariable String maPNK) {
        PhieuNhapKhoResponse response = phieuNhapKhoService.getPhieuNhapById(maPNK);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phiếu nhập thành công", response));
    }
    
    @DeleteMapping("/{maPNK}")
    public ResponseEntity<ApiResponse<Void>> deletePhieuNhap(@PathVariable String maPNK) {
        phieuNhapKhoService.deletePhieuNhap(maPNK);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa phiếu nhập thành công", null));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PhieuNhapKhoResponse>>> getAllPhieuNhap() {
        List<PhieuNhapKhoResponse> response = phieuNhapKhoService.getAllPhieuNhap();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phiếu nhập thành công", response));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<PhieuNhapKhoResponse>>> getPhieuNhapByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<PhieuNhapKhoResponse> response = phieuNhapKhoService.getPhieuNhapByDateRange(start, end);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy phiếu nhập theo khoảng thời gian thành công", response));
    }
    
    @GetMapping("/trangthai/{trangThai}")
    public ResponseEntity<ApiResponse<List<PhieuNhapKhoResponse>>> getPhieuNhapByTrangThai(@PathVariable String trangThai) {
        List<PhieuNhapKhoResponse> response = phieuNhapKhoService.getPhieuNhapByTrangThai(trangThai);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy phiếu nhập theo trạng thái thành công", response));
    }
}
