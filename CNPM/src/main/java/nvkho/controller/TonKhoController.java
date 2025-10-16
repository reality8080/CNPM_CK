package nvkho.controller;

import lombok.RequiredArgsConstructor;
import nvkho.dto.response.ApiResponse;
import nvkho.dto.response.TonKhoResponse;
import nvkho.service.TonKhoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tonkho")
@RequiredArgsConstructor
public class TonKhoController {
    
    private final TonKhoService tonKhoService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TonKhoResponse>>> getAllTonKho() {
        List<TonKhoResponse> response = tonKhoService.getAllTonKho();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách tồn kho thành công", response));
    }
    
    @GetMapping("/{maSP}")
    public ResponseEntity<ApiResponse<TonKhoResponse>> getTonKhoBySanPham(@PathVariable String maSP) {
        TonKhoResponse response = tonKhoService.getTonKhoBySanPham(maSP);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tồn kho sản phẩm thành công", response));
    }
    
    @GetMapping("/canh-bao")
    public ResponseEntity<ApiResponse<List<TonKhoResponse>>> getSanPhamSapHetHang(@RequestParam(defaultValue = "10") Integer nguong) {
        List<TonKhoResponse> response = tonKhoService.getSanPhamSapHetHang(nguong);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách sản phẩm sắp hết hàng thành công", response));
    }
}