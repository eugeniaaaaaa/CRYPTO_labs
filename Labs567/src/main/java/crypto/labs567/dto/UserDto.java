package crypto.labs567.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDto {
    @Email
    private String email;
    @Size(min = 4, message = "Too short username")
    private String username;
    private String firstName;
    private String lastName;
    @Size(min = 8, message = "Password must be at least 8 symbols long")
    private String password;
}
