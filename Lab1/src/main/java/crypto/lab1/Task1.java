package crypto.lab1;

import java.util.Optional;

public class Task1 {
    public static void main(String[] args) {
        final String text = "Now the first one who will post the link to this document to our chat will receive +1 score. Simple.";
        System.out.println(crack(Utils.encode(text, 35))
                .orElseThrow(() -> new IllegalStateException("Cannot crack such an easy code")));
    }

    private static Optional<String> crack(byte[] encoded) {
        final byte mostFrequentByte = Utils.mostFrequentByte(encoded);

        for (char reallyFrequentSymbol : Utils.symbolFrequenciesAscending.toCharArray()) {
            // Suppose that the most frequent symbol in text is whitespace
            int key = mostFrequentByte ^ (int) reallyFrequentSymbol;
            // (a xor b) xor b == a, so we can use encode function for decoding
            String decodedText = new String(Utils.encode(new String(encoded), key));
            if (Utils.textMakesSense(decodedText)) {
                // Actually, at this point we should check if the text makes any sense
                // And if not - compute the key again using the second most frequently use
                return Optional.of(decodedText);
            }
        }
        return Optional.empty();
    }
}
