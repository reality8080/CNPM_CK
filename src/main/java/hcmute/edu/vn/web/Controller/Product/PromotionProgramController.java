package hcmute.edu.vn.web.Controller.Product;

import hcmute.edu.vn.web.Entity.Product.PromotionProgram;
import hcmute.edu.vn.web.Service.implement.Product.CategoryService;
import hcmute.edu.vn.web.Service.implement.Product.ProductService;
import hcmute.edu.vn.web.Service.implement.Product.PromotionProgramService;
import hcmute.edu.vn.web.Service.implement.Product.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/promotion-programs")
@RequiredArgsConstructor
public class PromotionProgramController {
    private final PromotionProgramService promotionProgramService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final ProductService productService;

    @Operation(summary = "Create a new promotion program", description = "Creates a new promotion program in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion program created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<PromotionProgram> createPromotionProgram(@RequestBody PromotionProgram promotionProgram) {
        return ResponseEntity.ok(promotionProgramService.createPromotionProgram(promotionProgram));
    }

    @Operation(summary = "Get a promotion program by ID", description = "Retrieves a promotion program by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion program found"),
            @ApiResponse(responseCode = "404", description = "Promotion program not found")
    })
    @GetMapping("/{promotionId}")
    public ResponseEntity<PromotionProgram> getPromotionProgramById(@PathVariable String promotionId) {
        return promotionProgramService.getPromotionProgramById(promotionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all promotion programs", description = "Retrieves all promotion programs")
    @GetMapping
    public ResponseEntity<List<PromotionProgram>> getAllPromotionPrograms() {
        return ResponseEntity.ok(promotionProgramService.getAllPromotionPrograms());
    }

    @Operation(summary = "Get promotion programs by type", description = "Retrieves promotion programs by type")
    @GetMapping("/type/{promotionType}")
    public ResponseEntity<List<PromotionProgram>> getPromotionProgramsByType(@PathVariable String promotionType) {
        return ResponseEntity.ok(promotionProgramService.getPromotionProgramsByType(promotionType));
    }

    @Operation(summary = "Update a promotion program", description = "Updates an existing promotion program by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion program updated successfully"),
            @ApiResponse(responseCode = "404", description = "Promotion program not found")
    })
    @PutMapping("/{promotionId}")
    public ResponseEntity<PromotionProgram> updatePromotionProgram(@PathVariable String promotionId, @RequestBody PromotionProgram promotionProgram) {
        return ResponseEntity.ok(promotionProgramService.updatePromotionProgram(promotionId, promotionProgram));
    }

    @Operation(summary = "Delete a promotion program", description = "Deletes a promotion program by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promotion program deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Promotion program not found")
    })
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<Void> deletePromotionProgram(@PathVariable String promotionId) {
        promotionProgramService.deletePromotionProgram(promotionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<PromotionProgram>> createBulkCategory(
            @Valid @RequestBody List<PromotionProgram> discounts) {

        List<PromotionProgram> savedDiscounts = promotionProgramService.saveAll(discounts);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDiscounts);
    }
}