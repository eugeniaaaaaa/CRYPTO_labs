package crypto.labs567.service;

import crypto.labs567.domain.User;
import crypto.labs567.dto.UserInfoDto;
import crypto.labs567.dto.UserRegistrationDto;
import crypto.labs567.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserPersistenceService(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public void saveUser(UserRegistrationDto userDto) throws DataIntegrityViolationException {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPasswordEncoded(encoder.encode(userDto.getPassword()));
        user.setPhoneNumber(userDto.getPhoneNumber());
        userRepository.save(user);
    }

    public UserInfoDto getUserInfo(String username) {
        User user = userRepository.findUserByUsername(username);
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setFirstName(user.getFirstName());
        userInfoDto.setLastName(user.getLastName());
        userInfoDto.setPhoneNumber(user.getPhoneNumber());
        return userInfoDto;
    }
}
