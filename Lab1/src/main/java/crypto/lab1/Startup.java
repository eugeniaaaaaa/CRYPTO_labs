package crypto.lab1;

import java.nio.charset.StandardCharsets;

public class Startup {
    public static void main(String[] args) {

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
