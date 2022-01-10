package crypto.lab2;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class Task1 {
    public static void main(String[] args) {
        cipheredText().forEach(System.out::println);
    }

    @SneakyThrows
    private static Stream<String> cipheredText() {
        return Files.lines(Paths.get(Objects.requireNonNull(Task1.class.getClassLoader().getResource("encrypted-text.txt")).getPath()));
    }
}
