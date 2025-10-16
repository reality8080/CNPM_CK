package nvkho.service.impl;

import nvkho.dto.request.KiemKeRequest;
import nvkho.dto.response.KiemKeResponse;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KiemKeServiceImpl implements KiemKeService {

    private final KiemKeRepository kiemKeRepository;
    private final SanPhamRepository sanPhamRepository;

    @Override
    @Transactional
    public KiemKeResponse createKiemKe(KiemKeRequest request) {
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
        kiemKe = kiemKeRepository.save(kiemKe);
        return convertToResponse(kiemKe);
    }

    @Override
    @Transactional
    public KiemKeResponse xacNhanKiemKe(String maKiemKe) {
        KiemKe kiemKe = kiemKeRepository.findById(maKiemKe)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu kiểm kê"));

        kiemKe.setTrangThai("DaXacNhan");

        for (KiemKe.ChiTietKiemKe chiTiet : kiemKe.getChiTietKiemKe()) {
            SanPham sanPham = chiTiet.getSanPham();
            if (sanPham != null) {
                sanPham.setSoLuongTon(chiTiet.getSoLuongThucTe());
                sanPhamRepository.save(sanPham);
            }
        }

        kiemKe = kiemKeRepository.save(kiemKe);
        return convertToResponse(kiemKe);
    }

    @Override
    public KiemKeResponse getKiemKeById(String maKiemKe) {
        KiemKe kiemKe = kiemKeRepository.findById(maKiemKe)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu kiểm kê"));
        return convertToResponse(kiemKe);
    }

    @Override
    public List<KiemKeResponse> getAllKiemKe() {
        return kiemKeRepository.findAll().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    private KiemKeResponse convertToResponse(KiemKe kiemKe) {
        KiemKeResponse response = new KiemKeResponse();
        response.setMaKiemKe(kiemKe.getMaKiemKe());
        response.setNgayKiemKe(kiemKe.getNgayKiemKe());
        response.setMaNV(kiemKe.getMaNV());
        response.setTrangThai(kiemKe.getTrangThai());
        response.setGhiChu(kiemKe.getGhiChu());

        List<KiemKeResponse.ChiTietKiemKeResponse> chiTietResponseList = kiemKe.getChiTietKiemKe().stream()
            .map(this::convertChiTietToResponse)
            .collect(Collectors.toList());
        response.setChiTietKiemKe(chiTietResponseList);

        return response;
    }

    private KiemKeResponse.ChiTietKiemKeResponse convertChiTietToResponse(KiemKe.ChiTietKiemKe chiTiet) {
        KiemKeResponse.ChiTietKiemKeResponse response = new KiemKeResponse.ChiTietKiemKeResponse();
        if (chiTiet.getSanPham() != null) {
            response.setMaSP(chiTiet.getSanPham().getMaSP());
            response.setTenSP(chiTiet.getSanPham().getTenSP());
        }
        response.setSoLuongHeThong(chiTiet.getSoLuongHeThong());
        response.setSoLuongThucTe(chiTiet.getSoLuongThucTe());
        response.setChenhLech(chiTiet.getChenhLech());
        response.setGhiChu(chiTiet.getGhiChu());
        return response;
    }
}