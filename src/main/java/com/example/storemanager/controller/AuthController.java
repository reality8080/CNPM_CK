package com.example.storemanager.controller;

import com.example.storemanager.dto.AuthRequest;
import com.example.storemanager.dto.AuthResponse;
import com.example.storemanager.entity.User;
import com.example.storemanager.repository.UserRepository;
import com.example.storemanager.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Sai tài khoản hoặc mật khẩu"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Mật khẩu không đúng");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole(), user.getId()));
    }
    @GetMapping("/login")
    public ResponseEntity<?> showLoginPage() {
        // Trả về file login.html từ thư mục static hoặc templates
        return ResponseEntity.status(302)
                .header("Location", "/login.html")
                .build();
    }

    // Kiểm tra server chạy OK
    @GetMapping("/test")
    public String test() {
        return "✅ AuthController hoạt động!";
    }
}
