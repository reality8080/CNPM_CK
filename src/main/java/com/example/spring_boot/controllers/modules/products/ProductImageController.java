package com.example.spring_boot.controllers.modules.products;

import com.example.spring_boot.domains.products.ProductImage;
import com.example.spring_boot.dto.ApiResponse;
import com.example.spring_boot.services.products.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Module API cho ProductImage (quản lý hình ảnh sản phẩm) */
@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
@Tag(name = "Product Images", description = "APIs quản lý hình ảnh sản phẩm (trả về ApiResponse)")
public class ProductImageController {

    private final ProductImageService productImageService;

    /**
     * Tạo hình ảnh sản phẩm với validation giới hạn
     * Test API:
     * - POST /api/product-images
     * - Body: {"productId":"PRODUCT_ID","imageUrl":"URL","altText":"Description"}
     */
    @PostMapping
    @Operation(summary = "Tạo hình ảnh sản phẩm")
    public ResponseEntity<ApiResponse<ProductImage>> create(@RequestBody ProductImage img) {
        try {
            ProductImage created = productImageService.create(img);
            return ResponseEntity.ok(ApiResponse.success(created, "Product image created successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("giới hạn tối đa")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.fail(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Failed to create product image: " + e.getMessage()));
        }
    }

    /** DELETE /api/product-images/{id} */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa hình ảnh sản phẩm (soft)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        productImageService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product image deleted successfully"));
    }

    /** GET /api/product-images/by-product/{productId} */
    @GetMapping("/by-product/{productId}")
    @Operation(summary = "Lấy hình ảnh theo product ID")
    public ApiResponse<List<ProductImage>> getByProduct(@PathVariable String productId) {
        return ApiResponse.success(productImageService.getByProductId(productId),
                "Product images retrieved successfully");
    }

    /**
     * Kiểm tra số lượng ảnh hiện tại và giới hạn
     * GET /api/product-images/check-limit/{productId}
     */
    @GetMapping("/check-limit/{productId}")
    @Operation(summary = "Kiểm tra giới hạn ảnh của sản phẩm")
    public ApiResponse<Map<String, Object>> checkImageLimit(@PathVariable String productId) {
        try {
            int currentCount = productImageService.getImageCountByProductId(productId);
            boolean canAddMore = productImageService.canAddMoreImages(productId);
            int maxAllowed = ProductImageService.getMaxImagesPerProduct();

            Map<String, Object> result = new HashMap<>();
            result.put("currentCount", currentCount);
            result.put("maxAllowed", maxAllowed);
            result.put("canAddMore", canAddMore);
            result.put("remainingSlots", Math.max(0, maxAllowed - currentCount));

            return ApiResponse.success(result, "Image limit check completed");
        } catch (Exception e) {
            return ApiResponse.fail("Failed to check image limit: " + e.getMessage());
        }
    }
}
