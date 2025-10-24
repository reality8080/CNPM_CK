package com.example.spring_boot.services;

import com.example.spring_boot.domains.User;
import com.example.spring_boot.repository.UserRepository;
import com.example.spring_boot.utils.HashPassword;
import com.example.spring_boot.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepo;
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final HashPassword hashPassword;

    public AuthService(UserRepository userRepo, MongoTemplate mongoTemplate, UserService userService, HashPassword hashPassword) {
        this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
        this.userService = userService;
        this.hashPassword = hashPassword;
    }

    public User login(Map<String, Object> credentials) {
        long startTime = System.currentTimeMillis();
        log.info("🔐 [AUTH] Attempting login with credentials: {}", credentials);

        try {
            String emailOrPhone = (String) credentials.get("emailOrPhone");
            String password = (String) credentials.get("password");
            Boolean rememberMe = (Boolean) credentials.getOrDefault("rememberMe", false);

            if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
                log.error("Email or phone is null or empty");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone is required");
            }
            if (password == null || password.trim().isEmpty()) {
                log.error("Password is null or empty");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
            }

            String normalized = ValidationUtils.normalize(emailOrPhone);
            log.debug("Normalized emailOrPhone: {}", normalized);

            Query query = new Query();
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("email").is(normalized),
                    Criteria.where("phone").is(normalized)
            ));

            String hashedPassword = hashPassword.hashPasswordMD5(password);  // Hash input password
            query.addCriteria(Criteria.where("password").is(hashedPassword));
            log.debug("Executing query: {}", query);

            User user = mongoTemplate.findOne(query, User.class);
            if (user == null) {
                log.warn("No user found for email/phone: {}", normalized);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            String newRefreshToken = UUID.randomUUID().toString();
            user.setRefreshToken(newRefreshToken);
            user.setUpdatedAt(Instant.now());
            if (rememberMe) {
                user.setRefreshTokenExpiry(Instant.now().plusSeconds(30 * 24 * 60 * 60)); // 30 days
            } else {
                user.setRefreshTokenExpiry(Instant.now().plusSeconds(24 * 60 * 60)); // 1 day
            }
            log.debug("Saving user with refreshToken: {}", newRefreshToken);
            userRepo.save(user);

            if (user.getRole() == null && user.getRoleId() != null) {
                log.debug("Populating role for user: {}", user.getId());
                userService.batchPopulateRoles(List.of(user));
            }

            long endTime = System.currentTimeMillis();
            log.info("✅ [AUTH] Login successful for user {} in {}ms", user.getId(), endTime - startTime);
            return user;

        } catch (ResponseStatusException e) {
            log.warn("❌ [AUTH] Login failed: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            log.error("❌ [AUTH] Unexpected error during login", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed: " + e.getMessage());
        }
    }

    public User getUserByRefreshToken(String token) {
        long startTime = System.currentTimeMillis();
        log.info("🔑 [AUTH] Verifying refresh token: {}", token);

        try {
            if (token == null || token.trim().isEmpty()) {
                log.error("Token is null or empty");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is required");
            }

            Query query = new Query(Criteria.where("refreshToken").is(token));
            User user = mongoTemplate.findOne(query, User.class);

            if (user == null) {
                log.warn("No user found for token: {}", token);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }

            if (user.getRefreshTokenExpiry() != null && user.getRefreshTokenExpiry().isBefore(Instant.now())) {
                log.warn("Token expired for user: {}", user.getId());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
            }

            if (user.getRole() == null && user.getRoleId() != null) {
                log.debug("Populating role for user: {}", user.getId());
                userService.batchPopulateRoles(List.of(user));
            }

            long endTime = System.currentTimeMillis();
            log.info("✅ [AUTH] Token verified for user {} in {}ms", user.getId(), endTime - startTime);
            return user;

        } catch (ResponseStatusException e) {
            log.warn("❌ [AUTH] Token verification failed: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            log.error("❌ [AUTH] Unexpected error verifying token", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Token verification failed: " + e.getMessage());
        }
    }
}