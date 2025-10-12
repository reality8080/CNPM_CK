package nvkho.service;

import nvkho.dto.request.SanPhamRequest;
import nvkho.dto.response.SanPhamResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SanPhamService {
    SanPhamResponse createSanPham(SanPhamRequest request, MultipartFile file);
    SanPhamResponse updateSanPham(String maSP, SanPhamRequest request, MultipartFile file);
    void deleteSanPham(String maSP);
    SanPhamResponse getSanPhamById(String maSP);
    List<SanPhamResponse> getAllSanPham();
    List<SanPhamResponse> searchSanPham(String keyword);
    List<SanPhamResponse> getSanPhamByDanhMuc(String maDM);
}
