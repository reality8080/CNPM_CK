package nvkho.service.impl;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.DoiTraHangRequest;
import nvkho.dto.response.DoiTraHangResponse;
import nvkho.entity.DoiTraHang;
import nvkho.entity.SanPham;
import nvkho.exception.ResourceNotFoundException;
import nvkho.repository.DoiTraHangRepository;
import nvkho.repository.SanPhamRepository;
import nvkho.service.DoiTraHangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoiTraHangServiceImpl implements DoiTraHangService {
    
    private final DoiTraHangRepository doiTraHangRepository;
    private final SanPhamRepository sanPhamRepository;
    
    @Override
    @Transactional
    public DoiTraHangResponse createDoiTra(DoiTraHangRequest request) {
    	SanPham sanPham = sanPhamRepository.findById(request.getMaSP())
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    	    
    	    DoiTraHang doiTraHang = new DoiTraHang();
    	    doiTraHang.setMaDoiTra("DT" + System.currentTimeMillis());
    	    doiTraHang.setSanPham(sanPham);
    	    doiTraHang.setSoLuong(request.getSoLuong());
    	    doiTraHang.setLyDo(request.getLyDo());
    	    doiTraHang.setMaHD(request.getMaHD());
    	    doiTraHang.setNgayDoiTra(request.getNgayDoiTra() != null ? request.getNgayDoiTra() : new Date());
    	    doiTraHang.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : "ChuaXuLy");
    	    
    	    doiTraHang = doiTraHangRepository.save(doiTraHang);
    	    return convertToResponse(doiTraHang);
    }
    
    @Override
    @Transactional
    public DoiTraHangResponse xuLyDoiTra(String maDoiTra) {
    	DoiTraHang doiTraHang = doiTraHangRepository.findById(maDoiTra)
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đổi trả"));
    	    
    	    SanPham sanPham = doiTraHang.getSanPham();
    	    sanPham.setSoLuongTon(sanPham.getSoLuongTon() + doiTraHang.getSoLuong());
    	    sanPhamRepository.save(sanPham);
    	    
    	    doiTraHang.setTrangThai("DaXuLy");
    	    doiTraHang = doiTraHangRepository.save(doiTraHang);
    	    
    	    return convertToResponse(doiTraHang);
    }
    
    @Override
    public DoiTraHangResponse getDoiTraById(String maDoiTra) {
    	DoiTraHang doiTraHang = doiTraHangRepository.findById(maDoiTra)
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đổi trả"));
    	    return convertToResponse(doiTraHang);
    }
    
    @Override
    public List<DoiTraHangResponse> getAllDoiTra() {
        return doiTraHangRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private DoiTraHangResponse convertToResponse(DoiTraHang doiTraHang) {
        DoiTraHangResponse response = new DoiTraHangResponse();
        response.setMaDoiTra(doiTraHang.getMaDoiTra());
        response.setMaHD(doiTraHang.getMaHD());
        
        if (doiTraHang.getSanPham() != null) {
            response.setMaSP(doiTraHang.getSanPham().getMaSP());
            response.setTenSP(doiTraHang.getSanPham().getTenSP());
        }
        
        response.setSoLuong(doiTraHang.getSoLuong());
        response.setLyDo(doiTraHang.getLyDo());
        response.setNgayDoiTra(doiTraHang.getNgayDoiTra());
        response.setTrangThai(doiTraHang.getTrangThai());
        
        return response;
    }
    @Override
    @Transactional
    public DoiTraHangResponse updateDoiTra(String maDoiTra, DoiTraHangRequest request) {
    	DoiTraHang doiTraHang = doiTraHangRepository.findById(maDoiTra)
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đổi trả"));
    	    
    	    SanPham sanPham = sanPhamRepository.findById(request.getMaSP())
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    	    
    	    doiTraHang.setSanPham(sanPham);
    	    doiTraHang.setSoLuong(request.getSoLuong());
    	    doiTraHang.setLyDo(request.getLyDo());
    	    doiTraHang.setMaHD(request.getMaHD());
    	    
    	    if (request.getTrangThai() != null) {
    	        doiTraHang.setTrangThai(request.getTrangThai());
    	    }
    	    
    	    if (request.getNgayDoiTra() != null) {
    	        doiTraHang.setNgayDoiTra(request.getNgayDoiTra());
    	    }
    	    
    	    doiTraHang = doiTraHangRepository.save(doiTraHang);
    	    return convertToResponse(doiTraHang);
    }

    @Override
    @Transactional
    public void deleteDoiTra(String maDoiTra) {
        DoiTraHang doiTraHang = doiTraHangRepository.findById(maDoiTra)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đổi trả"));
        doiTraHangRepository.delete(doiTraHang);
    }
}