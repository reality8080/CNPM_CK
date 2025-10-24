package com.example.spring_boot.services.products; // Package service quản lý sản phẩm

import com.example.spring_boot.domains.products.Product; // Entity sản phẩm
import com.example.spring_boot.domains.products.ProductAttribute; // Thuộc tính sản phẩm
import com.example.spring_boot.domains.products.ProductImage; // Ảnh sản phẩm
import com.example.spring_boot.dto.PageResponse;
import com.example.spring_boot.domains.products.Category; // Entity danh mục
import com.example.spring_boot.repository.products.ProductRepository; // Repository Mongo cho sản phẩm
import com.example.spring_boot.repository.products.CategoryRepository; // Repository danh mục

import lombok.RequiredArgsConstructor; // Inject constructor cho field final
import lombok.extern.slf4j.Slf4j; // Hỗ trợ logging
import org.springframework.data.domain.Page; // Kết quả phân trang
import org.springframework.data.domain.PageImpl; // Triển khai Page từ danh sách
import org.springframework.data.domain.Pageable; // Đầu vào phân trang
import org.springframework.data.mongodb.core.MongoTemplate; // MongoDB template cho query tối ưu
import org.springframework.data.mongodb.core.query.Criteria; // Criteria cho query
import org.springframework.data.mongodb.core.query.Query; // Query builder
import org.springframework.data.domain.Sort; // Sort cho ordering
import org.springframework.stereotype.Service; // Bean service Spring
import org.springframework.transaction.annotation.Transactional; // Transaction wrapper

import java.time.Instant; // Thời điểm UTC
import java.util.List; // Danh sách kết quả
import java.util.Map; // Map cho batch operations
import java.util.Set; // Set cho unique values
import java.util.HashSet; // HashSet implementation
import java.util.ArrayList; // ArrayList implementation
import java.util.HashMap; // HashMap implementation
import java.util.Objects; // Objects utility
import java.util.concurrent.ConcurrentHashMap; // Thread-safe cache
import java.util.stream.Collectors; // Stream operations
import org.bson.types.ObjectId; // ObjectId cho batch query
import org.springframework.scheduling.annotation.Async; // Async processing

@Service // Đăng ký bean service
@RequiredArgsConstructor // Tạo constructor cho field final
@Slf4j // Bật logging
@Transactional // Transaction cho class
public class ProductService {

    private final ProductRepository productRepository; // DAO sản phẩm
    private final CategoryRepository categoryRepository; // DAO danh mục
    private final MongoTemplate mongoTemplate; // MongoDB template cho query tối ưu

    // In-memory cache cho categories (thread-safe)
    private final Map<String, Category> categoryCache = new ConcurrentHashMap<>();
    private volatile long categoryCacheTimestamp = 0;
    private static final long CACHE_TTL = 300000; // 5 minutes

    /** Tạo product mới: reset id, set createdAt, lưu DB. */
    public Product create(Product product) {
        try {
            // Validate categoryId nếu có (cho phép bỏ trống)
            if (product.getCategoryId() != null) {
                if (!categoryRepository.existsById(product.getCategoryId().toHexString())) {
                    throw new RuntimeException("Category not found");
                }
            }

            product.setId(null); // Reset id để luôn tạo mới
            product.setCreatedAt(Instant.now()); // Gán thời điểm tạo
            Product savedProduct = productRepository.save(product); // Lưu và nhận entity đã lưu

            // Clear cache khi có thay đổi dữ liệu
            clearCache();

            return savedProduct; // Trả về entity đã lưu
        } catch (Exception e) {
            log.error("Create product failed, name={}", product != null ? product.getName() : null, e); // Log ngữ cảnh
            throw new RuntimeException("Failed to create product: " + e.getMessage(), e); // Bao lỗi nghiệp vụ
        }
    }

    /** Cập nhật product (chặn nếu đã bị xóa mềm). */
    public Product update(String id, Product updated) {
        try {
            Product existing = productRepository.findById(id) // Tìm theo id
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id)); // Không thấy -> 404
            if (existing.getDeletedAt() != null)
                throw new RuntimeException("Product has been deleted"); // Đã xóa mềm -> chặn cập nhật

            // Validate categoryId nếu có thay đổi
            if (updated.getCategoryId() != null && !updated.getCategoryId().equals(existing.getCategoryId())) {
                if (!categoryRepository.existsById(updated.getCategoryId().toHexString())) {
                    throw new RuntimeException("Category not found");
                }
                existing.setCategoryId(updated.getCategoryId()); // Cập nhật danh mục
            }

            existing.setName(updated.getName()); // Cập nhật tên
            existing.setDescription(updated.getDescription()); // Cập nhật mô tả
            existing.setPrice(updated.getPrice()); // Cập nhật giá
            existing.setStock(updated.getStock()); // Cập nhật tồn
            existing.setUpdatedAt(Instant.now()); // Gán thời điểm cập nhật

            Product savedProduct = productRepository.save(existing); // Lưu thay đổi

            // Clear cache khi có thay đổi dữ liệu
            clearCache();

            return savedProduct; // Trả về kết quả
        } catch (Exception e) {
            log.error("Update product failed, id={}", id, e); // Log ngữ cảnh
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e); // Bao lỗi nghiệp vụ
        }
    }

    /** Xóa mềm product. */
    public void softDelete(String id) {
        try {
            Product existing = productRepository.findById(id) // Tìm theo id
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id)); // Không thấy -> lỗi
            if (existing.getDeletedAt() != null)
                throw new RuntimeException("Product has been deleted"); // Đã xóa mềm -> chặn thao tác lặp
            existing.setDeletedAt(Instant.now()); // Đánh dấu xóa mềm
            productRepository.save(existing); // Lưu thay đổi

            // Clear cache khi có thay đổi dữ liệu
            clearCache();
        } catch (Exception e) {
            log.error("Soft delete product failed, id={}", id, e); // Log ngữ cảnh lỗi
            throw new RuntimeException("Failed to soft delete product: " + e.getMessage(), e); // Bao lỗi nghiệp vụ
        }
    }

    @Transactional(readOnly = true)
    /** Lấy product theo id (chỉ trả về nếu chưa bị xóa mềm) - TỐI ƯU HÓA. */
    public Product getById(String id) {
        try {
            // Sử dụng MongoTemplate với projection tối ưu
            Query query = new Query(Criteria.where("_id").is(id).and("deletedAt").isNull());
            query.fields().include("name", "description", "price", "stock", "categoryId", "createdAt", "updatedAt");

            Product p = mongoTemplate.findOne(query, Product.class);
            if (p == null) {
                throw new RuntimeException("Product not found with ID: " + id);
            }

            // Batch populate cho single product (tối ưu hơn)
            List<Product> singleProductList = List.of(p);
            batchPopulateCategories(singleProductList);
            batchPopulateAttributesAndImages(singleProductList);

            return p; // Trả về entity đã populate
        } catch (Exception e) {
            log.error("Get product by id failed, id={}", id, e); // Log lỗi
            throw new RuntimeException("Failed to get product: " + e.getMessage(), e); //
            // Bao lỗi nghiệp vụ
        }
    }

    @Transactional(readOnly = true)
    /** Lấy sản phẩm đang hoạt động theo trang - TỐI ƯU HÓA với batch loading. */
    public PageResponse<Product> getAllActive(int page, int size) {
        long startTime = System.currentTimeMillis();
        log.info("🚀 [PERFORMANCE] Getting active products with pagination: page={}, size={}", page, size);

        try {
            Query query = new Query(Criteria.where("deletedAt").isNull());
            query.fields().include("name", "description", "price", "stock", "categoryId", "createdAt", "updatedAt");
            optimizeQuery(query, "getAllActive");

            // Pagination
            query.skip((long) page * size).limit(size);

            List<Product> products = mongoTemplate.find(query, Product.class);

            // Batch load categories
            batchPopulateCategories(products);
            // Batch load attributes & images
            batchPopulateAttributesAndImages(products);

            // Count total products for pagination metadata
            long total = mongoTemplate.count(new Query(Criteria.where("deletedAt").isNull()), Product.class);

            long endTime = System.currentTimeMillis();
            log.info("✅ [PERFORMANCE] Completed in {}ms, retrieved {} products", endTime - startTime, products.size());

            return new PageResponse<>(products, total, page, size);
        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Get active products failed", e);
            throw new RuntimeException("Failed to list products: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    /** Tìm kiếm theo tên (ignore-case) - TỐI ƯU HÓA với compound query. */
    public List<Product> searchByName(String name) {
        long startTime = System.currentTimeMillis();
        log.info("🔍 [PERFORMANCE] Searching products by name: {}", name);

        try {
            // Sử dụng MongoTemplate với compound query tối ưu
            Query query = new Query();

            if (name == null || name.trim().isEmpty()) {
                // Nếu search term rỗng, lấy tất cả active products
                query.addCriteria(Criteria.where("deletedAt").isNull());
            } else {
                // Compound query: name search + soft delete filter
                query.addCriteria(Criteria.where("deletedAt").isNull())
                        .addCriteria(Criteria.where("name").regex(name, "i")); // Case-insensitive regex
            }
            optimizeQuery(query, "search");

            // Projection để chỉ lấy fields cần thiết
            query.fields().include("name", "description", "price", "stock", "categoryId", "createdAt");

            List<Product> products = mongoTemplate.find(query, Product.class);
            log.info("📊 [PERFORMANCE] Found {} products matching '{}' in {}ms",
                    products.size(), name, System.currentTimeMillis() - startTime);

            // BATCH LOADING: Load tất cả categories trong 1 query
            batchPopulateCategories(products);
            // BATCH LOADING: Load attributes & images trong 2 query
            batchPopulateAttributesAndImages(products);

            long endTime = System.currentTimeMillis();
            log.info("✅ [PERFORMANCE] Search completed in {}ms", endTime - startTime);
            return products; // Trả về danh sách đã populate
        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Search products failed, name={}", name, e); // Log lỗi
            throw new RuntimeException("Failed to search products: " + e.getMessage(), e); // Bao lỗi nghiệp vụ
        }
    }

    @Transactional(readOnly = true)
    /** Lấy sản phẩm theo categoryId - TỐI ƯU HÓA với compound index. */
    public List<Product> getByCategoryId(String categoryId) {
        long startTime = System.currentTimeMillis();
        log.info("📂 [PERFORMANCE] Getting products by category: {}", categoryId);

        try {
            // Sử dụng compound query tối ưu cho category + soft delete
            Query query = new Query(Criteria.where("categoryId").is(categoryId)
                    .and("deletedAt").isNull());
            optimizeQuery(query, "category");

            // Projection để chỉ lấy fields cần thiết
            query.fields().include("name", "description", "price", "stock", "categoryId", "createdAt");

            List<Product> products = mongoTemplate.find(query, Product.class);
            log.info("📊 [PERFORMANCE] Found {} products in category {} in {}ms",
                    products.size(), categoryId, System.currentTimeMillis() - startTime);

            // BATCH LOADING: Load tất cả categories trong 1 query
            batchPopulateCategories(products);
            // BATCH LOADING: Load attributes & images
            batchPopulateAttributesAndImages(products);

            long endTime = System.currentTimeMillis();
            log.info("✅ [PERFORMANCE] Category query completed in {}ms", endTime - startTime);
            return products; // Trả về danh sách đã populate
        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Get products by categoryId failed, categoryId={}", categoryId, e); // Log lỗi
            throw new RuntimeException("Failed to get products by category: " + e.getMessage(), e); // Bao lỗi nghiệp vụ
        }
    }

    @Transactional(readOnly = true)
    /** Phân trang sản phẩm active với filtering - TỐI ƯU HÓA với skip/limit. */
    public Page<Product> getPaged(Pageable pageable, String name, String categoryId) {
        long startTime = System.currentTimeMillis();
        log.info("📄 [PERFORMANCE] Getting paged products: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            // Sử dụng skip/limit thay vì load tất cả rồi slice
            Query query = new Query(Criteria.where("deletedAt").isNull());

            // Apply filters
            if (name != null && !name.trim().isEmpty()) {
                query.addCriteria(Criteria.where("name").regex(name, "i"));
                log.debug("🔄 [DEBUG] Filtering by name: {}", name);
            }

            if (categoryId != null && !categoryId.trim().isEmpty()) {
                try {
                    ObjectId categoryObjectId = new ObjectId(categoryId);
                    query.addCriteria(Criteria.where("categoryId").is(categoryObjectId));
                    log.debug("🔄 [DEBUG] Filtering by categoryId: {}", categoryId);
                } catch (Exception e) {
                    log.warn("🔄 [DEBUG] Invalid categoryId format: {}", categoryId);
                }
            }

            // Debug: Log query
            log.debug("🔄 [DEBUG] Query: {}", query.toString());
            // Projection để chỉ lấy fields cần thiết
            // query.fields().include("name", "description", "price", "stock", "categoryId",
            // "createdAt", "updatedAt");

            // Apply pagination
            query.skip(pageable.getOffset());
            query.limit(pageable.getPageSize());

            // Apply sorting
            if (pageable.getSort().isSorted()) {
                pageable.getSort().forEach(order -> {
                    query.with(org.springframework.data.domain.Sort.by(
                            order.getDirection(), order.getProperty()));
                });
            } else {
                // Default sort by creation time ascending (from old to new)
                query.with(org.springframework.data.domain.Sort.by("createdAt").ascending());
            }

            List<Product> products = mongoTemplate.find(query, Product.class);
            log.info("📊 [PERFORMANCE] Retrieved {} products for page {} in {}ms",
                    products.size(), pageable.getPageNumber(), System.currentTimeMillis() - startTime);

            // Debug: Kiểm tra categoryId của products
            log.debug("🔄 [DEBUG] Checking categoryIds in retrieved products:");
            for (Product product : products) {
                log.debug("🔄 [DEBUG] Product ID: {}, categoryId: {}",
                        product.getId(), product.getCategoryId());
            }

            // Debug: Kiểm tra trực tiếp database
            Query debugQuery = new Query(Criteria.where("deletedAt").isNull());
            debugQuery.limit(1);
            debugQuery.fields().include("id", "categoryId");
            List<Product> debugProducts = mongoTemplate.find(debugQuery, Product.class);
            if (!debugProducts.isEmpty()) {
                Product debugProduct = debugProducts.get(0);
                log.debug("🔄 [DEBUG] Direct DB query - Product ID: {}, categoryId: {}",
                        debugProduct.getId(), debugProduct.getCategoryId());
            }

            // Count total records (separate query for efficiency)
            long totalCount = getTotalActiveCount();

            // BATCH LOADING: Load tất cả categories trong 1 query
            batchPopulateCategories(products);
            // // BATCH LOADING: Load attributes & images
            batchPopulateAttributesAndImages(products);

            long endTime = System.currentTimeMillis();
            log.info("✅ [PERFORMANCE] Pagination completed in {}ms", endTime - startTime);

            // Tạo custom PageImpl với totalPages làm tròn xuống
            return createCustomPageImpl(products, pageable, totalCount);
        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Get paged products failed, page={}, size={}",
                    pageable.getPageNumber(), pageable.getPageSize(), e); // Log lỗi
            throw new RuntimeException("Failed to paginate products: " + e.getMessage(), e); // Bao lỗi nghiệp vụ
        }
    }

    @Transactional(readOnly = true)
    /**
     * Phân trang sản phẩm active - TỐI ƯU HÓA với skip/limit (backward
     * compatibility).
     */
    public Page<Product> getPaged(Pageable pageable) {
        return getPaged(pageable, null, null);
    }

    // =====================================================
    // HELPER METHODS - Các phương thức hỗ trợ tối ưu hóa
    // =====================================================

    /**
     * Batch populate categories với caching - TỐI ƯU HÓA
     * Tối ưu: Single query + in-memory cache để load tất cả categories cần thiết
     */
    private void batchPopulateCategories(List<Product> products) {
        if (products == null || products.isEmpty()) {
            log.debug("🔄 [DEBUG] No products to populate categories");
            return;
        }

        long startTime = System.currentTimeMillis();
        log.debug("🔄 [DEBUG] Starting batchPopulateCategories for {} products", products.size());

        // Step 1: Collect unique category IDs from products
        Set<String> categoryIdSet = new HashSet<>();
        for (Product product : products) {
            if (product.getCategoryId() != null) {
                String categoryIdStr = product.getCategoryId().toString();
                categoryIdSet.add(categoryIdStr);
                log.debug("🔄 [DEBUG] Found categoryId: {} for product: {}", categoryIdStr, product.getId());
            } else {
                log.debug("🔄 [DEBUG] Product {} has null categoryId", product.getId());
            }
        }

        if (categoryIdSet.isEmpty()) {
            log.debug("🔄 [DEBUG] No categoryIds found, skipping populate");
            return;
        }

        List<String> categoryIds = new ArrayList<>(categoryIdSet);
        log.debug("🔄 [DEBUG] Unique categoryIds to load: {}", categoryIds);

        // Step 2: Check cache and collect missing IDs
        Map<String, Category> categoryMap = new HashMap<>();
        List<String> missingCategoryIds = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        for (String categoryId : categoryIds) {
            Category cached = categoryCache.get(categoryId);
            if (cached != null && (currentTime - categoryCacheTimestamp) < CACHE_TTL) {
                categoryMap.put(categoryId, cached);
                log.debug("🔄 [DEBUG] Using cached category: {} - {}", categoryId, cached.getName());
            } else {
                missingCategoryIds.add(categoryId);
                log.debug("🔄 [DEBUG] Category {} not in cache or expired", categoryId);
            }
        }

        // Step 3: Load missing categories from database
        if (!missingCategoryIds.isEmpty()) {
            log.debug("🔄 [DEBUG] Loading {} missing categories from DB: {}", missingCategoryIds.size(),
                    missingCategoryIds);

            try {
                // Convert String IDs to ObjectIds for MongoDB query
                List<ObjectId> objectIds = missingCategoryIds.stream()
                        .map(id -> {
                            try {
                                return new ObjectId(id);
                            } catch (Exception e) {
                                log.warn("🔄 [DEBUG] Invalid ObjectId format: {}", id);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (!objectIds.isEmpty()) {
                    Query categoryQuery = new Query(Criteria.where("_id").in(objectIds));
                    categoryQuery.fields().include("id", "name", "description", "createdAt");
                    List<Category> categories = mongoTemplate.find(categoryQuery, Category.class);

                    log.debug("🔄 [DEBUG] Found {} categories in DB", categories.size());

                    // Update cache and map
                    for (Category category : categories) {
                        String categoryIdStr = category.getId();
                        categoryCache.put(categoryIdStr, category);
                        categoryMap.put(categoryIdStr, category);
                        log.debug("🔄 [DEBUG] Loaded and cached category: {} - {}", categoryIdStr, category.getName());
                    }
                }

                // Update cache timestamp
                categoryCacheTimestamp = System.currentTimeMillis();

            } catch (Exception e) {
                log.error("🔄 [DEBUG] Error loading categories from DB", e);
            }
        }

        // Step 4: Populate categories into products
        int populatedCount = 0;
        for (Product product : products) {
            if (product.getCategoryId() != null) {
                String categoryIdStr = product.getCategoryId().toString();
                Category category = categoryMap.get(categoryIdStr);
                if (category != null) {
                    product.setCategory(category);
                    populatedCount++;
                    log.debug("🔄 [DEBUG] Populated category for product {}: {} - {}",
                            product.getId(), category.getId(), category.getName());
                } else {
                    log.warn("🔄 [DEBUG] Category not found for product {} with categoryId: {}",
                            product.getId(), categoryIdStr);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        log.debug(
                "🔄 [PERFORMANCE] Batch populated {} categories ({} from cache, {} from DB) in {}ms. Successfully populated {} products.",
                categoryMap.size(), categoryIds.size() - missingCategoryIds.size(), missingCategoryIds.size(),
                endTime - startTime, populatedCount);
    }

    /**
     * Get total count of active products
     * Tối ưu: Sử dụng count query với index
     */
    private long getTotalActiveCount() {
        Query countQuery = new Query(Criteria.where("deletedAt").isNull());
        return mongoTemplate.count(countQuery, Product.class);
    }

    // Cache cho statistics
    private volatile Map<String, Object> statisticsCache = null;
    private volatile long statisticsCacheTimestamp = 0;
    private static final long STATISTICS_CACHE_TTL = 600000; // 10 minutes

    /**
     * Tạo thống kê sản phẩm với aggregation và caching - TỐI ƯU HÓA
     * Tối ưu: Single aggregation query + caching cho multiple statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getProductStatistics() {
        // Check cache first
        long currentTime = System.currentTimeMillis();
        if (statisticsCache != null && (currentTime - statisticsCacheTimestamp) < STATISTICS_CACHE_TTL) {
            log.debug("📈 [PERFORMANCE] Returning cached statistics");
            return new java.util.HashMap<>(statisticsCache);
        }

        log.info("📈 [PERFORMANCE] Getting product statistics");
        long startTime = System.currentTimeMillis();

        try {
            // Sử dụng aggregation để tính toán statistics trong 1 query
            org.springframework.data.mongodb.core.aggregation.Aggregation aggregation = org.springframework.data.mongodb.core.aggregation.Aggregation
                    .newAggregation(
                            // Match active products only
                            org.springframework.data.mongodb.core.aggregation.Aggregation.match(
                                    Criteria.where("deletedAt").isNull()),

                            // Group and calculate statistics
                            org.springframework.data.mongodb.core.aggregation.Aggregation.group()
                                    .count().as("totalProducts")
                                    .avg("price").as("averagePrice")
                                    .sum("stock").as("totalStock")
                                    .min("price").as("minPrice")
                                    .max("price").as("maxPrice"),

                            // Project results
                            org.springframework.data.mongodb.core.aggregation.Aggregation.project()
                                    .and("totalProducts").as("totalProducts")
                                    .and("averagePrice").as("averagePrice")
                                    .and("totalStock").as("totalStock")
                                    .and("minPrice").as("minPrice")
                                    .and("maxPrice").as("maxPrice"));

            @SuppressWarnings("rawtypes")
            org.springframework.data.mongodb.core.aggregation.AggregationResults<Map> results = mongoTemplate
                    .aggregate(aggregation, "products", Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> stats = (Map<String, Object>) results.getUniqueMappedResult();
            if (stats == null) {
                stats = Map.of(
                        "totalProducts", 0L,
                        "averagePrice", 0.0,
                        "totalStock", 0L,
                        "minPrice", 0.0,
                        "maxPrice", 0.0);
            }

            // Update cache
            statisticsCache = new java.util.HashMap<>(stats);
            statisticsCacheTimestamp = currentTime;

            long endTime = System.currentTimeMillis();
            log.info("✅ [PERFORMANCE] Statistics completed in {}ms: {}", endTime - startTime, stats);
            return stats;

        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Error getting statistics", e);
            throw new RuntimeException("Failed to get statistics: " + e.getMessage(), e);
        }
    }

    /**
     * Batch populate attributes và images - TỐI ƯU HÓA HIỆU SUẤT
     * Sử dụng sequential processing với HashMap để tối ưu lookup
     */
    private void batchPopulateAttributesAndImages(List<Product> products) {
        if (products == null || products.isEmpty())
            return;

        // Chuẩn hóa danh sách ObjectId từ product.id (String)
        List<ObjectId> productObjectIds = products.stream()
                .map(Product::getId)
                .filter(id -> id != null && !id.isBlank())
                .map(ObjectId::new)
                .collect(Collectors.toList());
        if (productObjectIds.isEmpty())
            return;

        long start = System.currentTimeMillis();

        try {
            // Query attributes - chỉ lấy fields cần thiết, loại bỏ createdAt
            Query attrQuery = new Query(Criteria.where("productId").in(productObjectIds)
                    .and("deletedAt").isNull());
            attrQuery.fields().include("name", "value", "productId");
            List<ProductAttribute> allAttributes = mongoTemplate.find(attrQuery, ProductAttribute.class);

            // Query images - chỉ lấy 1 ảnh đại diện cho mỗi sản phẩm
            Query imgQuery = new Query(Criteria.where("productId").in(productObjectIds)
                    .and("deletedAt").isNull());
            imgQuery.fields().include("imageUrl", "isPrimary", "productId");
            imgQuery.with(Sort.by("isPrimary").descending().and(Sort.by("createdAt").ascending()));
            List<ProductImage> allImages = mongoTemplate.find(imgQuery, ProductImage.class);

            // Group attributes by productId - sử dụng HashMap cho O(1) lookup
            Map<String, List<ProductAttribute>> productIdToAttributes = new HashMap<>();
            for (ProductAttribute attr : allAttributes) {
                String productId = attr.getProductId().toHexString();
                productIdToAttributes.computeIfAbsent(productId, k -> new ArrayList<>()).add(attr);
            }

            // Group images by productId - chỉ lấy 1 ảnh đầu tiên cho mỗi sản phẩm
            Map<String, List<ProductImage>> productIdToImages = new HashMap<>();
            Set<String> processedProducts = new HashSet<>();
            for (ProductImage img : allImages) {
                String productId = img.getProductId().toHexString();
                if (!processedProducts.contains(productId)) {
                    productIdToImages.computeIfAbsent(productId, k -> new ArrayList<>()).add(img);
                    processedProducts.add(productId);
                }
            }

            // Populate products - sử dụng sequential để tránh race condition
            for (Product p : products) {
                String pid = p.getId();
                if (pid != null) {
                    List<ProductAttribute> attrs = productIdToAttributes.get(pid);
                    if (attrs != null)
                        p.setAttributes(attrs);
                    List<ProductImage> imgs = productIdToImages.get(pid);
                    if (imgs != null)
                        p.setImages(imgs);
                }
            }

            log.debug("🔄 [PERFORMANCE] Batch populated attributes={} images={} for {} products in {}ms",
                    allAttributes.size(), allImages.size(), products.size(), System.currentTimeMillis() - start);

        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Error in batch populate", e);
            // Fallback to sequential processing
            fallbackSequentialPopulate(products, productObjectIds);
        }
    }

    /**
     * Fallback sequential processing nếu parallel processing thất bại
     */
    private void fallbackSequentialPopulate(List<Product> products, List<ObjectId> productObjectIds) {
        long start = System.currentTimeMillis();

        // Sequential queries
        Query attrQuery = new Query(Criteria.where("productId").in(productObjectIds)
                .and("deletedAt").isNull());
        attrQuery.fields().include("name", "value", "productId", "createdAt");
        List<ProductAttribute> allAttributes = mongoTemplate.find(attrQuery, ProductAttribute.class);

        Query imgQuery = new Query(Criteria.where("productId").in(productObjectIds)
                .and("deletedAt").isNull());
        imgQuery.fields().include("imageUrl", "isPrimary", "productId", "createdAt");
        imgQuery.with(Sort.by("isPrimary").descending().and(Sort.by("createdAt").ascending()));
        List<ProductImage> allImages = mongoTemplate.find(imgQuery, ProductImage.class);

        // Group và populate
        Map<String, List<ProductAttribute>> productIdToAttributes = allAttributes.stream()
                .collect(Collectors.groupingBy(a -> a.getProductId().toHexString()));
        Map<String, List<ProductImage>> productIdToImages = allImages.stream()
                .collect(Collectors.groupingBy(i -> i.getProductId().toHexString()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .limit(1) // Chỉ lấy 1 ảnh đại diện
                                .collect(Collectors.toList())));

        products.forEach(p -> {
            String pid = p.getId();
            if (pid != null) {
                List<ProductAttribute> attrs = productIdToAttributes.get(pid);
                if (attrs != null)
                    p.setAttributes(attrs);
                List<ProductImage> imgs = productIdToImages.get(pid);
                if (imgs != null)
                    p.setImages(imgs);
            }
        });

        log.debug("🔄 [PERFORMANCE] Fallback sequential populated attributes={} images={} for {} products in {}ms",
                allAttributes.size(), allImages.size(), products.size(), System.currentTimeMillis() - start);
    }

    /**
     * Tạo custom PageImpl với totalPages làm tròn xuống
     */
    private Page<Product> createCustomPageImpl(List<Product> content, Pageable pageable, long total) {
        return new PageImpl<>(content, pageable, total) {
            @Override
            public int getTotalPages() {
                // Sử dụng Math.floor để làm tròn xuống thay vì Math.ceil
                return getTotalElements() == 0 ? 0
                        : Math.max(1, (int) Math.floor((double) getTotalElements() / getSize()));
            }
        };
    }

    /**
     * Clear cache khi có thay đổi dữ liệu
     */
    public void clearCache() {
        categoryCache.clear();
        statisticsCache = null;
        categoryCacheTimestamp = 0;
        statisticsCacheTimestamp = 0;
        log.info("🧹 [PERFORMANCE] Cache cleared");
    }

    /**
     * Tối ưu hóa query với compound index hints
     */
    private void optimizeQuery(Query query, String operation) {
        // Thêm hints cho compound indexes nếu có
        if (operation.contains("category")) {
            // Hint cho compound index: categoryId + deletedAt
            query.withHint("categoryId_1_deletedAt_1");
        } else if (operation.contains("search")) {
            // Hint cho text index hoặc compound index
            query.withHint("name_1_deletedAt_1");
        } else {
            // Hint cho deletedAt index
            query.withHint("deletedAt_1");
        }
    }

    /**
     * Lấy sản phẩm bán chạy - TỐI ƯU HÓA
     * Sắp xếp theo stock thấp nhất (đã bán nhiều) và createdAt gần đây
     */
    @Transactional(readOnly = true)
    public List<Product> getBestSellingProducts(int limit) {
        long startTime = System.currentTimeMillis();
        log.info("🔥 [PERFORMANCE] Getting best selling products, limit={}", limit);

        try {
            Query query = new Query(Criteria.where("deletedAt").isNull());
            query.fields().include("name", "description", "price", "stock", "categoryId", "createdAt", "updatedAt");

            // Sắp xếp theo stock thấp (đã bán nhiều) và createdAt từ cũ đến mới
            query.with(org.springframework.data.domain.Sort.by("stock").ascending()
                    .and(org.springframework.data.domain.Sort.by("createdAt").ascending()));

            query.limit(limit);

            List<Product> products = mongoTemplate.find(query, Product.class);

            // Batch load categories, attributes và images
            batchPopulateCategories(products);
            batchPopulateAttributesAndImages(products);

            long endTime = System.currentTimeMillis();
            log.info("✅ [PERFORMANCE] Retrieved {} best selling products in {}ms",
                    products.size(), endTime - startTime);

            return products;
        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Error getting best selling products", e);
            throw new RuntimeException("Failed to get best selling products: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy sản phẩm mới - TỐI ƯU HÓA
     * Sắp xếp theo createdAt gần đây nhất
     */
    @Transactional(readOnly = true)
    public List<Product> getNewProducts(int limit) {
        long startTime = System.currentTimeMillis();
        log.info("🆕 [PERFORMANCE] Getting new products, limit={}", limit);

        try {
            Query query = new Query(Criteria.where("deletedAt").isNull());
            query.fields().include("name", "description", "price", "stock", "categoryId", "createdAt", "updatedAt");

            // Sắp xếp theo createdAt từ cũ đến mới
            query.with(org.springframework.data.domain.Sort.by("createdAt").ascending());

            query.limit(limit);

            List<Product> products = mongoTemplate.find(query, Product.class);

            // Batch load categories, attributes và images
            batchPopulateCategories(products);
            batchPopulateAttributesAndImages(products);

            long endTime = System.currentTimeMillis();
            log.info("✅ [PERFORMANCE] Retrieved {} new products in {}ms",
                    products.size(), endTime - startTime);

            return products;
        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Error getting new products", e);
            throw new RuntimeException("Failed to get new products: " + e.getMessage(), e);
        }
    }

    /**
     * Preload categories vào cache
     */
    @Async
    public void preloadCategories() {
        try {
            Query query = new Query();
            query.fields().include("id", "name", "description", "createdAt");
            List<Category> allCategories = mongoTemplate.find(query, Category.class);

            categoryCache.clear();
            for (Category category : allCategories) {
                categoryCache.put(category.getId(), category);
            }
            categoryCacheTimestamp = System.currentTimeMillis();

            log.info("🔄 [PERFORMANCE] Preloaded {} categories into cache", allCategories.size());
        } catch (Exception e) {
            log.error("❌ [PERFORMANCE] Error preloading categories", e);
        }
    }

}
