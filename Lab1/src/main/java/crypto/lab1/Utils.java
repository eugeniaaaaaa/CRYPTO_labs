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
        return null;
    }

    private Utils() {}
}
