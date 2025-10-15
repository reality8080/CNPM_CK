package com.example.storemanager.config;

import com.example.storemanager.entity.*;
import com.example.storemanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureIndexes();
        seedUsers();
        seedCatalog();
        seedPromotion();
    }

    private void ensureIndexes() {
        mongoTemplate.indexOps("users").ensureIndex(new Index().on("username", org.springframework.data.domain.Sort.Direction.ASC).unique());
        mongoTemplate.indexOps("products").ensureIndex(new Index().on("name", org.springframework.data.domain.Sort.Direction.ASC));
        mongoTemplate.indexOps("products").ensureIndex(new Index().on("categoryId", org.springframework.data.domain.Sort.Direction.ASC));
        mongoTemplate.indexOps("invoices").ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.ASC));
        mongoTemplate.indexOps("promotions").ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.ASC));
        mongoTemplate.indexOps("promotions").ensureIndex(new Index().on("startDate", org.springframework.data.domain.Sort.Direction.ASC));
        mongoTemplate.indexOps("promotions").ensureIndex(new Index().on("endDate", org.springframework.data.domain.Sort.Direction.ASC));
    }

    private void seedUsers() {
        if (userRepository.findByUsername("manager").isEmpty()) {
            User manager = User.builder()
                    .username("manager")
                    .password(passwordEncoder.encode("123456"))
                    .role("MANAGER")
                    .fullName("Quản lý")
                    .build();
            userRepository.save(manager);
        }
        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = User.builder()
                    .username("staff")
                    .password(passwordEncoder.encode("123456"))
                    .role("STAFF")
                    .fullName("Nhân viên bán hàng")
                    .build();
            userRepository.save(staff);
        }
    }

    private void seedCatalog() {
        // Nếu bảng products trống, bổ sung dữ liệu mẫu; đảm bảo có categories để tham chiếu
        if (productRepository.count() == 0) {
            Category ao;
            Category quan;

            if (categoryRepository.count() == 0) {
                ao = categoryRepository.save(Category.builder().name("Áo").description("Danh mục áo").build());
                quan = categoryRepository.save(Category.builder().name("Quần").description("Danh mục quần").build());
            } else {
                List<Category> cats = categoryRepository.findAll();
                // Chọn 2 category đầu tiên hoặc tạo mới nếu không đủ
                ao = cats.size() > 0 ? cats.get(0) : categoryRepository.save(Category.builder().name("Áo").description("Danh mục áo").build());
                quan = cats.size() > 1 ? cats.get(1) : categoryRepository.save(Category.builder().name("Quần").description("Danh mục quần").build());
            }

            productRepository.save(Product.builder()
                    .name("Áo thun cổ tròn")
                    .description("Chất cotton")
                    .quantity(100)
                    .categoryId(ao.getId())
                    .price(new BigDecimal("199000"))
                    .images(List.of())
                    .build());

            productRepository.save(Product.builder()
                    .name("Quần jeans slim fit")
                    .description("Co giãn nhẹ")
                    .quantity(50)
                    .categoryId(quan.getId())
                    .price(new BigDecimal("399000"))
                    .images(List.of())
                    .build());
        }
    }

    private void seedPromotion() {
        boolean hasPromo = promotionRepository.count() > 0;
        if (!hasPromo) {
            promotionRepository.save(Promotion.builder()
                    .name("Giảm 10% đơn hàng đầu tiên")
                    .type("PERCENT")
                    .value(new BigDecimal("10"))
                    .startDate(Instant.now())
                    .endDate(Instant.now().plusSeconds(30L * 24 * 3600))
                    .minOrderValue(new BigDecimal("200000"))
                    .maxUsage(1)
                    .active(false)
                    .priority(1)
                    .build());
        }
    }
}
