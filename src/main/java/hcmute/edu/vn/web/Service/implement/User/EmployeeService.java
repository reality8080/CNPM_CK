package hcmute.edu.vn.web.Service.implement.User;

import hcmute.edu.vn.web.Entity.User.Employee;
import hcmute.edu.vn.web.Entity.User.UserProfile;
import hcmute.edu.vn.web.Repository.User.EmployeeRepository;
import hcmute.edu.vn.web.Repository.User.UserProfileRepository;
import hcmute.edu.vn.web.Service.User.IUserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IUserService<Employee> {

    private final EmployeeRepository employeeRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final GridFsTemplate gridFsTemplate;
    // ================= CRUD Employee =================

    // 1. Create (Admin)
    @Override
    public Employee save(Employee employee) {
        // 1. Xử lý mã hóa mật khẩu
        if (employee.getPassword() != null && !employee.getPassword().startsWith("$2a$")) { // Tránh mã hóa lại mật khẩu đã mã hóa
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        // 2. Thiết lập Role và thời gian
        employee.setRole("EMPLOYEE");
        // Giả định: Employee mới tạo sẽ active ngay hoặc chờ kích hoạt
        // employee.setActive(true);
        // employee.setCreatedAt(LocalDateTime.now()); // User.java dùng Date, nhưng dùng LocalDateTime thì tốt hơn

        // 3. Lưu UserProfile trước
        if (employee.getUserProfile() != null) {
            UserProfile savedProfile = userProfileRepository.save(employee.getUserProfile());
            employee.setUserProfile(savedProfile);
        }
        return employeeRepository.save(employee);
    }
    @Override
    public void saveRefreshToken(String email, String token) {
        findByEmail(email).ifPresent(employee -> {
            employee.setRefreshToken(token);
            employeeRepository.save(employee);
        });
    }

    @Override
    public boolean isRefreshTokenValid(String email, String token) {
        return findByEmail(email)
                .map(employee -> employee.getRefreshToken() != null && employee.getRefreshToken().equals(token))
                .orElse(false);
    }

    // 2. Read All (Admin)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // 3. Read One (Admin)
    @Override
    public Optional<Employee> findByEmail(String email) {
        // Vì EmployeeRepository chưa có findByEmail, ta giả sử có thể dùng findAll và lọc
        // hoặc bổ sung phương thức findByEmail vào EmployeeRepository
        // Hiện tại ta dùng hàm có sẵn:
        return employeeRepository.findById(email); // Giả định email là ID
    }

    // 4. Update (Admin) - Sử dụng save

    // 5. Delete (Admin)
    public void deleteByEmail(String email) {
        Optional<Employee> employeeOpt = employeeRepository.findById(email);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            // Xóa UserProfile liên quan nếu cần
            if (employee.getUserProfile() != null) {
                // BỔ SUNG: Lấy ObjectId và xóa ảnh GridFS liên quan
                ObjectId gridFsId = employee.getUserProfile().getProfilePictureGridFsId();
                if (gridFsId != null) {
                    gridFsTemplate.delete(new Query(Criteria.where("_id").is(gridFsId)));
                }
                userProfileRepository.deleteById(employee.getUserProfile().getId());
            }
            employeeRepository.delete(employee);
        }
    }

    // Lưu UserProfile
//    @Override
    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
}
