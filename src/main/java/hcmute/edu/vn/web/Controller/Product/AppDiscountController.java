package hcmute.edu.vn.web.Controller.Product;

import hcmute.edu.vn.web.Entity.Product.AppDiscount;
import hcmute.edu.vn.web.Service.implement.Product.AppDiscountService;
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
@RequestMapping("/api/v1/admin/app-discounts")
@RequiredArgsConstructor
public class AppDiscountController {
    private final AppDiscountService appDiscountService;

    @Operation(summary = "Create a new app discount", description = "Creates a new app discount in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "App discount created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<AppDiscount> createAppDiscount(@RequestBody AppDiscount appDiscount) {
        return ResponseEntity.ok(appDiscountService.createAppDiscount(appDiscount));
    }

    @Operation(summary = "Get an app discount by ID", description = "Retrieves an app discount by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "App discount found"),
            @ApiResponse(responseCode = "404", description = "App discount not found")
    })
    @GetMapping("/{discountId}")
    public ResponseEntity<AppDiscount> getAppDiscountById(@PathVariable String discountId) {
        return appDiscountService.getAppDiscountById(discountId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all app discounts", description = "Retrieves all app discounts")
    @GetMapping
    public ResponseEntity<List<AppDiscount>> getAllAppDiscounts() {
        return ResponseEntity.ok(appDiscountService.getAllAppDiscounts());
    }

    @Operation(summary = "Get app discounts by shop", description = "Retrieves app discounts by shop ID")
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<AppDiscount>> getAppDiscountsByShop(@PathVariable String shopId) {
        return ResponseEntity.ok(appDiscountService.getAppDiscountsByShop(shopId));
    }

    @Operation(summary = "Update an app discount", description = "Updates an existing app discount by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "App discount updated successfully"),
            @ApiResponse(responseCode = "404", description = "App discount not found")
    })
    @PutMapping("/{discountId}")
    public ResponseEntity<AppDiscount> updateAppDiscount(@PathVariable String discountId, @RequestBody AppDiscount appDiscount) {
        return ResponseEntity.ok(appDiscountService.updateAppDiscount(discountId, appDiscount));
    }

    @Operation(summary = "Delete an app discount", description = "Deletes an app discount by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "App discount deleted successfully"),
            @ApiResponse(responseCode = "404", description = "App discount not found")
    })
    @DeleteMapping("/{discountId}")
    public ResponseEntity<Void> deleteAppDiscount(@PathVariable String discountId) {
        appDiscountService.deleteAppDiscount(discountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AppDiscount>> createBulkAppDiscounts(
            @Valid @RequestBody List<AppDiscount> discounts) {

        List<AppDiscount> savedDiscounts = appDiscountService.saveAll(discounts);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDiscounts);
    }

}

