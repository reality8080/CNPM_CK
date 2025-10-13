package hcmute.edu.vn.web.Service.implement.Product;

import hcmute.edu.vn.web.Entity.Product.Product;
import hcmute.edu.vn.web.Entity.Product.PromotionProgram;
import hcmute.edu.vn.web.Repository.Product.PromotionProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PromotionProgramService {
    private final PromotionProgramRepository promotionProgramRepository;

    public PromotionProgram createPromotionProgram(PromotionProgram promotionProgram) {
        return promotionProgramRepository.save(promotionProgram);
    }

    public Optional<PromotionProgram> getPromotionProgramById(String promotionId) {
        return promotionProgramRepository.findById(promotionId);
    }

    public List<PromotionProgram> getAllPromotionPrograms() {
        return promotionProgramRepository.findAll();
    }

    public List<PromotionProgram> getPromotionProgramsByType(String promotionType) {
        return promotionProgramRepository.findByPromotionType(promotionType);
    }

    @Transactional
    public PromotionProgram updatePromotionProgram(String promotionId, PromotionProgram updatedPromotionProgram) {
        Optional<PromotionProgram> existingPromotionProgram = promotionProgramRepository.findById(promotionId);
        if (existingPromotionProgram.isPresent()) {
            updatedPromotionProgram.setPromotionId(promotionId);
            return promotionProgramRepository.save(updatedPromotionProgram);
        } else {
            throw new RuntimeException("PromotionProgram not found with ID: " + promotionId);
        }
    }

    @Transactional
    public void deletePromotionProgram(String promotionId) {
        if (promotionProgramRepository.existsById(promotionId)) {
            promotionProgramRepository.deleteById(promotionId);
        } else {
            throw new RuntimeException("PromotionProgram not found with ID: " + promotionId);
        }
    }

    @Transactional
    public List<PromotionProgram> seedPromotionPrograms(int count, List<Product> allProducts) {
        if (allProducts.isEmpty()) {
            throw new IllegalArgumentException("Không thể tạo PromotionProgram khi thiếu Product.");
        }

        // ... logic tạo 20 PromotionProgram ...
        List<PromotionProgram> programs = new ArrayList<>();
        String[] promotionTypes = {"Percentage", "FixedAmount", "BuyXGetY"};

        for (int i = 1; i <= count; i++) {
            LocalDateTime startDate = LocalDateTime.now().plusDays(i % 2 == 0 ? 1 : -7);
            LocalDateTime endDate = startDate.plusDays(7);
            String type = promotionTypes[i % promotionTypes.length];
            Double discountValue = type.equals("Percentage") ? ThreadLocalRandom.current().nextDouble(5, 50) : 50000.0 * (i % 3 + 1);

            programs.add(PromotionProgram.builder()
                    .promotionId(UUID.randomUUID().toString())
                    .promotionName("Khuyến Mãi Lớn Tháng " + (i % 12 + 1) + " (Type: " + type + ")")
                    .promotionType(type)
                    .discountValue(Math.round(discountValue * 100.0) / 100.0)
                    .startDate(startDate)
                    .endDate(endDate)
                    .applyCondition(type.equals("BuyXGetY") ? "Mua 2 Tặng 1" : "Đơn hàng từ " + (i * 100000) + "đ")
                    .status(i % 3 == 0 ? "Expired" : "Active")
                    .build());
        }

        return promotionProgramRepository.saveAll(programs);
    }

    // Bổ sung phương thức saveAll cho đồng bộ
    public List<PromotionProgram> saveAll(List<PromotionProgram> programs) {
        return promotionProgramRepository.saveAll(programs);
    }

}