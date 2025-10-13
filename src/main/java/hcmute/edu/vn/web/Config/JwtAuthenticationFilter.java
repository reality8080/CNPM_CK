package hcmute.edu.vn.web.Config;

import hcmute.edu.vn.web.Service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail; // Là email của Admin/Employee

        // 1. Kiểm tra header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // 2. Kiểm tra userEmail và Security Context
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Tải thông tin người dùng
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 3. Xác thực token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // THÊM ĐÂY: Extract authorities từ token claims (ưu tiên token, fallback UserDetails)
                List<GrantedAuthority> authorities = extractAuthoritiesFromToken(jwt, userDetails);

                // Tạo đối tượng xác thực với authorities từ token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities  // Sử dụng authorities từ token thay vì userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 4. Thiết lập người dùng vào Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    // THÊM ĐÂY: Method helper để extract authorities từ token claims
    private List<GrantedAuthority> extractAuthoritiesFromToken(String jwt, UserDetails userDetails) {
        // Extract claims từ token (sử dụng JwtService - giả sử bạn làm extractAllClaims public hoặc thêm method tương tự)
        Claims claims = jwtService.extractAllClaims(jwt);  // Nếu private, cần làm public hoặc thêm getClaimsPublic()

        // Lấy authorities từ claims (nếu có)
        @SuppressWarnings("unchecked")
        List<String> authoritiesList = (List<String>) claims.get("authorities");
        if (authoritiesList != null && !authoritiesList.isEmpty()) {
            // Convert string list thành GrantedAuthority list
            return authoritiesList.stream()
                    .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            // Fallback: Dùng từ UserDetails (DB) nếu token thiếu
            return (List<GrantedAuthority>) userDetails.getAuthorities();
        }
    }
}