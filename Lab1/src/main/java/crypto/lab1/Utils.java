package crypto.lab1;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class Utils {
    // According to the Concise Oxford Dictionary
    public static final char[] symbolFrequenciesAscending = " EARIOTNSLCUDPMHGBFYWKVXZJQ".toLowerCase().toCharArray();

    public static boolean textMakesSense(String text) {
        // I suppose, something more sophisticated is needed here
        final String[] actualEnglishWords = {"snowman", "simple"};
        return Stream.of(actualEnglishWords).anyMatch(text.toLowerCase()::contains);
    }

    /**
     * Encodes text with a single-byte key
     */
    public static byte[] encode(String text, int key) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= key;
        }
        return bytes;
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

    private Utils() {
    }
}
