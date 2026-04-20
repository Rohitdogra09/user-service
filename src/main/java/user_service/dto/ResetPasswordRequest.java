package user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * ResetPasswordRequest - token from email + new password
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Token required")
    private String token;           // Token sent via email

    @NotBlank(message = "New password required")
    private String newPassword;
}
