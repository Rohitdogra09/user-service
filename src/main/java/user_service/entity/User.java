package user_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

/**
 * User Entity
 * Added: passwordResetToken, passwordResetExpiry for forgot password
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;           // ✅ Now stored as BCrypt hash

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    // ✅ For forgot password flow
    private String passwordResetToken;          // Random token sent via email
    private LocalDateTime passwordResetExpiry;  // Token expires after 15 mins

    // ✅ For OAuth (Google/GitHub login)
    private String oauthProvider;   // "google", "github", or null
    private String oauthId;         // Provider's user ID

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum Role {
        CUSTOMER, ADMIN, HOTEL_MANAGER
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (role == null) role = Role.CUSTOMER;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
