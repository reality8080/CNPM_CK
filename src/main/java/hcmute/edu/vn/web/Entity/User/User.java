package hcmute.edu.vn.web.Entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@NoArgsConstructor
@Data
@AllArgsConstructor
public abstract class User implements UserDetails {
    @Id
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Size(max = 500, message = "Refresh token must not exceed 500 characters")
    private String refreshToken;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(Admin|Employee|Customer)$", message = "Role must be either Admin, Employee, or Customer")
    private String role;

    @DBRef
    private UserProfile userProfile;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime upDate;

    @Size(max = 500, message = "Reset password token must not exceed 500 characters")
    private String resetPasswordToken;

    private LocalDateTime resetPasswordExpire;

    private boolean isActive = false;

}