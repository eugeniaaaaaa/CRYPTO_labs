package crypto.labs567.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crypto")
@Getter
@Setter
public class CryptoProperties {
    private String commonPasswordsFileName;
    private String contentEncodingSecretKey;
}
