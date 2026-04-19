package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user_service.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    /**
     * Spring autogenerate this SQl;
     * select count(*) >0 from users where email =?
     * used to check duplicate emails before registration
     */

    boolean existsByEmail(String email);
}
