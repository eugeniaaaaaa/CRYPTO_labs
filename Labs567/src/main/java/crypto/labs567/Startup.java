package crypto.labs567;

import crypto.labs567.properties.CryptoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CryptoProperties.class)
public class Startup {
    public static void main(String[] args) {
        SpringApplication.run(Startup.class);
    }
}
