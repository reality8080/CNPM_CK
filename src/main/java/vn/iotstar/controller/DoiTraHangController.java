package vn.iotstar.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.entity.DoiTraHang;
import vn.iotstar.service.DoiTraHangService;

import java.util.List;

@RestController
@RequestMapping("/api/doitra")
public class DoiTraHangController {
    @Autowired
    private DoiTraHangService service;

    @PostMapping
    public ResponseEntity<?> createDoiTra(@Valid @RequestBody DoiTraHang doiTra) {
        try {
            DoiTraHang savedDoiTra = service.saveDoiTra(doiTra);
            return ResponseEntity.ok(savedDoiTra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{maHD}")
    public ResponseEntity<List<DoiTraHang>> getDoiTraByHoaDon(@PathVariable String maHD) {
        try {
            List<DoiTraHang> doiTraList = service.findByMaHD(maHD);
            return ResponseEntity.ok(doiTraList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}