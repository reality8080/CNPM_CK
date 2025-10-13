package hcmute.edu.vn.web.Config;

import hcmute.edu.vn.web.Entity.User.User; // Giả định có interface/class User chung
import hcmute.edu.vn.web.Repository.User.AdminRepository;
import hcmute.edu.vn.web.Repository.User.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMongoAuditing
public class ApplicationConfig {

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;

    // Cấu hình UserDetailsService để tải UserDetails từ DB (Admin/Employee)
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User foundUser = null;

            // 1. Tìm trong Admin
            foundUser = adminRepository.findById(username).orElse(null);

            if (foundUser == null) {
                // 2. Nếu không phải Admin, tìm trong Employee (Giả định email là ID)
                foundUser = employeeRepository.findById(username).orElse(null);
            }

            if (foundUser == null) {
                throw new UsernameNotFoundException("User not found with email: " + username);
            }

            // Map Entity User sang Spring Security UserDetails
            return new org.springframework.security.core.userdetails.User(
                    foundUser.getEmail(),
                    foundUser.getPassword(),
                    foundUser.isActive(), // enabled/disabled
                    true, true, true,
                    List.of(new SimpleGrantedAuthority(foundUser.getRole()))
            );
        };
    }

    // Cấu hình AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Cấu hình AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Cấu hình PasswordEncoder (BCrypt được khuyến nghị)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}