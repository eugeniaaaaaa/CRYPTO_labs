import crypto.labs567.validation.QuiteStrongPasswordValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TestStartup {
    public static void main(String[] args) throws IOException {
        QuiteStrongPasswordValidator validator = new QuiteStrongPasswordValidator();
        List<String> strongPasswords = Files.lines(Paths.get("Lab4/data/top-100k-most-common-passwords.txt"))
                .filter(s -> validator.isValid(s, null))
                .limit(1000)
                .collect(Collectors.toList());

        Files.write(Paths.get("Labs567/data/most-common-passwords.txt"), strongPasswords);
    }
}
