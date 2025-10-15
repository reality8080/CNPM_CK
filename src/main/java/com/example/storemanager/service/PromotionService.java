package com.example.storemanager.service;

import com.example.storemanager.entity.Promotion;
import com.example.storemanager.repository.PromotionRepository;
import com.example.storemanager.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionService {
    private final PromotionRepository repo;
    private final NotificationService ws;

    public PromotionService(PromotionRepository repo, NotificationService ws) {
        this.repo = repo;
        this.ws = ws;
    }

    public List<Promotion> findAll() { return repo.findAll(); }

    public Promotion create(Promotion promo) {
        promo.setActive(false);
        return repo.save(promo);
    }

    public Promotion activate(String id) {
        Promotion p = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
        p.setActive(true);
        repo.save(p);
        ws.sendPromotionNotification("🎉 Khuyến mãi mới được kích hoạt: " + p.getName());
        return p;
    }
}
