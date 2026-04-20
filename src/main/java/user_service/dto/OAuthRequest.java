package user_service.dto;

import lombok.*;

/**
 * OAuthRequest - data received from frontend after Google/GitHub login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthRequest {

    private String provider;      // "google" or "github"
    private String oauthId;       // Provider's user ID
    private String email;
    private String firstName;
    private String lastName;
}
