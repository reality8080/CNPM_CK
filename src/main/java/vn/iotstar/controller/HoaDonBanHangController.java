package vn.iotstar.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.iotstar.DTO.HoaDonDTO;
import vn.iotstar.DTO.ReportDTO;
import vn.iotstar.entity.HoaDonBanHang;
import vn.iotstar.entity.ChiTietHoaDon;
import vn.iotstar.service.HoaDonBanHangService;

import java.util.List;

@RestController
@RequestMapping("/api/hoadon")
public class HoaDonBanHangController {
    @Autowired
    private HoaDonBanHangService service;

    @PostMapping
    public ResponseEntity<?> createHoaDon(@Valid @RequestBody HoaDonRequestDTO request) {
        try {
            HoaDonBanHang savedHoaDon = service.saveHoaDon(request.getHoaDon(), request.getChiTietList());
            return ResponseEntity.ok(savedHoaDon);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<HoaDonDTO>> getAllHoaDon(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String trangThai) {
        try {
            Page<HoaDonDTO> hoaDons = service.getAllHoaDon(pageable, startDate, endDate, trangThai);
            return ResponseEntity.ok(hoaDons);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{maHD}")
    public ResponseEntity<HoaDonDTO> getHoaDonById(@PathVariable String maHD) {
        try {
            HoaDonDTO hoaDon = service.getHoaDonById(maHD);
            return ResponseEntity.ok(hoaDon);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/report")
    public ResponseEntity<List<ReportDTO>> getSalesReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<ReportDTO> reports = service.generateSalesReport(startDate, endDate);
            return ResponseEntity.ok(reports);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

class HoaDonRequestDTO {
    private HoaDonBanHang hoaDon;
    private List<ChiTietHoaDon> chiTietList;

    public HoaDonBanHang getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDonBanHang hoaDon) {
        this.hoaDon = hoaDon;
    }

    public List<ChiTietHoaDon> getChiTietList() {
        return chiTietList;
    }

    public void setChiTietList(List<ChiTietHoaDon> chiTietList) {
        this.chiTietList = chiTietList;
    }
}