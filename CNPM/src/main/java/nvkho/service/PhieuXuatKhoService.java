package nvkho.service;

import nvkho.dto.request.PhieuXuatKhoRequest;
import nvkho.dto.response.PhieuXuatKhoResponse;
import java.util.Date;
import java.util.List;

public interface PhieuXuatKhoService {
    PhieuXuatKhoResponse createPhieuXuat(PhieuXuatKhoRequest request);
    PhieuXuatKhoResponse updateTrangThai(String maPXK, String trangThai);
    PhieuXuatKhoResponse getPhieuXuatById(String maPXK);
    List<PhieuXuatKhoResponse> getAllPhieuXuat();
    List<PhieuXuatKhoResponse> getPhieuXuatByDateRange(Date start, Date end);
    List<PhieuXuatKhoResponse> getPhieuXuatByTrangThai(String trangThai);
}