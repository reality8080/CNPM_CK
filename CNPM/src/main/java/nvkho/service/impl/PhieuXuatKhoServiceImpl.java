package nvkho.service.impl;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.ChiTietXuatKhoRequest;
import nvkho.dto.request.PhieuXuatKhoRequest;
import nvkho.dto.response.PhieuXuatKhoResponse;
import nvkho.entity.ChiTietXuatKho;
import nvkho.entity.PhieuXuatKho;
import nvkho.entity.SanPham;
import nvkho.exception.BadRequestException;
import nvkho.exception.InsufficientStockException;
import nvkho.exception.ResourceNotFoundException;
import nvkho.repository.ChiTietXuatKhoRepository;
import nvkho.repository.PhieuXuatKhoRepository;
import nvkho.repository.SanPhamRepository;
import nvkho.service.PhieuXuatKhoService;
import nvkho.util.MaPhieuGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhieuXuatKhoServiceImpl implements PhieuXuatKhoService {
    
    private final PhieuXuatKhoRepository phieuXuatKhoRepository;
    private final ChiTietXuatKhoRepository chiTietXuatKhoRepository;
    private final SanPhamRepository sanPhamRepository;
    
    @Override
    @Transactional
    public PhieuXuatKhoResponse createPhieuXuat(PhieuXuatKhoRequest request) {
        if (request.getChiTiet() == null || request.getChiTiet().isEmpty()) {
            throw new BadRequestException("Chi tiết xuất kho không được rỗng");
        }
        
        PhieuXuatKho phieuXuatKho = new PhieuXuatKho();
        phieuXuatKho.setMaPXK(MaPhieuGenerator.generateMaPXK());
        phieuXuatKho.setMaHD(request.getMaHD());
        phieuXuatKho.setMaNV(request.getMaNV());
        phieuXuatKho.setNgayXuat(request.getNgayXuat() != null ? request.getNgayXuat() : new Date());
        phieuXuatKho.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : "DangXuLy");
        
        phieuXuatKho = phieuXuatKhoRepository.save(phieuXuatKho);
        
        for (ChiTietXuatKhoRequest chiTietRequest : request.getChiTiet()) {
            SanPham sanPham = sanPhamRepository.findById(chiTietRequest.getMaSP())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + chiTietRequest.getMaSP()));
            
            if (sanPham.getSoLuongTon() < chiTietRequest.getSoLuong()) {
                throw new InsufficientStockException("Không đủ số lượng tồn kho cho sản phẩm: " + sanPham.getTenSP());
            }
            
            ChiTietXuatKho chiTiet = new ChiTietXuatKho();
            chiTiet.setMaCTXK("CTXK" + System.currentTimeMillis());
            chiTiet.setMaPXK(phieuXuatKho.getMaPXK());
            chiTiet.setPhieuXuatKho(phieuXuatKho);
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(chiTietRequest.getSoLuong());
            
            chiTietXuatKhoRepository.save(chiTiet);
            
            sanPham.setSoLuongTon(sanPham.getSoLuongTon() - chiTietRequest.getSoLuong());
            sanPhamRepository.save(sanPham);
        }
        
        return convertToResponse(phieuXuatKho);
    }
    
    @Override
    @Transactional
    public PhieuXuatKhoResponse updateTrangThai(String maPXK, String trangThai) {
        PhieuXuatKho phieuXuatKho = phieuXuatKhoRepository.findById(maPXK)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất kho"));
        
        String trangThaiCu = phieuXuatKho.getTrangThai();
        phieuXuatKho.setTrangThai(trangThai);
        
        if ("DaXuat".equals(trangThai) && !"DaXuat".equals(trangThaiCu)) {
            List<ChiTietXuatKho> chiTietList = chiTietXuatKhoRepository.findByPhieuXuatKhoMaPXK(maPXK);
            for (ChiTietXuatKho chiTiet : chiTietList) {
                SanPham sanPham = chiTiet.getSanPham();
                if (sanPham != null) {
                    if (sanPham.getSoLuongTon() < chiTiet.getSoLuong()) {
                        throw new InsufficientStockException("Không đủ số lượng tồn kho cho sản phẩm: " + sanPham.getTenSP());
                    }
                    sanPham.setSoLuongTon(sanPham.getSoLuongTon() - chiTiet.getSoLuong());
                    sanPhamRepository.save(sanPham);
                }
            }
        }
        
        phieuXuatKho = phieuXuatKhoRepository.save(phieuXuatKho);
        return convertToResponse(phieuXuatKho);
    }
    
    @Override
    public PhieuXuatKhoResponse getPhieuXuatById(String maPXK) {
        PhieuXuatKho phieuXuatKho = phieuXuatKhoRepository.findById(maPXK)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất kho"));
        return convertToResponse(phieuXuatKho);
    }
    
    @Override
    public List<PhieuXuatKhoResponse> getAllPhieuXuat() {
        return phieuXuatKhoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PhieuXuatKhoResponse> getPhieuXuatByDateRange(Date start, Date end) {
        return phieuXuatKhoRepository.findByNgayXuatBetween(start, end).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PhieuXuatKhoResponse> getPhieuXuatByTrangThai(String trangThai) {
        return phieuXuatKhoRepository.findByTrangThai(trangThai).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private PhieuXuatKhoResponse convertToResponse(PhieuXuatKho phieuXuatKho) {
        PhieuXuatKhoResponse response = new PhieuXuatKhoResponse();
        response.setMaPXK(phieuXuatKho.getMaPXK());
        response.setMaHD(phieuXuatKho.getMaHD());
        response.setMaNV(phieuXuatKho.getMaNV());
        response.setNgayXuat(phieuXuatKho.getNgayXuat());
        response.setTrangThai(phieuXuatKho.getTrangThai());
        
        List<ChiTietXuatKho> chiTietList = chiTietXuatKhoRepository.findByPhieuXuatKhoMaPXK(phieuXuatKho.getMaPXK());
        List<PhieuXuatKhoResponse.ChiTietXuatKhoResponse> chiTietResponseList = chiTietList.stream()
                .map(this::convertChiTietToResponse)
                .collect(Collectors.toList());
        response.setChiTiet(chiTietResponseList);
        
        return response;
    }
    
    private PhieuXuatKhoResponse.ChiTietXuatKhoResponse convertChiTietToResponse(ChiTietXuatKho chiTiet) {
        PhieuXuatKhoResponse.ChiTietXuatKhoResponse response = new PhieuXuatKhoResponse.ChiTietXuatKhoResponse();
        response.setMaCTXK(chiTiet.getMaCTXK());
        if (chiTiet.getSanPham() != null) {
            response.setMaSP(chiTiet.getSanPham().getMaSP());
            response.setTenSP(chiTiet.getSanPham().getTenSP());
        }
        response.setSoLuong(chiTiet.getSoLuong());
        return response;
    }
}