package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user_service.entity.User;

import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // ✅ Find user by password reset token
    Optional<User> findByPasswordResetToken(String token);

    // ✅ Find user by OAuth provider + ID
    Optional<User> findByOauthProviderAndOauthId(String provider, String oauthId);
}
