package user_service.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

/**
 * ForgotPasswordRequest - just needs email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @Email(message = "Valid email required")
    private String email;
}
