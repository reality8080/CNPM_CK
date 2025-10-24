package com.example.spring_boot.services.products;


import com.example.spring_boot.domains.products.ProductReview;
import com.example.spring_boot.domains.products.Product;
import com.example.spring_boot.repository.products.ProductReviewRepository;
import com.example.spring_boot.repository.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ProductReview createReview(String productId, String name, String email, Integer rating, String comment) {
        // Validate
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Đánh giá phải từ 1 đến 5 sao");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung đánh giá không được để trống");
        }

        // Kiểm tra sản phẩm tồn tại
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        ProductReview review = ProductReview.builder()
                .productId(new ObjectId(productId))
                .name(name.trim())
                .email(email.trim())
                .rating(rating)
                .comment(comment.trim())
                .createdAt(Instant.now())
                .build();

        // Lưu đánh giá
        ProductReview saved = reviewRepository.save(review);
        log.info("Đánh giá mới cho sản phẩm {}: {} sao", productId, rating);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ProductReview> getReviewsByProductId(String productId) {
        return reviewRepository.findActiveByProductId(new ObjectId(productId));
    }

    @Transactional(readOnly = true)
    public long getReviewCount(String productId) {
        return reviewRepository.countByProductId(new ObjectId(productId));
    }

    @Transactional(readOnly = true)
    public double getAverageRating(String productId) {
        List<ProductReview> reviews = getReviewsByProductId(productId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
    }
}
