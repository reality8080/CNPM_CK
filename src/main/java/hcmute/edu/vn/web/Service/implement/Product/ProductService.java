package hcmute.edu.vn.web.Service.implement.Product;

import hcmute.edu.vn.web.Entity.Product.Category;
import hcmute.edu.vn.web.Entity.Product.Product;
import hcmute.edu.vn.web.Entity.Product.Supplier;
import hcmute.edu.vn.web.Repository.Product.ProductRepository;
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
public class ProductService {
    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsBySupplier(String supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }

    @Transactional
    public Product updateProduct(String productId, Product updatedProduct) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isPresent()) {
            updatedProduct.setProductId(productId);
            updatedProduct.setFavoriteCount(existingProduct.get().getFavoriteCount());
            updatedProduct.setSoldQuantity(existingProduct.get().getSoldQuantity());
            updatedProduct.setCreatedDate(existingProduct.get().getCreatedDate());
            updatedProduct.setRatingScore(existingProduct.get().getRatingScore());
            return productRepository.save(updatedProduct);
        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    @Transactional
    public void deleteProduct(String productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    @Transactional
    public List<Product> seedProducts(int count, List<Category> categories, List<Supplier> suppliers) {
        if (categories.isEmpty() || suppliers.isEmpty()) {
            throw new IllegalArgumentException("Không thể tạo Product khi thiếu Category hoặc Supplier.");
        }

        // ... logic tạo 20 Product, sử dụng categories và suppliers để gán ID ...

        List<Product> products = new ArrayList<>();
        String[] productPrefixes = {"Áo Polo", "Quần Skinny", "Váy Xòe", "Áo Hoodie", "Thắt Lưng Da"};

        for (int i = 1; i <= count; i++) {
            Category category = categories.get(i % categories.size());
            Supplier supplier = suppliers.get(i % suppliers.size());
            double price = ThreadLocalRandom.current().nextDouble(50000, 500000);

            products.add(Product.builder()
                    .productId(UUID.randomUUID().toString())
                    .productName(productPrefixes[i % productPrefixes.length] + " Cao Cấp Màu " + (i % 3 == 0 ? "Đen" : "Trắng"))
                    .description("Sản phẩm làm từ chất liệu thoáng mát, phù hợp với mọi thời tiết.")
                    .price(Math.round(price / 1000.0) * 1000.0)
                    .imageUrl("http://example.com/images/product" + i + ".jpg")
                    .status(i % 4 == 0 ? "Inactive" : "Active")
                    .soldQuantity(ThreadLocalRandom.current().nextInt(10, 500))
                    .createdDate(LocalDateTime.now().minusDays(i))
                    .ratingScore(ThreadLocalRandom.current().nextDouble(3.5, 5.0))
                    .favoriteCount(ThreadLocalRandom.current().nextInt(5, 500))
                    .categoryId(category.getCategoryId())
                    .supplierId(supplier.getSupplierId())
                    .build());
        }

        return productRepository.saveAll(products);
    }

    // Bổ sung phương thức saveAll cho đồng bộ
    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }

}