package crypto.lab4;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordGenerator implements AutoCloseable {
    private static final int top100PasswordRate = 10;
    private static final int top100kPasswordRate = 85;
    private static final int randomPasswordRate = 5;
    private static final double totalRate = top100PasswordRate + top100kPasswordRate + randomPasswordRate;

    private final Random random = new Random();
    private List<String> top100Passwords;
    private List<String> top100kPasswords;

    public PasswordGenerator() {
        top100Passwords = resourceLines("top-100-most-common-passwords.txt");
        top100kPasswords = resourceLines("top-100k-most-common-passwords.txt");
    }

    public String generatePassword() {
        double randomValue = random.nextDouble();
        String password;
        if (randomValue < top100PasswordRate / totalRate) {
            password = passwordFromTop100(randomValue);
        } else if (randomValue < top100kPasswordRate / totalRate) {
            password = passwordFromTop100k(randomValue);
        } else {
            password = randomPassword();
        }
        return password;
    }


    private String passwordFromTop100(double randomValue) {
        double passwordIndexRate = randomValue / (top100PasswordRate / totalRate);
        int passwordIndex = (int) (100 * passwordIndexRate);
        return top100Passwords.get(passwordIndex);
    }

    private String passwordFromTop100k(double randomValue) {
        double passwordIndexRate = (randomValue - top100PasswordRate / totalRate) / (top100kPasswordRate / totalRate);
        int passwordIndex = (int) (999998 * passwordIndexRate);
        return top100kPasswords.get(passwordIndex);
    }

    @SneakyThrows
    private List<String> resourceLines(String resourceName) {
        return Files.readAllLines(Paths.get("data/" + resourceName));
    }

    private final int[] availableCharacters = IntStream.concat(
                    IntStream.concat(IntStream.range('A', 'Z'), IntStream.range('a', 'z')),
                    IntStream.range('0', '9'))
            .toArray();

    public String randomPassword() {
        return IntStream.range(0, random.nextInt(6) + 4)
                .mapToObj(i -> (char) availableCharacters[random.nextInt(availableCharacters.length)])
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    @Override
    public void close() throws Exception {
        top100Passwords = null;
        top100kPasswords = null;
    }
}
