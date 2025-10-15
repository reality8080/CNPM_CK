package hcmute.edu.vn.web.Service.implement.User;

import hcmute.edu.vn.web.Entity.User.Customer;
import hcmute.edu.vn.web.Repository.User.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    // ================= CRUD Customer =================

    // 1. Create (Public hoặc Admin)
    public Customer save(Customer customer) {
        // 1. Xử lý mã hóa mật khẩu
        if (customer.getPassword() != null && !customer.getPassword().startsWith("$2a$")) { // Tránh mã hóa lại mật khẩu đã mã hóa
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }

        // 2. Thiết lập Role và thời gian
        customer.setRoleId("CUSTOMER"); // Giả định roleId là "CUSTOMER"
        // Giả định: Customer mới tạo sẽ active ngay hoặc chờ kích hoạt
        // customer.setActive(true);
        // createdAt và updatedAt được xử lý tự động bởi @CreatedDate và @LastModifiedDate

        return customerRepository.save(customer);
    }

    public void saveRefreshToken(String email, String token) {
        findByEmail(email).ifPresent(customer -> {
            customer.setRefreshToken(token);
            customerRepository.save(customer);
        });
    }

    public boolean isRefreshTokenValid(String email, String token) {
        return findByEmail(email)
                .map(customer -> customer.getRefreshToken() != null && customer.getRefreshToken().equals(token))
                .orElse(false);
    }

    // 2. Read All (Admin)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    // 3. Read One (Admin hoặc Public với quyền)
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // 4. Update (Admin hoặc User tự cập nhật) - Sử dụng save

    // 5. Delete (Admin)
    public void deleteByEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            customerRepository.delete(customerOpt.get());
        }
    }
}