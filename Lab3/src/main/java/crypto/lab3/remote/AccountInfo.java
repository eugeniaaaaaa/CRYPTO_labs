package crypto.lab3.remote;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@Setter
@ToString
public class AccountInfo {
    private int id;
    private long money;
    private LocalTime deletionTime;
}
