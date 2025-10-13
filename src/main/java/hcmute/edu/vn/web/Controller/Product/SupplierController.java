package hcmute.edu.vn.web.Controller.Product;

import hcmute.edu.vn.web.Entity.Product.Category;
import hcmute.edu.vn.web.Entity.Product.Supplier;
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
@RequestMapping("/api/v1/admin/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @Operation(summary = "Create a new supplier", description = "Creates a new supplier in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.createSupplier(supplier));
    }

    @Operation(summary = "Get a supplier by ID", description = "Retrieves a supplier by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier found"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @GetMapping("/{supplierId}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable String supplierId) {
        return supplierService.getSupplierById(supplierId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all suppliers", description = "Retrieves all suppliers")
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @Operation(summary = "Search suppliers by name", description = "Searches suppliers by name (case-insensitive)")
    @GetMapping("/search")
    public ResponseEntity<List<Supplier>> searchSuppliersByName(@RequestParam String name) {
        return ResponseEntity.ok(supplierService.searchSuppliersByName(name));
    }

    @Operation(summary = "Update a supplier", description = "Updates an existing supplier by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @PutMapping("/{supplierId}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable String supplierId, @RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.updateSupplier(supplierId, supplier));
    }

    @Operation(summary = "Delete a supplier", description = "Deletes a supplier by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @DeleteMapping("/{supplierId}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable String supplierId) {
        try {
            supplierService.deleteSupplier(supplierId);
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
    public ResponseEntity<List<Supplier>> createBulkSupplier(
            @Valid @RequestBody List<Supplier> discounts) {

        List<Supplier> savedDiscounts = supplierService.saveAll(discounts);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDiscounts);
    }

}