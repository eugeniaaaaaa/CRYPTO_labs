package crypto.labs567.service;

import crypto.labs567.repository.UserRepository;
import crypto.labs567.service.security.UserPrincipal;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {
    private final EmailValidator emailValidator = new EmailValidator();
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return Stream.concat(Stream.of(usernameOrEmail)
                                .filter(email -> emailValidator.isValid(email, null))
                                .map(userRepository::findUserByEmail),
                        Stream.of(usernameOrEmail)
                                .map(userRepository::findUserByUsername))
                .filter(Objects::nonNull)
                .map(UserPrincipal::new)
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user " + usernameOrEmail));
    }
}
