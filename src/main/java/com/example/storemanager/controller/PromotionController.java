package com.example.storemanager.controller;

import com.example.storemanager.entity.Promotion;
import com.example.storemanager.repository.PromotionRepository;
import com.example.storemanager.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/promotions")
public class PromotionController {
    private final PromotionRepository promoRepo;
    private final NotificationService notifier;

    public PromotionController(PromotionRepository promoRepo, NotificationService notifier) {
        this.promoRepo = promoRepo;
        this.notifier = notifier;
    }

    @GetMapping
    public List<Promotion> getAll() {
        return promoRepo.findAll();
    }

    @PostMapping
    public Promotion add(@RequestBody Promotion promo) {
        promo.setActive(false);
        return promoRepo.save(promo);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable String id) {
        return promoRepo.findById(id).map(p -> {
            p.setActive(true);
            promoRepo.save(p);
            notifier.sendPromotionNotification("🎉 Kích hoạt khuyến mãi mới: " + p.getName());
            return ResponseEntity.ok(p);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    
}
