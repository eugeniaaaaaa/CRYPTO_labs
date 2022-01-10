package crypto.lab3.remote;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BetInfo {
    private String message;
    private AccountInfo account;
    private long realNumber;
}
