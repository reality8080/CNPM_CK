package hcmute.edu.vn.web.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì sử dụng JWT (stateless)

                // Cấu hình phân quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập công khai (ví dụ: API đăng nhập/đăng ký)
                        .requestMatchers("/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/views/*.html",
                                "/js/**",
                                "/css/**",
                                "/favicon.ico", // Added for favicon
                                "/images/**").permitAll()

                        .requestMatchers("/views/**").permitAll()

                        // Yêu cầu quyền ADMIN cho các API quản lý Employee và bản thân Admin
                        .requestMatchers(
                                "/api/v1/admin/**").hasAuthority("ADMIN")
                        // Bảo vệ mọi endpoint khác
                        .anyRequest().authenticated()
                )

                // Cấu hình quản lý phiên (session)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Vô trạng thái (Stateless)
                )

                // Thiết lập Authentication Provider
                .authenticationProvider(authenticationProvider)

                // Thêm JWT filter vào chuỗi xử lý
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}