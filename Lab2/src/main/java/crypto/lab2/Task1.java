package crypto.lab2;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Task1 {
    public static void main(String[] args) {
        List<String> cipheredText = cipheredText();

        cipheredTextBytes(cipheredText)
                .flatMap(bytes1 -> cipheredTextBytes(cipheredText).map(bytes2 -> xor(bytes1, bytes2)))
                .map(ints -> Arrays.stream(ints).map(Task1::nearestSymbol).toArray())
                .map(ints -> new String(ints, 0, ints.length))
                .forEach(System.out::println);
    }

    private static int[] xor(byte[] a, byte[] b) {
        int minLen = Math.min(a.length, b.length);
        int[] result = new int[minLen];

        for (int i = 0; i < minLen; i++) {
            result[i] = (a[i] ^ b[i]);
        }

        return result;
    }

    private static int nearestSymbol(int b) {
        return IntStream.concat(IntStream.range('A', 'Z'), IntStream.range('a', 'z'))
                .mapToObj(s -> new Object() {
                    final int symbol = s;
                    final int onesCount = onesCount((byte) (s ^ b));
                })
                .min(Comparator.comparingInt(p -> p.onesCount))
                .map(pair -> pair.symbol)
                .orElseThrow(IllegalAccessError::new);
    }

    private static int onesCount(byte b) {
        int onesCount = 0;
        while (b > 0) {
            if ((b & 1) > 0) {
                onesCount++;
            }
            b >>= 1;
        }
        return onesCount;
    }

    private static Stream<byte[]> cipheredTextBytes(List<String> cipheredText) {
        return cipheredText.stream().map(String::getBytes);
    }

    private static List<String> cipheredText() {
        return resourceLines("encrypted-text.txt");
    }

    private static List<String> englishNames() {
        return resourceLines("english-names.txt");
    }

    @SneakyThrows
    private static List<String> resourceLines(String resourceName) {
        return Files.lines(Paths.get(Objects.requireNonNull(Task1.class.getClassLoader().getResource(resourceName)).getPath()))
                .collect(Collectors.toList());
    }
}
