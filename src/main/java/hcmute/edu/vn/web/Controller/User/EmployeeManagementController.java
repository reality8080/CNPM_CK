package hcmute.edu.vn.web.Controller.User;

import hcmute.edu.vn.web.Entity.User.Employee;
import hcmute.edu.vn.web.Service.implement.User.EmployeeService;
import jakarta.validation.Valid; // Cần import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/employees")
@RequiredArgsConstructor
public class EmployeeManagementController {

    private final EmployeeService employeeService;

    // [C]reate - Thêm Employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        // Cần đảm bảo rằng Role được set là "EMPLOYEE" trong Service
        try {
            Employee savedEmployee = employeeService.save(employee);
            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Xử lý lỗi trùng email, thiếu trường...
        }
    }

    // [R]ead All - Lấy danh sách Employee
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.findAll();
        return ResponseEntity.ok(employees);
    }

    // [R]ead One - Lấy chi tiết Employee
    @GetMapping("/{email}")
    public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {
        return employeeService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // [U]pdate - Cập nhật thông tin Employee
    @PutMapping("/{email}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String email,
                                                   @Valid @RequestBody Employee employeeDetails) { // BỔ SUNG: @Valid
        return employeeService.findByEmail(email).map(existingEmployee -> {

            // Cập nhật các trường (Service sẽ lo phần mã hóa mật khẩu)
            if (employeeDetails.getPassword() != null && !employeeDetails.getPassword().isEmpty()) {
                existingEmployee.setPassword(employeeDetails.getPassword());
            }
            if (employeeDetails.getDepartment() != null) {
                existingEmployee.setDepartment(employeeDetails.getDepartment());
            }

            // Admin có thể thay đổi Role và Active status
            existingEmployee.setRole(employeeDetails.getRole());
            existingEmployee.setActive(employeeDetails.isActive());

            // Cập nhật UserProfile
            if (employeeDetails.getUserProfile() != null) {
                // Giả định: saveUserProfile sẽ trả về UserProfile đã có ID (nếu chưa có thì tạo mới)
                existingEmployee.setUserProfile(employeeService.saveUserProfile(employeeDetails.getUserProfile()));
            }

            Employee updatedEmployee = employeeService.save(existingEmployee);
            return ResponseEntity.ok(updatedEmployee);
        }).orElse(ResponseEntity.notFound().build());
    }

    // [D]elete - Xóa Employee
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        // Kiểm tra xem Employee có tồn tại trước khi xóa (hoặc để Service ném exception)
        if (employeeService.findByEmail(email).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        employeeService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seed")
    public ResponseEntity<List<Employee>> seedEmployees(@RequestBody List<Employee> employees) {
        List<Employee> savedEmployees = new ArrayList<>();
        List<String> failedEmails = new ArrayList<>();

        for (Employee employee : employees) {
            try {
                // Đặt lại Role phòng trường hợp Client quên đặt
                employee.setRole("Employee");
                // Sử dụng hàm save() hiện có (đã xử lý mã hóa mật khẩu)
                Employee savedEmployee = employeeService.save(employee);
                savedEmployees.add(savedEmployee);
            } catch (Exception e) {
                // Xử lý lỗi trùng lặp hoặc lỗi validation
                failedEmails.add(employee.getEmail() + ": " + e.getMessage());
                // Tiếp tục với bản ghi tiếp theo
            }
        }

        if (savedEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Trả về danh sách nhân viên đã được lưu
        // Có thể bổ sung thông báo lỗi nếu cần
        if (!failedEmails.isEmpty()) {
            System.err.println("Failed to seed employees: " + failedEmails);
        }

        return new ResponseEntity<>(savedEmployees, HttpStatus.CREATED);
    }
}