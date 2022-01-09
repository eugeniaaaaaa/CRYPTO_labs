package crypto.lab1;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

public class Task1 {
    // According to the Concise Oxford Dictionary
    private static final char[] symbolFrequenciesAscending = " EARIOTNSLCUDPMHGBFYWKVXZJQ".toLowerCase().toCharArray();

    public static void main(String[] args) {
        final String text = "Now the first one who will post the link to this document to our chat will receive +1 score. Simple.";
        System.out.println(crack(encode(text, 35))
                .orElseThrow(() -> new IllegalStateException("Cannot crack such an easy code")));
    }

    private static Optional<String> crack(byte[] encoded) {
        final int[] letterFrequencies = new int[256]; // Byte range -> (-128, 127)
        for (byte b : encoded) {
            letterFrequencies[b - Byte.MIN_VALUE]++;
        }
        int maxIndex = 0;
        for (int i = 1; i < 256; i++) {
            if (letterFrequencies[i] > letterFrequencies[maxIndex]) {
                maxIndex = i;
            }
        }

        byte maxIndexByte = (byte) (maxIndex + Byte.MIN_VALUE);

        for (char reallyFrequentSymbol : symbolFrequenciesAscending) {
            // Suppose that the most frequent symbol in text is whitespace
            int key = maxIndexByte ^ (int) reallyFrequentSymbol;
            // (a xor b) xor b == a, so we can use encode function for decoding
            String decodedText = new String(encode(new String(encoded), key));
            if (textMakesSense(decodedText)) {
                // Actually, at this point we should check if the text makes any sense
                // And if not - compute the key again using the second most frequently use
                return Optional.of(decodedText);
            }
        }
        return Optional.empty();
    }

    private static boolean textMakesSense(String text) {
        // I suppose, something more sophisticated is needed here
        final String[] actualEnglishWords = {"snowman", "simple"};
        return Stream.of(actualEnglishWords).anyMatch(text.toLowerCase()::contains);
    }

    /**
     * Encodes text with a single-byte key
     */
    private static byte[] encode(String text, int key) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= key;
        }
        return bytes;
    }
}
