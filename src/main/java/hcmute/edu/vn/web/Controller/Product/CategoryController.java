package hcmute.edu.vn.web.Controller.Product;

import hcmute.edu.vn.web.Entity.Product.*;
import hcmute.edu.vn.web.Service.implement.Product.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final PromotionProgramService promotionProgramService;
    private final AppDiscountService appDiscountService;

    @Operation(summary = "Create a new category", description = "Creates a new category in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @Operation(summary = "Get a category by ID", description = "Retrieves a category by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String categoryId) {
        return categoryService.getCategoryById(categoryId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all categories", description = "Retrieves all categories")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Search categories by name", description = "Searches categories by name (case-insensitive)")
    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategoriesByName(@RequestParam String name) {
        return ResponseEntity.ok(categoryService.searchCategoriesByName(name));
    }

    @Operation(summary = "Update a category", description = "Updates an existing category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable String categoryId, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, category));
    }

    @Operation(summary = "Delete a category", description = "Deletes a category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (IllegalStateException e) {
            // Bắt lỗi ràng buộc dữ liệu
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // HTTP 409 Conflict
        } catch (RuntimeException e) {
            // Bắt lỗi không tìm thấy (Not Found)
            return ResponseEntity.notFound().build(); // HTTP 404
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Category>> createBulkCategory(
            @Valid @RequestBody List<Category> discounts) {

        List<Category> savedDiscounts = categoryService.saveAll(discounts);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDiscounts);
    }

}
