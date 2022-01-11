package crypto.labs567.service;

import crypto.labs567.properties.CryptoProperties;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

@Service
public class CommonPasswordService {
    private final HashSet<String> commonPasswords = new HashSet<>();
    private final CryptoProperties cryptoProperties;

    public CommonPasswordService(CryptoProperties cryptoProperties) {
        this.cryptoProperties = cryptoProperties;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        Files.lines(Paths.get(cryptoProperties.getCommonPasswordsFileName()))
                .forEach(commonPasswords::add);
    }

    public boolean isCommon(String password) {
        return commonPasswords.contains(password);
    }
}
