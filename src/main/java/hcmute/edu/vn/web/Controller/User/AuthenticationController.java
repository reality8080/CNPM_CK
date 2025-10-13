package hcmute.edu.vn.web.Controller.User;

import hcmute.edu.vn.web.DTO.*;
import hcmute.edu.vn.web.Entity.User.Admin;
import hcmute.edu.vn.web.Service.User.IUserService;
import hcmute.edu.vn.web.Service.implement.User.AdminService;
import hcmute.edu.vn.web.Service.implement.User.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final hcmute.edu.vn.web.Service.JwtService jwtService;
    // Cần cho logic quản lý Refresh Token
    private final AdminService adminService;
    private final EmployeeService employeeService;

    private IUserService<?> getService(String email) {
        if (adminService.findByEmail(email).isPresent()) {
            return adminService;
        }
        if (employeeService.findByEmail(email).isPresent()) {
            return employeeService;
        }
        return null;
    }

    // ================================= LOGIN =================================
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        // 1. Xác thực người dùng (sử dụng AuthenticationManager)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Tải UserDetails
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // 3. Tạo Access Token và Refresh Token
        final String accessToken = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        // 4. LƯU BỔ SUNG: Lưu Refresh Token vào DB
        IUserService<?> userService = getService(request.getEmail());
        if (userService != null) {
            userService.saveRefreshToken(request.getEmail(), refreshToken);
        } else {
            // Rất hiếm khi xảy ra nếu xác thực thành công nhưng không tìm thấy Service
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 5. Trả về token
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Login successful")
                .build());
    }

    // ================================= REFRESH TOKEN =================================
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        final String refreshToken = request.getRefreshToken();

        // 1. Trích xuất email từ refresh token
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse.builder()
                    .message("Invalid refresh token format.")
                    .build());
        }

        // 2. Tải UserDetails
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        IUserService<?> userService = getService(userEmail);

        // 3. Kiểm tra tính hợp lệ của token (chữ ký, thời hạn) VÀ khớp với token trong DB
        if (userService != null
                && jwtService.isTokenValid(refreshToken, userDetails)
                && userService.isRefreshTokenValid(userEmail, refreshToken)) {

            // 4. Tạo Access Token và Refresh Token mới
            final String newAccessToken = jwtService.generateToken(userDetails);
            final String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            // 5. Cập nhật Refresh Token mới vào DB
            userService.saveRefreshToken(userEmail, newRefreshToken);

            // 6. Trả về token mới
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .message("Token refreshed successfully.")
                    .build());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse.builder()
                .message("Invalid or expired refresh token. Please log in again.")
                .build());
    }

    // ================================= RESET PASSWORD =================================

    // BỔ SUNG: API Yêu cầu Reset Password (Gửi OTP)
    @PostMapping("/reset-password/request")
    public ResponseEntity<String> requestResetPassword(@RequestParam String email) {
        try {
            IUserService<?> userService = getService(email);
            if (userService instanceof AdminService adminService) {
                adminService.generateResetTokenAndSendEmail(email);
            } else {
                // **LƯU Ý:** Bạn cần thêm logic tương tự vào EmployeeService nếu muốn Employee dùng tính năng này
                return ResponseEntity.badRequest().body("Tính năng reset password chưa được hỗ trợ cho loại người dùng này.");
            }
            return ResponseEntity.ok("Mã xác thực đã được gửi đến email của bạn.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // BỔ SUNG: API Xác nhận Reset Password (Dùng OTP)
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmResetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            IUserService<?> userService = getService(request.getEmail());
            if (userService instanceof AdminService adminService) {
                adminService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
            } else {
                // **LƯU Ý:** Bạn cần thêm logic tương tự vào EmployeeService nếu muốn Employee dùng tính năng này
                return ResponseEntity.badRequest().body("Tính năng reset password chưa được hỗ trợ cho loại người dùng này.");
            }
            return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    @PostMapping("/register/request")
    public ResponseEntity<String> requestRegistration(@RequestBody RegistrationRequest request) {
        try {
            // Kiểm tra xem email đã tồn tại chưa
            if (getService(request.getEmail()) != null) {
                return ResponseEntity.badRequest().body("Email already exists.");
            }

            // Lưu tạm thông tin đăng ký (có thể dùng Redis hoặc DB tạm thời)
            // Giả định AdminService có phương thức để lưu tạm và gửi OTP
            if (adminService instanceof AdminService adminServiceImpl) {
                adminServiceImpl.generateRegistrationTokenAndSendEmail(request.getEmail(), request);
                return ResponseEntity.ok("OTP sent to your email.");
            } else {
                return ResponseEntity.badRequest().body("Registration not supported for this user type.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("System error: " + e.getMessage());
        }
    }

    // BỔ SUNG: API Xác nhận Đăng ký (Dùng OTP)
    @PostMapping("/register/confirm")
    public ResponseEntity<AuthenticationResponse> confirmRegistration(@RequestBody ResetPasswordRequest request) {
        try {
            if (adminService instanceof AdminService adminServiceImpl) {
                // Xác nhận OTP và tạo Admin mới
                Admin newAdmin = adminServiceImpl.confirmRegistration(request.getEmail(), request.getOtp());

                // Tạo token sau khi đăng ký thành công
                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                String accessToken = jwtService.generateToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                adminServiceImpl.saveRefreshToken(request.getEmail(), refreshToken);

                return ResponseEntity.ok(AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .message("Registration successful.")
                        .build());
            } else {
                return ResponseEntity.badRequest().body(AuthenticationResponse.builder()
                        .message("Registration not supported for this user type.")
                        .build());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthenticationResponse.builder()
                    .message(e.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AuthenticationResponse.builder()
                    .message("System error: " + e.getMessage())
                    .build());
        }
    }
}