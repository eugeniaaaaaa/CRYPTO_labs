package crypto.labs567.dto;

import crypto.labs567.validation.NotCommonPassword;
import crypto.labs567.validation.QuiteStrongPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserRegistrationDto {
    @Email
    private String email;
    @Size(min = 4, message = "Too short username")
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @QuiteStrongPassword
    @NotCommonPassword
    private String password;
}
