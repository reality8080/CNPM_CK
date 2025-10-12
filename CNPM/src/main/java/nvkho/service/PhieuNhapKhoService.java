package nvkho.service;

import nvkho.dto.request.PhieuNhapKhoRequest;
import nvkho.dto.response.PhieuNhapKhoResponse;
import java.util.Date;
import java.util.List;

public interface PhieuNhapKhoService {
    PhieuNhapKhoResponse createPhieuNhap(PhieuNhapKhoRequest request);
    PhieuNhapKhoResponse updateTrangThai(String maPNK, String trangThai);
    PhieuNhapKhoResponse getPhieuNhapById(String maPNK);
    List<PhieuNhapKhoResponse> getAllPhieuNhap();
    List<PhieuNhapKhoResponse> getPhieuNhapByDateRange(Date start, Date end);
    List<PhieuNhapKhoResponse> getPhieuNhapByTrangThai(String trangThai);
}