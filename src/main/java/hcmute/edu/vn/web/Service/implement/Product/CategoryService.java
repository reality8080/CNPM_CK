package hcmute.edu.vn.web.Service.implement.Product;

import hcmute.edu.vn.web.Entity.Product.Category;
import hcmute.edu.vn.web.Entity.Product.Product;
import hcmute.edu.vn.web.Repository.Product.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final String[] categoryNames = {"Áo thun", "Quần jeans", "Đầm nữ", "Áo khoác", "Phụ kiện"};

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById(String categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> searchCategoriesByName(String name) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(name);
    }

    @Transactional
    public Category updateCategory(String categoryId, Category updatedCategory) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (existingCategory.isPresent()) {
            updatedCategory.setCategoryId(categoryId);
            return categoryRepository.save(updatedCategory);
        } else {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }
    }

    @Transactional
    public void deleteCategory(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }

        // BƯỚC BỔ SUNG: Kiểm tra xem có Product nào đang sử dụng Category này không
        List<Product> linkedProducts = productService.getProductsByCategory(categoryId);
        if (!linkedProducts.isEmpty()) {
            // NÊN: Ném một exception rõ ràng để Controller trả về mã lỗi 409 Conflict
            throw new IllegalStateException("Cannot delete Category. " + linkedProducts.size() + " Product(s) are linked to it.");

            // HOẶC: (Nếu muốn xóa luôn)
            // linkedProducts.forEach(product -> productService.deleteProduct(product.getProductId()));
        }

        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public List<Category> seedCategories(int count) {
        List<Category> categories = new ArrayList<>();
        String[] categoryNames = {"Áo thun", "Quần jeans", "Đầm nữ", "Áo khoác", "Phụ kiện"};

        for (int i = 1; i <= count; i++) {
            categories.add(Category.builder()
                    .categoryId(UUID.randomUUID().toString())
                    .categoryName(categoryNames[i % categoryNames.length] + " " + ((i / categoryNames.length) + 1))
                    .description("Danh mục các sản phẩm " + categoryNames[i % categoryNames.length].toLowerCase() + " theo phong cách hiện đại.")
                    .createdDate(LocalDateTime.now().minusDays(20 - (i % 20)))
                    .build());
        }
        return categoryRepository.saveAll(categories);
    }

    // Bổ sung phương thức saveAll cho đồng bộ
    public List<Category> saveAll(List<Category> categories) {
        return categoryRepository.saveAll(categories);
    }

}