package nvkho.service.impl;

import lombok.RequiredArgsConstructor;
import nvkho.dto.response.ThongKeResponse;
import nvkho.entity.PhieuNhapKho;
import nvkho.entity.PhieuXuatKho;
import nvkho.entity.SanPham;
import nvkho.repository.*;
import nvkho.service.ThongKeService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {
    
    private final SanPhamRepository sanPhamRepository;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final PhieuXuatKhoRepository phieuXuatKhoRepository;
    
    @Override
    public ThongKeResponse getThongKeChung() {
        ThongKeResponse response = new ThongKeResponse();
        
        List<SanPham> sanPhamList = sanPhamRepository.findAll();
        response.setTongSanPham((long) sanPhamList.size());
        
        Integer tongSoLuongTon = sanPhamList.stream()
                .mapToInt(SanPham::getSoLuongTon)
                .sum();
        response.setTongSoLuongTon(tongSoLuongTon);
        
        Double giaTriTonKho = sanPhamList.stream()
                .mapToDouble(sp -> sp.getSoLuongTon() * sp.getGiaBan())
                .sum();
        response.setGiaTriTonKho(giaTriTonKho);
        
        List<PhieuNhapKho> phieuNhapList = phieuNhapKhoRepository.findAll();
        response.setSoPhieuNhap((long) phieuNhapList.size());
        
        Double tongGiaTriNhap = phieuNhapList.stream()
                .filter(p -> "DaNhap".equals(p.getTrangThai()))
                .mapToDouble(PhieuNhapKho::getTongTien)
                .sum();
        response.setTongGiaTriNhap(tongGiaTriNhap);
        
        List<PhieuXuatKho> phieuXuatList = phieuXuatKhoRepository.findAll();
        response.setSoPhieuXuat((long) phieuXuatList.size());
        
        Map<String, Long> thongKeTheoDanhMuc = sanPhamList.stream()
                .collect(Collectors.groupingBy(
                        sp -> sp.getDanhMuc().getTenDM(),
                        Collectors.counting()
                ));
        response.setThongKeTheoDanhMuc(thongKeTheoDanhMuc);
        
        Map<String, Integer> sanPhamSapHetHang = sanPhamRepository.findBySoLuongTonLessThan(10).stream()
                .collect(Collectors.toMap(
                        SanPham::getTenSP,
                        SanPham::getSoLuongTon
                ));
        response.setSanPhamSapHetHang(sanPhamSapHetHang);
        
        return response;
    }
    
    @Override
    public ThongKeResponse getThongKeTheoKhoangThoiGian(Date start, Date end) {
        ThongKeResponse response = new ThongKeResponse();
        
        List<PhieuNhapKho> phieuNhapList = phieuNhapKhoRepository.findByNgayNhapBetween(start, end);
        response.setSoPhieuNhap((long) phieuNhapList.size());
        
        Double tongGiaTriNhap = phieuNhapList.stream()
                .filter(p -> "DaNhap".equals(p.getTrangThai()))
                .mapToDouble(PhieuNhapKho::getTongTien)
                .sum();
        response.setTongGiaTriNhap(tongGiaTriNhap);
        
        List<PhieuXuatKho> phieuXuatList = phieuXuatKhoRepository.findByNgayXuatBetween(start, end);
        response.setSoPhieuXuat((long) phieuXuatList.size());
        
        return response;
    }
}