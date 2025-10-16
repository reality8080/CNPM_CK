package nvkho.controller;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.KiemKeRequest;
import nvkho.dto.response.ApiResponse;
import nvkho.dto.response.KiemKeResponse;
import nvkho.service.KiemKeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kiemke")
@RequiredArgsConstructor
public class KiemKeController {

    private final KiemKeService kiemKeService;

    @PostMapping
    public ResponseEntity<ApiResponse<KiemKeResponse>> createKiemKe(@RequestBody KiemKeRequest request) {
        KiemKeResponse response = kiemKeService.createKiemKe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Tạo phiếu kiểm kê thành công", response));
    }

    @PutMapping("/{maKiemKe}/xacnhan")
    public ResponseEntity<ApiResponse<KiemKeResponse>> xacNhanKiemKe(@PathVariable String maKiemKe) {
        KiemKeResponse response = kiemKeService.xacNhanKiemKe(maKiemKe);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xác nhận kiểm kê thành công", response));
    }

    @GetMapping("/{maKiemKe}")
    public ResponseEntity<ApiResponse<KiemKeResponse>> getKiemKeById(@PathVariable String maKiemKe) {
        KiemKeResponse response = kiemKeService.getKiemKeById(maKiemKe);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin kiểm kê thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KiemKeResponse>>> getAllKiemKe() {
        List<KiemKeResponse> response = kiemKeService.getAllKiemKe();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách kiểm kê thành công", response));
    }
}