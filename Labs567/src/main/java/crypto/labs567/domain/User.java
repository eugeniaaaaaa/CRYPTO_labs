package crypto.labs567.domain;

import crypto.labs567.converters.AttributeEncryptor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "labs567_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true) // Because why not?
    @Convert(converter = AttributeEncryptor.class)
    private String username;
    @Convert(converter = AttributeEncryptor.class)
    private String firstName;
    @Convert(converter = AttributeEncryptor.class)
    private String lastName;
    @Convert(converter = AttributeEncryptor.class)
    private String phoneNumber;
    private String passwordEncoded;

    @Override
    public int hashCode() {
        return User.class.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }
}
