package hcmute.edu.vn.web.Service.implement.User;

import hcmute.edu.vn.web.DTO.RegistrationRequest;
import hcmute.edu.vn.web.Entity.User.Admin;
import hcmute.edu.vn.web.Entity.User.UserProfile;
import hcmute.edu.vn.web.Repository.User.AdminRepository;
import hcmute.edu.vn.web.Repository.User.UserProfileRepository;
import hcmute.edu.vn.web.Service.EmailService;
import hcmute.edu.vn.web.Service.User.IUserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminService implements IUserService<Admin> { // Giả định AdminService extends IUserService với các method cụ thể

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired // BỔ SUNG: Inject GridFsTemplate
    private GridFsTemplate gridFsTemplate;
    // Lưu trữ tạm cho OTP reset password (key: email, value: {otp, timestamp})
    private final ConcurrentHashMap<String, OtpData> resetOtpStorage = new ConcurrentHashMap<>();

    // Lưu trữ tạm cho registration (key: email:otp, value: request)
    private final ConcurrentHashMap<String, RegistrationRequest> registrationStorage = new ConcurrentHashMap<>();

    // Lưu refresh token (giả định lưu trong entity Admin, nhưng nếu cần map riêng thì dùng)
    // Ở đây giả định Admin entity có field refreshToken

    private static final long OTP_EXPIRY_MINUTES = 15; // Thời hạn OTP

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

//    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

//    @Override
    public Admin createAdmin(Admin admin) {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setActive(true);
        admin.setRole("ADMIN");
        admin.setCreatedAt(LocalDateTime.now());
        // Lưu UserProfile nếu có
        if (admin.getUserProfile() != null) {
            UserProfile savedProfile = userProfileRepository.save(admin.getUserProfile());
            admin.setUserProfile(savedProfile);
        }

        return adminRepository.save(admin);
    }

//    @Override
    public Optional<Admin> updateAdminDetails(String email, Admin adminDetails) {
        return findByEmail(email).map(admin -> {
            if (adminDetails.getPassword() != null && !adminDetails.getPassword().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
            }
//            if (adminDetails.isActive() != admin.isActive()) { // Giả định có setter cho active
//                admin.setActive(adminDetails.isActive());
//            }
            admin.setActive(true);
            return adminRepository.save(admin);
        });
    }

//    @Override
    public Optional<Admin> updateAdminUserProfile(String email, UserProfile userProfile) {
        return findByEmail(email).map(admin -> {
            UserProfile existingProfile = admin.getUserProfile();
            if (existingProfile == null) {
                existingProfile = new UserProfile();
            }
            // Cập nhật các trường của UserProfile
            existingProfile.setFullName(userProfile.getFullName());

            existingProfile.setAddress(userProfile.getAddress());

            existingProfile.setPhoneNumber(userProfile.getPhoneNumber());

            existingProfile.setDateOfBirth(userProfile.getDateOfBirth());

            UserProfile savedProfile = userProfileRepository.save(existingProfile);
            admin.setUserProfile(savedProfile);
            return adminRepository.save(admin);
        });
    }

//    @Override
    public void deleteByEmail(String email) {
        adminRepository.findByEmail(email).ifPresent(admin -> {
            // Xóa UserProfile nếu có
            if (admin.getUserProfile() != null) {
                // BỔ SUNG: Lấy ObjectId và xóa ảnh GridFS liên quan
                ObjectId gridFsId = admin.getUserProfile().getProfilePictureGridFsId();
                if (gridFsId != null) {
                    gridFsTemplate.delete(new Query(Criteria.where("_id").is(gridFsId)));
                }
                userProfileRepository.delete(admin.getUserProfile());
            }
            adminRepository.delete(admin);
        });
    }

    @Override
    public Admin save(Admin admin) {
        // Phương thức chung từ IUserService, có thể dùng để save/update
        if (admin.getPassword() != null && !admin.getPassword().startsWith("{bcrypt}")) { // Giả định prefix mã hóa
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        return adminRepository.save(admin);
    }

    @Override
    public void saveRefreshToken(String email, String refreshToken) {
        findByEmail(email).ifPresent(admin -> {
            admin.setRefreshToken(refreshToken); // Giả định Admin có field refreshToken
            adminRepository.save(admin);
        });
    }

    @Override
    public boolean isRefreshTokenValid(String email, String refreshToken) {
        return findByEmail(email)
                .map(admin -> refreshToken.equals(admin.getRefreshToken()))
                .orElse(false);
    }

//    @Override
    public void generateResetTokenAndSendEmail(String email) {
        if (findByEmail(email).isEmpty()) {
            throw new IllegalArgumentException("Email not found");
        }
        String otp = generateOtp();
        resetOtpStorage.put(email, new OtpData(otp, Instant.now()));

        String subject = "Reset Password OTP";
        String body = "Your OTP for password reset is: " + otp + ". It expires in " + OTP_EXPIRY_MINUTES + " minutes.";
        emailService.sendEmail(email, subject, body);
    }

//    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        OtpData otpData = resetOtpStorage.get(email);
        if (otpData == null || !otpData.otp.equals(otp) || Instant.now().isAfter(otpData.timestamp.plusSeconds(OTP_EXPIRY_MINUTES * 60))) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        findByEmail(email).ifPresent(admin -> {
            admin.setPassword(passwordEncoder.encode(newPassword));
            adminRepository.save(admin);
            resetOtpStorage.remove(email);
        });
    }

//    @Override
    public void generateRegistrationTokenAndSendEmail(String email, RegistrationRequest request) {
        String otp = generateOtp();
        String key = email + ":" + otp;
        request.setTimestamp(Instant.now()); // Set timestamp
        registrationStorage.put(key, request);

        String subject = "Registration OTP";
        String body = "Your OTP for registration is: " + otp + ". It expires in " + OTP_EXPIRY_MINUTES + " minutes.";
        emailService.sendEmail(email, subject, body);
    }

//    @Override
    public Admin confirmRegistration(String email, String otp) {
        String key = email + ":" + otp;
        RegistrationRequest regData = registrationStorage.get(key);
        if (regData == null || Instant.now().isAfter(regData.getTimestamp().plusSeconds(OTP_EXPIRY_MINUTES * 60))) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        Admin admin = new Admin();
        admin.setEmail(regData.getEmail());
        if (regData.getPassword() == null || regData.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required for registration");
        }
        admin.setPassword(passwordEncoder.encode(regData.getPassword()));
        admin.setActive(true);
        admin.setRole("ADMIN");

        if (regData.getUserProfile() != null) {
            UserProfile savedProfile = userProfileRepository.save(regData.getUserProfile());
            admin.setUserProfile(savedProfile);
        } else {
            throw new IllegalArgumentException("User profile is required for registration");
        }

        Admin savedAdmin;
        try {
            savedAdmin = adminRepository.save(admin);
        } catch (DataIntegrityViolationException e) {
            registrationStorage.remove(key);
            throw new IllegalArgumentException("Email already exists or database error: " + e.getMessage());
        }

        registrationStorage.remove(key);
        return savedAdmin;
    }

    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 999999));
    }

    // Inner classes for temp data
    private static class OtpData {
        String otp;
        Instant timestamp;

        OtpData(String otp, Instant timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
    public UserDetails loadUserByUsername(String email)  {
        // Tìm admin từ DB (giả sử Admin entity có role)
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // THÊM ĐÂY: Set authorities cho admin
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ADMIN"));

        return new org.springframework.security.core.userdetails.User(
                admin.getEmail(),
                admin.getPassword(),  // Encoded password
                authorities  // Pass authorities vào đây
        );
        // Hoặc nếu custom class: return new AdminUserDetails(admin, authorities);
    }
}