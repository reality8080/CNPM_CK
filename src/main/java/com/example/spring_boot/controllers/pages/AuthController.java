package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.domains.User;
import com.example.spring_boot.dto.ApiResponse;
import com.example.spring_boot.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;  // Thêm import này nếu cần
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Data
@Controller
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs cho đăng nhập và xác thực")
public class AuthController {

    private final AuthService authService;


    /**
     * Đăng nhập user
     * Test API:
     * - POST /api/auth/login
     * - Body: {"emailOrPhone":"test@example.com","password":"123456","rememberMe":true}
     */
    // AuthController.java
    @PostMapping("/login")
    @ResponseBody
    @Operation(summary = "Đăng nhập user")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> credentials, HttpSession session) {
        User user = authService.login(credentials);

        // Lưu vào session (nếu cần dùng sau)
        session.setAttribute("currentUser", user);
        session.setAttribute("refreshToken", user.getRefreshToken());

        // Xác định redirect URL theo role
        String roleName = (user.getRole() != null && user.getRole().getName() != null)
                ? user.getRole().getName().toUpperCase()
                : "USER";

        String redirectUrl = "ADMIN".equals(roleName) ? "/admin/dashboard" : "/";

        // Tạo response với redirectUrl
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("redirectUrl", redirectUrl);

        return ApiResponse.success(response, "Login successful");
    }

    /**
     * Lấy thông tin user hiện tại dựa trên refreshToken
     * Test API:
     * - GET /api/auth/me
     * - Header: Authorization: Bearer <refreshToken>
     */
    @GetMapping("/me")
    @ResponseBody  // <-- Thêm dòng này để trả JSON
    @Operation(summary = "Lấy thông tin user hiện tại")
    public ApiResponse<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        String token = authHeader.substring(7);
        User user = authService.getUserByRefreshToken(token);
        return ApiResponse.success(user, "User info retrieved");
    }
}