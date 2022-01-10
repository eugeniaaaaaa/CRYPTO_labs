package crypto.lab3;

import crypto.lab3.remote.AccountInfo;
import crypto.lab3.remote.BetInfo;
import crypto.lab3.remote.GameMode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.text.MessageFormat.format;

public class RemoteService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String createAccountPattern;
    private final String playPattern;

    public RemoteService(String gameUrl, int playerId, GameMode gameMode) {
        this.createAccountPattern = gameUrl + format("/createacc?id={0}", String.valueOf(playerId));
        this.playPattern = gameUrl + format("/play{0}?id={1}&bet={2}&number={3}", gameMode, String.valueOf(playerId), "{0}", "{1}");
        restTemplate.setMessageConverters(getMessageConverters());
    }

    public AccountInfo createAccount() {
        return restTemplate.getForObject(createAccountPattern, AccountInfo.class);
    }

    public BetInfo play(long amountOfMoney, long number) {
        return restTemplate.getForObject(format(playPattern, String.valueOf(amountOfMoney), String.valueOf(number)), BetInfo.class);
    }

    private List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }
}
