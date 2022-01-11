package crypto.lab4;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PasswordGenerator {
    private static final int top100PasswordRate = 10;
    private static final int top100kPasswordRate = 85;
    private static final int randomPasswordRate = 5;
    private static final double totalRate = top100PasswordRate + top100kPasswordRate + randomPasswordRate;

    private final Random random = new Random();

    public String generatePassword() {
        double randomValue = random.nextDouble();

        if (randomValue < top100PasswordRate / totalRate) {
            return passwordFromTop100(randomValue);
        } else if (randomValue < top100kPasswordRate / totalRate) {
            return passwordFromTop100k(randomValue);
        } else {
            return randomPassword();
        }
    }


    private String passwordFromTop100(double randomValue) {
        double passwordIndexRate = randomValue / (top100PasswordRate / totalRate);
        int passwordIndex = (int) (999998 * passwordIndexRate);
        return resourceLines("top-100-most-common-passwords.txt").get(passwordIndex);
    }

    private String passwordFromTop100k(double randomValue) {
        double passwordIndexRate = (randomValue - top100PasswordRate / totalRate) / (top100kPasswordRate / totalRate);
        int passwordIndex = (int) (100 * passwordIndexRate);
        return resourceLines("top-100k-most-common-passwords.txt").get(passwordIndex);
    }

    @SneakyThrows
    private List<String> resourceLines(String resourceName) {
        return Files.readAllLines(Paths.get(Objects.requireNonNull(PasswordGenerator.class.getClassLoader()
                .getResource(resourceName)).toString()));
    }

    private String randomPassword() {
        byte[] bytes = new byte[random.nextInt(6) + 4]; // Random password between 4 and 10
        random.nextBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
