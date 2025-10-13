package hcmute.edu.vn.web.Service.implement.Product;

import hcmute.edu.vn.web.Entity.Product.AppDiscount;
import hcmute.edu.vn.web.Repository.Product.AppDiscountRepository;
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
public class AppDiscountService {
    private final AppDiscountRepository appDiscountRepository;

    public AppDiscount createAppDiscount(AppDiscount appDiscount) {
        return appDiscountRepository.save(appDiscount);
    }

    public Optional<AppDiscount> getAppDiscountById(String discountId) {
        return appDiscountRepository.findById(discountId);
    }

    public List<AppDiscount> getAllAppDiscounts() {
        return appDiscountRepository.findAll();
    }

    public List<AppDiscount> getAppDiscountsByShop(String shopId) {
        return appDiscountRepository.findByShopId(shopId);
    }

    @Transactional
    public AppDiscount updateAppDiscount(String discountId, AppDiscount updatedAppDiscount) {
        Optional<AppDiscount> existingAppDiscount = appDiscountRepository.findById(discountId);
        if (existingAppDiscount.isPresent()) {
            updatedAppDiscount.setDiscountId(discountId);
            return appDiscountRepository.save(updatedAppDiscount);
        } else {
            throw new RuntimeException("AppDiscount not found with ID: " + discountId);
        }
    }

    @Transactional
    public void deleteAppDiscount(String discountId) {
        if (appDiscountRepository.existsById(discountId)) {
            appDiscountRepository.deleteById(discountId);
        } else {
            throw new RuntimeException("AppDiscount not found with ID: " + discountId);
        }
    }

    @Transactional
    public List<AppDiscount> seedAppDiscounts(int count) {
        List<AppDiscount> discounts = new ArrayList<>();
        String[] shopIds = {"SHOP001", "SHOP002", "SHOP003", "SHOP004", "SHOP005"}; // ID cửa hàng giả định

        for (int i = 1; i <= count; i++) {
            LocalDateTime startDate = LocalDateTime.now().plusDays(i % 3 == 0 ? 0 : -3);
            LocalDateTime endDate = startDate.plusDays(15);
            double discountPercentage = ThreadLocalRandom.current().nextDouble(10, 40);

            discounts.add(AppDiscount.builder()
                    .discountId(UUID.randomUUID().toString())
                    .shopId(shopIds[i % shopIds.length])
                    .discountPercentage(Math.round(discountPercentage * 10.0) / 10.0)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(i % 4 == 0 ? "Inactive" : "Active")
                    .build());
        }
        return appDiscountRepository.saveAll(discounts);
    }

    // Bổ sung phương thức saveAll cho đồng bộ (có thể đã có trong repository)
    public List<AppDiscount> saveAll(List<AppDiscount> discounts) {
        return appDiscountRepository.saveAll(discounts);
    }

}
