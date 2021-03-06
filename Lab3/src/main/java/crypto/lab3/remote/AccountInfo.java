package crypto.lab3.remote;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class AccountInfo {
    private int id;
    private long money;
    private ZonedDateTime deletionTime;
}
