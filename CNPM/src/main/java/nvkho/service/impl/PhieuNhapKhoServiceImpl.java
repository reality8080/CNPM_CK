package nvkho.service.impl;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.ChiTietNhapKhoRequest;
import nvkho.dto.request.PhieuNhapKhoRequest;
import nvkho.dto.response.PhieuNhapKhoResponse;
import nvkho.entity.ChiTietNhapKho;
import nvkho.entity.PhieuNhapKho;
import nvkho.entity.SanPham;
import nvkho.exception.BadRequestException;
import nvkho.exception.ResourceNotFoundException;
import nvkho.repository.ChiTietNhapKhoRepository;
import nvkho.repository.PhieuNhapKhoRepository;
import nvkho.repository.SanPhamRepository;
import nvkho.service.PhieuNhapKhoService;
import nvkho.util.MaPhieuGenerator;
import nvkho.util.TinhToanUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhieuNhapKhoServiceImpl implements PhieuNhapKhoService {
    
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final ChiTietNhapKhoRepository chiTietNhapKhoRepository;
    private final SanPhamRepository sanPhamRepository;
    
    @Override
    @Transactional
    public PhieuNhapKhoResponse createPhieuNhap(PhieuNhapKhoRequest request) {
        if (request.getChiTiet() == null || request.getChiTiet().isEmpty()) {
            throw new BadRequestException("Chi tiết nhập kho không được rỗng");
        }
        
        PhieuNhapKho phieuNhapKho = new PhieuNhapKho();
        phieuNhapKho.setMaPNK(MaPhieuGenerator.generateMaPNK());
        phieuNhapKho.setNgayNhap(request.getNgayNhap() != null ? request.getNgayNhap() : new Date());
        phieuNhapKho.setMaNV(request.getMaNV());
        phieuNhapKho.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : "DangXuLy");
        
        phieuNhapKho = phieuNhapKhoRepository.save(phieuNhapKho);
        
        List<Double> danhSachThanhTien = new ArrayList<>();
        
        for (ChiTietNhapKhoRequest chiTietRequest : request.getChiTiet()) {
            SanPham sanPham = sanPhamRepository.findById(chiTietRequest.getMaSP())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + chiTietRequest.getMaSP()));
            
            Double thanhTien = TinhToanUtil.tinhThanhTien(chiTietRequest.getSoLuong(), chiTietRequest.getDonGiaNhap());
            danhSachThanhTien.add(thanhTien);
            
            ChiTietNhapKho chiTiet = new ChiTietNhapKho();
            chiTiet.setMaCTNK("CTNK" + System.currentTimeMillis());
            chiTiet.setPhieuNhapKho(phieuNhapKho);
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(chiTietRequest.getSoLuong());
            chiTiet.setDonGiaNhap(chiTietRequest.getDonGiaNhap());
            chiTiet.setThanhTien(thanhTien);
            
            chiTietNhapKhoRepository.save(chiTiet);
            
            if ("DaNhap".equals(phieuNhapKho.getTrangThai())) {
                sanPham.setSoLuongTon(sanPham.getSoLuongTon() + chiTietRequest.getSoLuong());
                sanPhamRepository.save(sanPham);
            }
        }
        
        Double tongTien = TinhToanUtil.tinhTongTien(danhSachThanhTien);
        phieuNhapKho.setTongTien(tongTien);
        phieuNhapKho = phieuNhapKhoRepository.save(phieuNhapKho);
        
        return convertToResponse(phieuNhapKho);
    }
    
    @Override
    @Transactional
    public PhieuNhapKhoResponse updateTrangThai(String maPNK, String trangThai) {
        PhieuNhapKho phieuNhapKho = phieuNhapKhoRepository.findById(maPNK)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho"));
        
        String trangThaiCu = phieuNhapKho.getTrangThai();
        phieuNhapKho.setTrangThai(trangThai);
        
        if ("DaNhap".equals(trangThai) && !"DaNhap".equals(trangThaiCu)) {
            List<ChiTietNhapKho> chiTietList = chiTietNhapKhoRepository.findByPhieuNhapKhoMaPNK(maPNK);
            for (ChiTietNhapKho chiTiet : chiTietList) {
                SanPham sanPham = chiTiet.getSanPham();
                sanPham.setSoLuongTon(sanPham.getSoLuongTon() + chiTiet.getSoLuong());
                sanPhamRepository.save(sanPham);
            }
        }
        
        phieuNhapKho = phieuNhapKhoRepository.save(phieuNhapKho);
        return convertToResponse(phieuNhapKho);
    }
    
    @Override
    public PhieuNhapKhoResponse getPhieuNhapById(String maPNK) {
        PhieuNhapKho phieuNhapKho = phieuNhapKhoRepository.findById(maPNK)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho"));
        return convertToResponse(phieuNhapKho);
    }
    
    @Override
    public List<PhieuNhapKhoResponse> getAllPhieuNhap() {
        return phieuNhapKhoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PhieuNhapKhoResponse> getPhieuNhapByDateRange(Date start, Date end) {
        return phieuNhapKhoRepository.findByNgayNhapBetween(start, end).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PhieuNhapKhoResponse> getPhieuNhapByTrangThai(String trangThai) {
        return phieuNhapKhoRepository.findByTrangThai(trangThai).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private PhieuNhapKhoResponse convertToResponse(PhieuNhapKho phieuNhapKho) {
        PhieuNhapKhoResponse response = new PhieuNhapKhoResponse();
        response.setMaPNK(phieuNhapKho.getMaPNK());
        response.setNgayNhap(phieuNhapKho.getNgayNhap());
        response.setMaNV(phieuNhapKho.getMaNV());
        response.setTongTien(phieuNhapKho.getTongTien());
        response.setTrangThai(phieuNhapKho.getTrangThai());
        
        List<ChiTietNhapKho> chiTietList = chiTietNhapKhoRepository.findByPhieuNhapKhoMaPNK(phieuNhapKho.getMaPNK());
        List<PhieuNhapKhoResponse.ChiTietNhapKhoResponse> chiTietResponseList = chiTietList.stream()
                .map(this::convertChiTietToResponse)
                .collect(Collectors.toList());
        response.setChiTiet(chiTietResponseList);
        
        return response;
    }
    
    private PhieuNhapKhoResponse.ChiTietNhapKhoResponse convertChiTietToResponse(ChiTietNhapKho chiTiet) {
        PhieuNhapKhoResponse.ChiTietNhapKhoResponse response = new PhieuNhapKhoResponse.ChiTietNhapKhoResponse();
        response.setMaCTNK(chiTiet.getMaCTNK());
        response.setMaSP(chiTiet.getSanPham().getMaSP());
        response.setTenSP(chiTiet.getSanPham().getTenSP());
        response.setSoLuong(chiTiet.getSoLuong());
        response.setDonGiaNhap(chiTiet.getDonGiaNhap());
        response.setThanhTien(chiTiet.getThanhTien());
        return response;
    }
}