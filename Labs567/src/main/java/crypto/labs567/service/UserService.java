package crypto.labs567.service;

import crypto.labs567.repository.UserRepository;
import crypto.labs567.service.security.UserPrincipal;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return Stream.concat(Stream.of(usernameOrEmail)
                                .filter(EmailValidator.getInstance()::isValid)
                                .map(userRepository::findUserByEmail),
                        Stream.of(usernameOrEmail)
                                .map(userRepository::findUserByUsername))
                .filter(Objects::nonNull)
                .map(UserPrincipal::new)
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user " + usernameOrEmail));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(); // Suppose that default options are enough
    }
}
