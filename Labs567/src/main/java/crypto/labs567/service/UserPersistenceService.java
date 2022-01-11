package crypto.labs567.service;

import crypto.labs567.domain.User;
import crypto.labs567.dto.UserDto;
import crypto.labs567.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserPersistenceService(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public void saveUser(UserDto userDto) throws DataIntegrityViolationException {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setLastName(userDto.getLastName());
        user.setPasswordEncoded(encoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }
}
