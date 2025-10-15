package com.example.storemanager.service;

import com.example.storemanager.dto.AuthRequest;
import com.example.storemanager.dto.AuthResponse;
import com.example.storemanager.entity.User;
import com.example.storemanager.repository.UserRepository;
import com.example.storemanager.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
    }
}
