package crypto.lab3;

import crypto.lab3.remote.AccountInfo;
import crypto.lab3.remote.BetInfo;
import crypto.lab3.remote.GameMode;
import org.springframework.web.client.RestTemplate;

import static java.text.MessageFormat.format;

public class RemoteService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String createAccountPattern;
    private final String playPattern;

    public RemoteService(String gameUrl, int playerId, GameMode gameMode) {
        this.createAccountPattern = gameUrl + format("/createacc?id={0}", playerId);
        this.playPattern = gameUrl + format("/play{0}?id={1}&bet={2}&number={3}", gameMode, playerId, "{0}", "{1}");
    }

    public AccountInfo createAccount() {
        return restTemplate.getForObject(createAccountPattern, AccountInfo.class);
    }

    public BetInfo play(long amountOfMoney, int number) {
        return restTemplate.getForObject(format(playPattern, amountOfMoney, number), BetInfo.class);
    }
}
