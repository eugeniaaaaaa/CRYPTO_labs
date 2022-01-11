package crypto.labs567.domain;

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
    private String username;
    private String firstName;
    private String lastName;
    private String passwordEncoded;
    private String salt;
    @Embedded
    private UserRole role;

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
