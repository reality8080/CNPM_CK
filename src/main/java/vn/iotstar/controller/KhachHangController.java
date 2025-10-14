package vn.iotstar.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.entity.KhachHang;
import vn.iotstar.service.KhachHangService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangService KhachHangservice;

    @GetMapping
    public List<KhachHang> getAllCustomers() {
        return KhachHangservice.getAllCustomers();
    }

    @GetMapping("/search")
    public List<KhachHang> searchCustomers(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return KhachHangservice.getAllCustomers();
        }
        return KhachHangservice.searchCustomers(keyword);
    }

    @GetMapping("/{maKH}")
    public ResponseEntity<KhachHang> getCustomerById(@PathVariable String maKH) {
        Optional<KhachHang> customer = KhachHangservice.getCustomerById(maKH);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<KhachHang> addCustomer(@Valid @RequestBody KhachHang khachHang) {
        try {
            KhachHang savedCustomer = KhachHangservice.saveCustomer(khachHang);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{maKH}")
    public ResponseEntity<KhachHang> updateCustomer(@PathVariable String maKH, @Valid @RequestBody KhachHang khachHang) {
        Optional<KhachHang> existingCustomer = KhachHangservice.getCustomerById(maKH);
        if (existingCustomer.isPresent()) {
            khachHang.setMaKH(maKH);
            return ResponseEntity.ok(KhachHangservice.saveCustomer(khachHang));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{maKH}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String maKH) {
        if (KhachHangservice.getCustomerById(maKH).isPresent()) {
            KhachHangservice.deleteCustomer(maKH);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}