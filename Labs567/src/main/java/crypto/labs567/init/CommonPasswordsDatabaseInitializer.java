package crypto.labs567.init;

import crypto.labs567.repository.CommonPasswordsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class CommonPasswordsDatabaseInitializer implements CommandLineRunner {
    private final CommonPasswordsRepository commonPasswordsRepository;

    public CommonPasswordsDatabaseInitializer(CommonPasswordsRepository commonPasswordsRepository) {
        this.commonPasswordsRepository = commonPasswordsRepository;
    }

    @Override
    public void run(String... args) {
        if (!commonPasswordsRepository.tableExists()) {
            commonPasswordsRepository.init();
        }
    }
}
