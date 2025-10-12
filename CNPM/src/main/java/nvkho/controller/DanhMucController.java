package nvkho.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}	