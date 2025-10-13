package hcmute.edu.vn.web.DTO;

import hcmute.edu.vn.web.Entity.User.UserProfile;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RegistrationRequest {
    private String email;
    private String password;
    private UserProfile userProfile;
    private Instant timestamp;
}
