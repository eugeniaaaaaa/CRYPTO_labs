package crypto.lab2;

import crypto.lab1.Task2;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task1 {
    public static void main(String[] args) {
        List<String> cipheredText = cipheredText();
        List<String> names = names();

        cipheredTextBytes(cipheredText)
                .collect(Collectors.groupingBy(bytes -> bytes.length)).values().stream()
                .sorted(Comparator.comparingInt(List<byte[]>::size).reversed())
                .parallel()
                .map(lst -> lst.toArray(new byte[0][]))
                .map(Task1::transpose)
                .map(parts -> Task2.crack(parts, parts.length, s -> containsName(s, names)))
                .forEach(System.out::println);

    }

    private static byte[][] transpose(byte[][] bytes) {
        byte[][] result = new byte[bytes[0].length][bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < bytes[0].length; j++) {
                result[j][i] = bytes[i][j];
            }
        }

        return result;
    }

    private static int[] xor(byte[] a, byte[] b) {
        int minLen = Math.min(a.length, b.length);
        int[] result = new int[minLen];

        for (int i = 0; i < minLen; i++) {
            result[i] = (a[i] ^ b[i]);
        }

        return result;
    }

    private static boolean containsName(String text, List<String> names) {
        String upperText = text.toUpperCase();
        return names.stream().anyMatch(upperText::contains);
    }

    private static Stream<byte[]> cipheredTextBytes(List<String> cipheredText) {
        return cipheredText.stream().map(String::getBytes);
    }

    private static List<String> cipheredText() {
        return resourceLines("encrypted-text.txt").collect(Collectors.toList());
    }

    private static List<String> names() {
        return resourceLines("english-names.txt").map(String::toUpperCase).collect(Collectors.toList());
    }

    @SneakyThrows
    private static Stream<String> resourceLines(String resourceName) {
        return Files.lines(Paths.get(Objects.requireNonNull(Task1.class.getClassLoader().getResource(resourceName)).getPath()));
    }
}
