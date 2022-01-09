package crypto.lab1;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Task3 {
    private static class EstimatedKey {
        double score;
        BidiMap<Character, Character> key;
    }

    private static final Random random = new Random();

    public static void main(String[] args) {
        final String text = "Now the first one who will post the link to this".toUpperCase();
        final BidiMap<Character, Character> key = randomSubstitutionKey();
        final String encodedText = encode(text, key);

        List<BidiMap<Character, Character>> initialKeys = Stream.generate(Task3::randomSubstitutionKey).limit(10).collect(Collectors.toList());
        List<EstimatedKey> estimatedKeys;
        for (int i = 0; i < 20; i++) {
            estimatedKeys = initialKeys.stream().map(k -> estimate(encodedText, k)).collect(Collectors.toList());
        }
    }

    public static EstimatedKey rouletteRandom(List<EstimatedKey> estimatedKeys) {
        final int randomResult = random.nextInt(100);
        final double totalScore = estimatedKeys.stream().mapToDouble(k -> k.score).sum();
        double range = 0;
        for (EstimatedKey estimatedKey : estimatedKeys) {
            range += 100 * (estimatedKey.score / totalScore);
            if (range > randomResult) {
                return estimatedKey;
            }
        }
        return estimatedKeys.get(estimatedKeys.size() - 1);
    }

    private static BidiMap<Character, Character> crossover(BidiMap<Character, Character> first,
                                                           BidiMap<Character, Character> second,
                                                           Map<Character, Integer> firstFrequencies,
                                                           Map<Character, Integer> secondFrequencies,
                                                           Map<Character, Integer> actualFrequencies) {
        return first;
    }

    private static EstimatedKey estimate(String encodedText, BidiMap<Character, Character> key) {
        EstimatedKey estimatedKey = new EstimatedKey();
        estimatedKey.key = key;
        estimatedKey.score = score(encodedText, key);
        return estimatedKey;
    }

    private static double score(String encodedText, BidiMap<Character, Character> key) {
        final int N = encodedText.length();
        final String text = decode(encodedText, key);

        Map<Character, Long> letterCounts = IntStream.range(0, text.length())
                .mapToObj(text::charAt)
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        double p_n_min = 1.0 / 26;
        double result = 2 * (N - p_n_min);
        for (char c = 'A'; c <= 'Z'; c++) {
            result -= getLetterFrequency(c, N) - letterCounts.get(c);
        }

        return result / (2 * (N - p_n_min));
    }

    private static String encode(String message, Map<Character, Character> key) {
        return IntStream.range(0, message.length())
                .mapToObj(message::charAt)
                .map(key::get)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private static String decode(String encodedMessage, BidiMap<Character, Character> key) {
        return IntStream.range(0, encodedMessage.length())
                .mapToObj(encodedMessage::charAt)
                .map(key::getKey)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private static BidiMap<Character, Character> randomSubstitutionKey() {
        final List<Character> letters = getLetters();
        final List<Character> lettersCopy = new ArrayList<>(letters);
        return letters.stream().collect(Collectors.toMap(c -> c, c -> {
            int substIndex = random.nextInt(lettersCopy.size());
            char subst = lettersCopy.get(substIndex);
            lettersCopy.remove(substIndex);
            return subst;
        }, (a, b) -> {
            throw new IllegalStateException("Collision in map");
        }, DualHashBidiMap::new));
    }

    private static List<Character> getLetters() {
        return IntStream.rangeClosed('A', 'Z').mapToObj(i -> (char) i).collect(Collectors.toList());
    }

    private static double getLetterFrequency(char c, int N) {
        return getLetterFrequency(c) / 100 * N;
    }

    private static double getLetterFrequency(char c) {
        switch (c) {
            case 'E': return 11.1607;
            case 'M': return 3.0129;
            case 'A': return 8.4966;
            case 'H': return 3.0034;
            case 'R': return 7.5809;
            case 'G': return 2.4705;
            case 'I': return 7.5448;
            case 'B': return 2.0720;
            case 'O': return 7.1635;
            case 'F': return 1.8121;
            case 'T': return 6.9509;
            case 'Y': return 1.7779;
            case 'N': return 6.6544;
            case 'W': return 1.2899;
            case 'S': return 5.7351;
            case 'K': return 1.1016;
            case 'L': return 5.4893;
            case 'V': return 1.0074;
            case 'C': return 4.5388;
            case 'X': return 0.2902;
            case 'U': return 3.6308;
            case 'Z': return 0.2722;
            case 'D': return 3.3844;
            case 'J': return 0.1965;
            case 'P': return 3.1671;
            case 'Q': return 0.1962;

        }
        throw new IllegalStateException("Unknown letter " + c);
    }
}
