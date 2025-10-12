package nvkho.service.impl;

import nvkho.dto.request.KiemKeRequest;
import nvkho.entity.KiemKe;
import nvkho.entity.SanPham;
import nvkho.exception.ResourceNotFoundException;
import nvkho.repository.KiemKeRepository;
import nvkho.repository.SanPhamRepository;
import nvkho.service.KiemKeService;
import nvkho.util.MaPhieuGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KiemKeServiceImpl implements KiemKeService {
    
    private final KiemKeRepository kiemKeRepository;
    private final SanPhamRepository sanPhamRepository;
    
    @Override
    @Transactional
    public KiemKe createKiemKe(KiemKeRequest request) {
        KiemKe kiemKe = new KiemKe();
        kiemKe.setMaKiemKe(MaPhieuGenerator.generateMaKiemKe());
        kiemKe.setNgayKiemKe(request.getNgayKiemKe() != null ? request.getNgayKiemKe() : new Date());
        kiemKe.setMaNV(request.getMaNV());
        kiemKe.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : "DangXuLy");
        kiemKe.setGhiChu(request.getGhiChu());
        
        List<KiemKe.ChiTietKiemKe> chiTietList = new ArrayList<>();
        
        for (KiemKeRequest.ChiTietKiemKeRequest chiTietRequest : request.getChiTiet()) {
            SanPham sanPham = sanPhamRepository.findById(chiTietRequest.getMaSP())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + chiTietRequest.getMaSP()));
            
            KiemKe.ChiTietKiemKe chiTiet = new KiemKe.ChiTietKiemKe();
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuongHeThong(sanPham.getSoLuongTon());
            chiTiet.setSoLuongThucTe(chiTietRequest.getSoLuongThucTe());
            chiTiet.setChenhLech(chiTietRequest.getSoLuongThucTe() - sanPham.getSoLuongTon());
            chiTiet.setGhiChu(chiTietRequest.getGhiChu());
            
            chiTietList.add(chiTiet);
        }
        
        kiemKe.setChiTietKiemKe(chiTietList);
        return kiemKeRepository.save(kiemKe);
    }
    
    @Override
    @Transactional
    public KiemKe xacNhanKiemKe(String maKiemKe) {
        KiemKe kiemKe = kiemKeRepository.findById(maKiemKe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu kiểm kê"));
        
        kiemKe.setTrangThai("DaXacNhan");
        
        for (KiemKe.ChiTietKiemKe chiTiet : kiemKe.getChiTietKiemKe()) {
            SanPham sanPham = chiTiet.getSanPham();
            sanPham.setSoLuongTon(chiTiet.getSoLuongThucTe());
            sanPhamRepository.save(sanPham);
        }
        
        return kiemKeRepository.save(kiemKe);
    }
    
    @Override
    public KiemKe getKiemKeById(String maKiemKe) {
        return kiemKeRepository.findById(maKiemKe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu kiểm kê"));
    }
    
    @Override
    public List<KiemKe> getAllKiemKe() {
        return kiemKeRepository.findAll();
    }
}