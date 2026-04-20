package user_service.dto;

import lombok.*;

/**
 * AuthResponse - returned after successful login/register
 * Contains JWT token + user details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;           // JWT token
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String message;
}