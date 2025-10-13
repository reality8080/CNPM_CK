package hcmute.edu.vn.web.Service.implement.Product;

import hcmute.edu.vn.web.Entity.Product.Product;
import hcmute.edu.vn.web.Entity.Product.Supplier;
import hcmute.edu.vn.web.Repository.Product.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductService productService;
    private final String[] supplierNames = {"Fashion King Co.", "Global Wear Ltd.", "Trendy Threads VN", "Casual Style Inc.", "Premium Garment Group"};

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Optional<Supplier> getSupplierById(String supplierId) {
        return supplierRepository.findById(supplierId);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Supplier> searchSuppliersByName(String name) {
        return supplierRepository.findBySupplierNameContainingIgnoreCase(name);
    }

    @Transactional
    public Supplier updateSupplier(String supplierId, Supplier updatedSupplier) {
        Optional<Supplier> existingSupplier = supplierRepository.findById(supplierId);
        if (existingSupplier.isPresent()) {
            updatedSupplier.setSupplierId(supplierId);
            return supplierRepository.save(updatedSupplier);
        } else {
            throw new RuntimeException("Supplier not found with ID: " + supplierId);
        }
    }

    @Transactional
    public void deleteSupplier(String supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new RuntimeException("Category not found with ID: " + supplierId);
        }

        // BƯỚC BỔ SUNG: Kiểm tra xem có Product nào đang sử dụng Category này không
        List<Product> linkedProducts = productService.getProductsByCategory(supplierId);
        if (!linkedProducts.isEmpty()) {
            // NÊN: Ném một exception rõ ràng để Controller trả về mã lỗi 409 Conflict
            throw new IllegalStateException("Cannot delete Category. " + linkedProducts.size() + " Product(s) are linked to it.");

            // HOẶC: (Nếu muốn xóa luôn)
            // linkedProducts.forEach(product -> productService.deleteProduct(product.getProductId()));
        }

        supplierRepository.deleteById(supplierId);
    }

    @Transactional
    public List<Supplier> seedSuppliers(int count) {
        List<Supplier> suppliers = new ArrayList<>();
        String[] supplierNames = {"Fashion King Co.", "Global Wear Ltd.", "Trendy Threads VN", "Casual Style Inc.", "Premium Garment Group"};

        for (int i = 1; i <= count; i++) {
            suppliers.add(Supplier.builder()
                    .supplierId(UUID.randomUUID().toString())
                    .supplierName(supplierNames[i % supplierNames.length] + " Supplier " + ((i / supplierNames.length) + 1))
                    .address("Số " + i * 10 + ", Đường Nguyễn Huệ, TP.HCM")
                    .phoneNumber("+84901" + String.format("%06d", i))
                    .email("supplier" + i + "@clothingstore.com")
                    .description("Nhà cung cấp chuyên đồ " + supplierNames[i % supplierNames.length].toLowerCase() + " và nguyên vật liệu dệt may.")
                    .status(i % 5 == 0 ? "Inactive" : "Active")
                    .build());
        }
        return supplierRepository.saveAll(suppliers);
    }

    // Bổ sung phương thức saveAll cho đồng bộ
    public List<Supplier> saveAll(List<Supplier> suppliers) {
        return supplierRepository.saveAll(suppliers);
    }

}