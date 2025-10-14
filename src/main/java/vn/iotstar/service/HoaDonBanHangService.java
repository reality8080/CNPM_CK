package vn.iotstar.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.iotstar.DTO.HoaDonDTO;
import vn.iotstar.DTO.ChiTietHoaDonDTO;
import vn.iotstar.DTO.ReportDTO;

import vn.iotstar.entity.*;

import vn.iotstar.repository.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HoaDonBanHangService {
    @Autowired
    private HoaDonBanHangRepository hoaDonRepository;
    @Autowired
    private ChiTietHoaDonRepository chiTietRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private DoiTraHangRepository doiTraRepository;

    public HoaDonBanHang saveHoaDon(HoaDonBanHang hoaDon, List<ChiTietHoaDon> chiTietList) {
        HoaDonBanHang savedHoaDon = hoaDonRepository.save(hoaDon);
        double tongTien = 0;
        for (ChiTietHoaDon ct : chiTietList) {
            ct.setMaHD(savedHoaDon.getMaHD());
            SanPham sp = sanPhamRepository.findById(ct.getMaSP())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            if (sp.getSoLuongTon() < ct.getSoLuong()) {
                throw new RuntimeException("Số lượng tồn kho không đủ");
            }
            sp.setSoLuongTon(sp.getSoLuongTon() - ct.getSoLuong());
            sanPhamRepository.save(sp);
            ct.setThanhTien(ct.getSoLuong() * ct.getDonGia() * (1 - ct.getChietKhau() / 100));
            chiTietRepository.save(ct);
            tongTien += ct.getThanhTien();
        }
        savedHoaDon.setTongTien(tongTien);
        return hoaDonRepository.save(savedHoaDon);
    }

    public Page<HoaDonDTO> getAllHoaDon(Pageable pageable, String startDate, String endDate, String trangThai) {
        try {
            Page<HoaDonBanHang> hoaDons;
            if (startDate != null && endDate != null) {
                Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                if (trangThai != null && !trangThai.isEmpty()) {
                    hoaDons = hoaDonRepository.findByNgayBanBetweenAndTrangThai(start, end, trangThai, pageable);
                } else {
                    hoaDons = hoaDonRepository.findByNgayBanBetween(start, end, pageable);
                }
            } else if (trangThai != null && !trangThai.isEmpty()) {
                hoaDons = hoaDonRepository.findByTrangThai(trangThai, pageable);
            } else {
                hoaDons = hoaDonRepository.findAll(pageable);
            }
            return hoaDons.map(this::mapToDTO);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
        }
    }

    public HoaDonDTO getHoaDonById(String maHD) {
        HoaDonBanHang hoaDon = hoaDonRepository.findById(maHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        return mapToDTO(hoaDon);
    }

    private HoaDonDTO mapToDTO(HoaDonBanHang hoaDon) {
        HoaDonDTO dto = new HoaDonDTO();
        dto.setMaHD(hoaDon.getMaHD());
        dto.setMaNV(hoaDon.getMaNV());
        dto.setMaKH(hoaDon.getMaKH());
        KhachHang khachHang = khachHangRepository.findById(hoaDon.getMaKH())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        dto.setHoTenKhachHang(khachHang.getHoTen());
        dto.setNgayBan(hoaDon.getNgayBan());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setTrangThai(hoaDon.getTrangThai());
        dto.setPhuongThucThanhToan(hoaDon.getPhuongThucThanhToan());
        List<ChiTietHoaDon> chiTietList = chiTietRepository.findByMaHD(hoaDon.getMaHD());
        dto.setChiTiet(chiTietList.stream().map(ct -> {
            ChiTietHoaDonDTO chiTietDTO = new ChiTietHoaDonDTO();
            chiTietDTO.setMaSP(ct.getMaSP());
            SanPham sp = sanPhamRepository.findById(ct.getMaSP())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            chiTietDTO.setTenSP(sp.getTenSP());
            chiTietDTO.setSoLuong(ct.getSoLuong());
            chiTietDTO.setDonGia(ct.getDonGia());
            chiTietDTO.setChietKhau(ct.getChietKhau());
            chiTietDTO.setThanhTien(ct.getThanhTien());
            return chiTietDTO;
        }).collect(Collectors.toList()));
        return dto;
    }
    public List<ReportDTO> generateSalesReport(String startDate, String endDate) {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);

            // Lấy danh sách hóa đơn
            List<HoaDonBanHang> hoaDons = hoaDonRepository.findByNgayBanBetween(start, end);
            // Lấy danh sách đổi trả
            List<DoiTraHang> doiTras = doiTraRepository.findByNgayDoiTraBetween(start, end);

            // Nhóm hóa đơn theo ngày
            Map<Date, List<HoaDonBanHang>> hoaDonByDate = hoaDons.stream()
                    .collect(Collectors.groupingBy(hd -> {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(hd.getNgayBan());
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        return cal.getTime();
                    }));

            // Nhóm đổi trả theo ngày
            Map<Date, List<DoiTraHang>> doiTraByDate = doiTras.stream()
                    .collect(Collectors.groupingBy(dt -> {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dt.getNgayDoiTra());
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        return cal.getTime();
                    }));

            // Tạo báo cáo
            List<ReportDTO> reports = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            while (!cal.getTime().after(end)) {
                Date date = cal.getTime();
                ReportDTO report = new ReportDTO();
                report.setDate(date);

                // Tính doanh thu và số lượng bán
                List<HoaDonBanHang> dailyHoaDons = hoaDonByDate.getOrDefault(date, new ArrayList<>());
                double revenue = dailyHoaDons.stream()
                        .mapToDouble(HoaDonBanHang::getTongTien)
                        .sum();
                int quantitySold = dailyHoaDons.stream()
                        .flatMap(hd -> chiTietRepository.findByMaHD(hd.getMaHD()).stream())
                        .mapToInt(ChiTietHoaDon::getSoLuong)
                        .sum();
                report.setRevenue(revenue);
                report.setQuantitySold(quantitySold);

                // Tính số lượng đổi trả
                List<DoiTraHang> dailyDoiTras = doiTraByDate.getOrDefault(date, new ArrayList<>());
                int quantityReturned = dailyDoiTras.stream()
                        .mapToInt(DoiTraHang::getSoLuong)
                        .sum();
                report.setQuantityReturned(quantityReturned);

                reports.add(report);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            return reports;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo báo cáo bán hàng: " + e.getMessage());
        }
    }
}