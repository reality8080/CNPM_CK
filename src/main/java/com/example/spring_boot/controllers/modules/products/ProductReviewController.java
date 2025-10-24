package com.example.spring_boot.controllers.modules.products;


import com.example.spring_boot.domains.products.ProductReview;
import com.example.spring_boot.dto.ApiResponse;
import com.example.spring_boot.services.products.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    @PostMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<ProductReview>> createReview(
            @PathVariable String productId,
            @RequestBody ReviewRequest request) {

        ProductReview review = reviewService.createReview(
                productId,
                request.name(),
                request.email(),
                request.rating(),
                request.comment()
        );

        return ResponseEntity.ok(ApiResponse.success(review, "Gửi đánh giá thành công!"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ProductReview>>> getReviews(@PathVariable String productId) {
        List<ProductReview> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ApiResponse<ReviewStats>> getReviewStats(@PathVariable String productId) {
        long count = reviewService.getReviewCount(productId);
        double avg = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(ApiResponse.ok(new ReviewStats(count, avg)));
    }
}

// DTOs
record ReviewRequest(String name, String email, Integer rating, String comment) {}
record ReviewStats(long count, double averageRating) {}