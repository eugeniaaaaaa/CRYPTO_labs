package crypto.labs567.service;

import crypto.labs567.converters.AttributeEncryptor;
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
    private final AttributeEncryptor encryptor;

    public UserPersistenceService(PasswordEncoder encoder, UserRepository userRepository, AttributeEncryptor encryptor) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.encryptor = encryptor;
    }

    public void saveUser(UserRegistrationDto userDto) throws DataIntegrityViolationException {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPasswordEncoded(encoder.encode(userDto.getPassword()));
        user.setSalt(encryptor.randomSalt());
        user.setPhoneNumber(encryptor.encrypt(userDto.getPhoneNumber(), user.getSalt()));
        userRepository.save(user);
    }

    public UserInfoDto getUserInfo(String username) {
        User user = userRepository.findUserByUsername(username);
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setFirstName(user.getFirstName());
        userInfoDto.setLastName(user.getLastName());
        userInfoDto.setPhoneNumber(encryptor.decrypt(user.getPhoneNumber(), user.getSalt()));
        return userInfoDto;
    }
}
