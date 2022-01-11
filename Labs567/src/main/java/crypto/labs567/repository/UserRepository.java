package crypto.labs567.repository;

import crypto.labs567.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserByUsername(String username);
}
