package nvkho.service.impl;

import lombok.RequiredArgsConstructor;
import nvkho.dto.response.TonKhoResponse;
import nvkho.entity.SanPham;
import nvkho.exception.ResourceNotFoundException;
import nvkho.repository.SanPhamRepository;
import nvkho.service.TonKhoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TonKhoServiceImpl implements TonKhoService {
    
    private final SanPhamRepository sanPhamRepository;
    
    @Override
    public List<TonKhoResponse> getAllTonKho() {
        return sanPhamRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public TonKhoResponse getTonKhoBySanPham(String maSP) {
        SanPham sanPham = sanPhamRepository.findById(maSP)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        return convertToResponse(sanPham);
    }
    
    @Override
    public List<TonKhoResponse> getSanPhamSapHetHang(Integer nguongCanhBao) {
        return sanPhamRepository.findBySoLuongTonLessThan(nguongCanhBao).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private TonKhoResponse convertToResponse(SanPham sanPham) {
        TonKhoResponse response = new TonKhoResponse();
        response.setMaSP(sanPham.getMaSP());
        response.setTenSP(sanPham.getTenSP());
        response.setKichThuoc(sanPham.getKichThuoc());
        response.setMauSac(sanPham.getMauSac());
        response.setSoLuongTon(sanPham.getSoLuongTon());
        response.setTenDanhMuc(sanPham.getDanhMuc().getTenDM());
        response.setHinhAnh(sanPham.getHinhAnh());
        return response;
    }
}
