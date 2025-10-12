package nvkho.controller;

import lombok.RequiredArgsConstructor;
import nvkho.dto.response.ApiResponse;
import nvkho.dto.response.ThongKeResponse;
import nvkho.service.ThongKeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/baocao")
@RequiredArgsConstructor
public class ThongKeController {
    
    private final ThongKeService thongKeService;
    
    @GetMapping("/chung")
    public ResponseEntity<ApiResponse<ThongKeResponse>> getThongKeChung() {
        ThongKeResponse response = thongKeService.getThongKeChung();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê chung thành công", response));
    }
    
    @GetMapping("/khoang-thoi-gian")
    public ResponseEntity<ApiResponse<ThongKeResponse>> getThongKeTheoKhoangThoiGian(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        ThongKeResponse response = thongKeService.getThongKeTheoKhoangThoiGian(start, end);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê theo khoảng thời gian thành công", response));
    }
}
