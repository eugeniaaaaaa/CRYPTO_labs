package crypto.lab1;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Task3 {
    private static final Random random = new Random();

    public static void main(String[] args) {
        final BidiMap<Character, Character> key = randomSubstitutionKey();
        System.out.println(key);

        List<Map<Character, Character>> initialKeys = Stream.generate(Task3::randomSubstitutionKey).limit(7).collect(Collectors.toList());
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
}
