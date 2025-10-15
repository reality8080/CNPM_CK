package hcmute.edu.vn.web.Controller.User;

import hcmute.edu.vn.web.Entity.User.Customer;
import hcmute.edu.vn.web.Service.implement.User.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // [C]reate - Thêm Customer (Public registration hoặc Admin)
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        // Cần đảm bảo rằng Role được set là "CUSTOMER" trong Service
        try {
            Customer savedCustomer = customerService.save(customer);
            return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Xử lý lỗi trùng email, thiếu trường...
        }
    }

    // [R]ead All - Lấy danh sách Customer (Admin)
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    // [R]ead One - Lấy chi tiết Customer
    @GetMapping("/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        return customerService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // [U]pdate - Cập nhật thông tin Customer
    @PutMapping("/{email}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String email,
                                                   @Valid @RequestBody Customer customerDetails) {
        return customerService.findByEmail(email).map(existingCustomer -> {

            // Cập nhật các trường (Service sẽ lo phần mã hóa mật khẩu)
            if (customerDetails.getPassword() != null && !customerDetails.getPassword().isEmpty()) {
                existingCustomer.setPassword(customerDetails.getPassword());
            }
            if (customerDetails.getFullname() != null) {
                existingCustomer.setFullname(customerDetails.getFullname());
            }

            // Admin có thể thay đổi Role nếu cần
            if (customerDetails.getRoleId() != null) {
                existingCustomer.setRoleId(customerDetails.getRoleId());
            }

            Customer updatedCustomer = customerService.save(existingCustomer);
            return ResponseEntity.ok(updatedCustomer);
        }).orElse(ResponseEntity.notFound().build());
    }

    // [D]elete - Xóa Customer (Admin)
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String email) {
        // Kiểm tra xem Customer có tồn tại trước khi xóa (hoặc để Service ném exception)
        if (customerService.findByEmail(email).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        customerService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seed")
    public ResponseEntity<List<Customer>> seedCustomers(@RequestBody List<Customer> customers) {
        List<Customer> savedCustomers = new ArrayList<>();
        List<String> failedEmails = new ArrayList<>();

        for (Customer customer : customers) {
            try {
                // Đặt lại Role phòng trường hợp Client quên đặt
                customer.setRoleId("CUSTOMER");
                // Sử dụng hàm save() hiện có (đã xử lý mã hóa mật khẩu)
                Customer savedCustomer = customerService.save(customer);
                savedCustomers.add(savedCustomer);
            } catch (Exception e) {
                // Xử lý lỗi trùng lặp hoặc lỗi validation
                failedEmails.add(customer.getEmail() + ": " + e.getMessage());
                // Tiếp tục với bản ghi tiếp theo
            }
        }

        if (savedCustomers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Trả về danh sách khách hàng đã được lưu
        // Có thể bổ sung thông báo lỗi nếu cần
        if (!failedEmails.isEmpty()) {
            System.err.println("Failed to seed customers: " + failedEmails);
        }

        return new ResponseEntity<>(savedCustomers, HttpStatus.CREATED);
    }
}