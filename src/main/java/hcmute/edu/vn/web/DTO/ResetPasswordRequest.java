package hcmute.edu.vn.web.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
