package crypto.lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Task3 {
    public static void main(String[] args) {
        Map<Character, Character> letterSubstitutions = getLetterSubstitutions();
        System.out.println(letterSubstitutions);
    }

    private static Map<Character, Character> getLetterSubstitutions() {
        final Random random = new Random();
        final List<Character> letters = IntStream.rangeClosed('A', 'Z').mapToObj(i -> (char)i).collect(Collectors.toList());
        final List<Character> lettersCopy = new ArrayList<>(letters);
        System.out.println(letters);
        return letters.stream().collect(Collectors.toMap(c -> c, c -> {
            int substIndex = random.nextInt(lettersCopy.size());
            char subst = lettersCopy.get(substIndex);
            lettersCopy.remove(substIndex);
            return subst;
        }));
    }
}
