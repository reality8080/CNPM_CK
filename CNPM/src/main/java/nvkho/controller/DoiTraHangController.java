package nvkho.controller;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.DoiTraHangRequest;
import nvkho.dto.response.ApiResponse;
import nvkho.dto.response.DoiTraHangResponse;
import nvkho.service.DoiTraHangService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doitra")
@RequiredArgsConstructor
public class DoiTraHangController {
    
    private final DoiTraHangService doiTraHangService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<DoiTraHangResponse>> createDoiTra(@RequestBody DoiTraHangRequest request) {
        DoiTraHangResponse response = doiTraHangService.createDoiTra(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo phiếu đổi trả thành công", response));
    }
    
    @PutMapping("/{maDoiTra}")
    public ResponseEntity<ApiResponse<DoiTraHangResponse>> updateDoiTra(
            @PathVariable String maDoiTra,
            @RequestBody DoiTraHangRequest request) {
        DoiTraHangResponse response = doiTraHangService.updateDoiTra(maDoiTra, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật đổi trả thành công", response));
    }
    @PutMapping("/{maDoiTra}/xuly")
    public ResponseEntity<ApiResponse<DoiTraHangResponse>> xuLyDoiTra(@PathVariable String maDoiTra) {
        DoiTraHangResponse response = doiTraHangService.xuLyDoiTra(maDoiTra);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xử lý đổi trả thành công", response));
    }

    @DeleteMapping("/{maDoiTra}")
    public ResponseEntity<ApiResponse<Void>> deleteDoiTra(@PathVariable String maDoiTra) {
        doiTraHangService.deleteDoiTra(maDoiTra);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa đổi trả thành công", null));
    }
    @GetMapping("/{maDoiTra}")
    public ResponseEntity<ApiResponse<DoiTraHangResponse>> getDoiTraById(@PathVariable String maDoiTra) {
        DoiTraHangResponse response = doiTraHangService.getDoiTraById(maDoiTra);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin đổi trả thành công", response));
    }
    
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<DoiTraHangResponse>>> getAllDoiTra() {
        List<DoiTraHangResponse> response = doiTraHangService.getAllDoiTra();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách đổi trả thành công", response));
    }
}
