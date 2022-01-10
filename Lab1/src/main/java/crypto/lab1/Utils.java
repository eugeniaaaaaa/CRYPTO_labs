package crypto.lab1;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class Utils {
    // According to the Concise Oxford Dictionary
    public static final String symbolFrequenciesAscending = " EARIOTNSLCUDPMHGBFYWKVXZJQ".toLowerCase();

    public static boolean textMakesSense(String text) {
        // I suppose, something more sophisticated is needed here
        final String[] actualEnglishWords = {"snowman", "simple"};
        return Stream.of(actualEnglishWords).anyMatch(text.toLowerCase()::contains);
    }

    /**
     * Encodes text with a single-byte key
     */
    public static byte[] encode(String text, int key) {
        return encode(text.getBytes(StandardCharsets.UTF_8), key);
    }

    public static byte[] encode(byte[] textBytes, int key) {
        for (int i = 0; i < textBytes.length; i++) {
            textBytes[i] ^= key;
        }
        return textBytes;
    }

    /**
     * Encode using repeating-key xor cipher
     */
    public static byte[] encode(String text, String key) {
        return encode(text.getBytes(), key.getBytes());
    }

    public static byte[] encode(byte[] textBytes, byte[] keyBytes) {
        final int textLen = textBytes.length;
        final int keyLen = keyBytes.length;
        int i = 0;
        while (i < textLen) {
            for (int j = 0; i < textLen && j < keyLen; i++, j++) {
                textBytes[i] ^= keyBytes[j];
            }
        }

        return textBytes;
    }

    public static byte mostFrequentByte(byte[] bytes) {
        final int[] letterFrequencies = new int[256]; // Byte range -> (-128, 127)
        for (byte b : bytes) {
            letterFrequencies[b - Byte.MIN_VALUE]++;
        }
        int maxIndex = 0;
        for (int i = 1; i < 256; i++) {
            if (letterFrequencies[i] > letterFrequencies[maxIndex]) {
                maxIndex = i;
            }
        }

        return (byte) (maxIndex + Byte.MIN_VALUE);
    }

    public static double randomEqualProb(byte[] bytes, int N) {
        final int[] symbolCounts = new int[256];
        for (int i = 0; i < N; i++) {
            symbolCounts[bytes[i] - Byte.MIN_VALUE]++;
        }
        // Index of coincidence
        double I = 0;
        for (int i = 0; i < 256; i++) {
            I += symbolCounts[i] * (symbolCounts[i] - 1) / (double) (N * (N - 1));
        }

        return I;
    }

    private Utils() {
    }
}
