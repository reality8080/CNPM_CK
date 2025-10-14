package vn.iotstar.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.iotstar.entity.KhuyenMai;
import vn.iotstar.entity.SanPham;
import vn.iotstar.service.KhuyenMaiService;

@RestController
@RequestMapping("/api/promotions")
public class KhuyenMaiController {

    @Autowired
    private KhuyenMaiService service;

    @GetMapping
    public List<KhuyenMai> getAll() {
        return service.getAll();
    }

    @GetMapping("/search")
    public List<KhuyenMai> search(@RequestParam(required = false) String keyword) {
        return service.searchByMaGiamGia(keyword);
    }

    @PostMapping
    public ResponseEntity<KhuyenMai> add(@Valid @RequestBody KhuyenMai khuyenMai) {
        KhuyenMai saved = service.save(khuyenMai);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{maKM}")
    public ResponseEntity<KhuyenMai> getById(@PathVariable String maKM) {
        Optional<KhuyenMai> khuyenMai = service.getById(maKM);
        return khuyenMai.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{maKM}")
    public ResponseEntity<KhuyenMai> update(@PathVariable String maKM, @Valid @RequestBody KhuyenMai khuyenMai) {
        Optional<KhuyenMai> existing = service.getById(maKM);
        if (existing.isPresent()) {
            khuyenMai.setMaKM(maKM);
            KhuyenMai updated = service.save(khuyenMai);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{maKM}")
    public ResponseEntity<Void> delete(@PathVariable String maKM) {
        if (service.getById(maKM).isPresent()) {
            service.deleteById(maKM);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{maKM}/products")
    public ResponseEntity<List<SanPham>> getProductsByPromotion(@PathVariable String maKM) {
        Optional<KhuyenMai> khuyenMai = service.getById(maKM);
        if (khuyenMai.isPresent()) {
            List<SanPham> products = service.getProductsByMaSPList(khuyenMai.get().getMaSP());
            return ResponseEntity.ok(products);
        }
        return ResponseEntity.notFound().build();
    }
}