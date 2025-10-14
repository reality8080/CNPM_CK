package vn.iotstar.config;




import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Hidden
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().permitAll() // Cho phép tất cả request, không cần đăng nhập
                )
                .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu không dùng form
                .httpBasic(httpBasic -> httpBasic.disable()); // Tắt xác thực HTTP Basic

        return http.build();
    }
}
