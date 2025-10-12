package nvkho.service.impl;

import lombok.RequiredArgsConstructor;
import nvkho.dto.request.SanPhamRequest;
import nvkho.dto.response.SanPhamResponse;
import nvkho.entity.DanhMuc;
import nvkho.entity.SanPham;
import nvkho.exception.ResourceNotFoundException;
import nvkho.repository.DanhMucRepository;
import nvkho.repository.SanPhamRepository;
import nvkho.service.FileUploadService;
import nvkho.service.SanPhamService;
import nvkho.util.MaPhieuGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {
    
    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final FileUploadService fileUploadService;
    
    @Override
    @Transactional
    public SanPhamResponse createSanPham(SanPhamRequest request, MultipartFile file) {
    	DanhMuc danhMuc = danhMucRepository.findById(request.getMaDM())
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
    	    
    	    SanPham sanPham = new SanPham();
    	    sanPham.setMaSP(MaPhieuGenerator.generateMaSP());
    	    sanPham.setTenSP(request.getTenSP());
    	    sanPham.setGiaBan(request.getGiaBan());
    	    sanPham.setSoLuongTon(0);
    	    sanPham.setKichThuoc(request.getKichThuoc());
    	    sanPham.setMauSac(request.getMauSac());
    	    sanPham.setChatLieu(request.getChatLieu());
    	    sanPham.setDanhMuc(danhMuc);
    	    sanPham.setMoTa(request.getMoTa());
    	    
    	    if (file != null && !file.isEmpty()) {
    	        String hinhAnh = fileUploadService.uploadImage(file);
    	        sanPham.setHinhAnh(hinhAnh);
    	    }
    	    
    	    sanPham = sanPhamRepository.save(sanPham);
    	    return convertToResponse(sanPham);
    }
    
    @Override
    @Transactional
    public SanPhamResponse updateSanPham(String maSP, SanPhamRequest request, MultipartFile file) {
    	SanPham sanPham = sanPhamRepository.findById(maSP)
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    	    
    	    DanhMuc danhMuc = danhMucRepository.findById(request.getMaDM())
    	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
    	    
    	    sanPham.setTenSP(request.getTenSP());
    	    sanPham.setGiaBan(request.getGiaBan());
    	    sanPham.setKichThuoc(request.getKichThuoc());
    	    sanPham.setMauSac(request.getMauSac());
    	    sanPham.setChatLieu(request.getChatLieu());
    	    sanPham.setDanhMuc(danhMuc);
    	    sanPham.setMoTa(request.getMoTa());
    	    
    	    if (file != null && !file.isEmpty()) {
    	        if (sanPham.getHinhAnh() != null && !sanPham.getHinhAnh().isEmpty()) {
    	            fileUploadService.deleteImage(sanPham.getHinhAnh());
    	        }
    	        String hinhAnh = fileUploadService.uploadImage(file);
    	        sanPham.setHinhAnh(hinhAnh);
    	    }
    	    
    	    sanPham = sanPhamRepository.save(sanPham);
    	    return convertToResponse(sanPham);
    }
    
    @Override
    @Transactional
    public void deleteSanPham(String maSP) {
        SanPham sanPham = sanPhamRepository.findById(maSP)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        if (sanPham.getHinhAnh() != null && !sanPham.getHinhAnh().isEmpty()) {
            fileUploadService.deleteImage(sanPham.getHinhAnh());
        }
        sanPhamRepository.delete(sanPham);
    }
    
    @Override
    public SanPhamResponse getSanPhamById(String maSP) {
        SanPham sanPham = sanPhamRepository.findById(maSP)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        return convertToResponse(sanPham);
    }
    
    @Override
    public List<SanPhamResponse> getAllSanPham() {
        return sanPhamRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SanPhamResponse> searchSanPham(String keyword) {
        return sanPhamRepository.findByTenSPContainingIgnoreCase(keyword).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SanPhamResponse> getSanPhamByDanhMuc(String maDM) {
        return sanPhamRepository.findByDanhMucMaDM(maDM).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private SanPhamResponse convertToResponse(SanPham sanPham) {
    	SanPhamResponse response = new SanPhamResponse();
        response.setMaSP(sanPham.getMaSP());
        response.setTenSP(sanPham.getTenSP());
        response.setGiaBan(sanPham.getGiaBan());
        response.setSoLuongTon(sanPham.getSoLuongTon());
        response.setKichThuoc(sanPham.getKichThuoc());
        response.setMauSac(sanPham.getMauSac());
        response.setChatLieu(sanPham.getChatLieu());
        response.setMaDM(sanPham.getDanhMuc().getMaDM());
        response.setTenDanhMuc(sanPham.getDanhMuc().getTenDM());
        response.setMoTa(sanPham.getMoTa());
        response.setHinhAnh(sanPham.getHinhAnh());
        return response;
    }
}