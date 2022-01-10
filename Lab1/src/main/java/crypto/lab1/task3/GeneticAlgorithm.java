package crypto.lab1.task3;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeneticAlgorithm {
    private static class Pair<A, B> {
        A a;
        B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    private final Random random = new Random();
    // Genetic algorithm parameters
    private final int generations = 500;
    private final int populationSize = 500;
    private final int tournamentSize = 20;
    private final double tournamentWinnerProbability = 0.75;
    private final double crossoverProbability = 0.65;
    private final int crossoverPointsCount = 5;
    private final double mutationProbability = 0.2;
    private final double elitismPercentage = 0.15;
    private final String selectionMethod = "TS";
    private final int terminate = 100;

    // Other parameters
    private final double bigramWeight = 0;
    private final double trigramWeight = 1;

    //
    private final String letters = IntStream.rangeClosed('A', 'Z').mapToObj(i -> (char) i)
            .map(String::valueOf)
            .collect(Collectors.joining());
    private final int elitismCount = (int) (elitismPercentage * populationSize);
    private final int crossoverCount = populationSize - elitismCount;

    private final List<Double> tournamentProbabilities = new ArrayList<>();

    public GeneticAlgorithm() {
        tournamentProbabilities.add(tournamentWinnerProbability);
        double probability = tournamentWinnerProbability;
        for (int i = 1; i < tournamentSize; i++) {
            probability = probability * (1.0 - tournamentWinnerProbability);
            tournamentProbabilities.add(probability);
        }
    }

    @SneakyThrows
    private Map<String, Integer> getNgramFrequency(String fileName) {
        return Files.lines(Paths.get(fileName))
                .collect(Collectors.toMap(line -> line.split(",")[0], line -> Integer.valueOf(line.split(",")[1])));
    }

    private List<String> generateNgrams(String word, int n) {
        return IntStream.range(0, word.length() - n + 1)
                .mapToObj(i -> word.substring(i, i + n))
                .filter(str -> str.chars().allMatch(Character::isAlphabetic))
                .collect(Collectors.toList());
    }

    private String cipherText;

    private String decrypt(String key) {
        Map<Character, Character> letterMapping = IntStream.rangeClosed('A', 'Z')
                .mapToObj(i -> (char) i)
                .collect(Collectors.toMap(i -> i, i -> key.charAt(i - 'A')));

        return cipherText.chars()
                .mapToObj(c -> letters.contains(String.valueOf((char) c)) ? letterMapping.get((char) c) : (char) c)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private Map<String, Integer> bigramFrequency;
    private Map<String, Integer> trigramFrequency;

    private double calculateKeyFitness(String text) {
        List<String> bigrams = generateNgrams(text, 2);
        List<String> trigrams = generateNgrams(text, 3);

        double bigramFitness = 0;
        if (bigramWeight > 0) {
            for (String bigram : bigrams) {
                if (bigramFrequency.containsKey(bigram)) {
                    bigramFitness += log2(bigramFrequency.get(bigram));
                }
            }
        }

        double trigramFitness = 0;
        if (trigramWeight > 0) {
            for (String trigram : trigrams) {
                if (trigramFrequency.containsKey(trigram)) {
                    trigramFitness += log2(trigramFrequency.get(trigram));
                }
            }
        }

        return bigramFitness * bigramWeight + trigramFitness * trigramWeight;
    }

    private String mergeKeys(String first, String second) {
        Character[] offspring = new Character[26];
        int count = 0;
        while (count < crossoverPointsCount) {
            int r = random.nextInt(first.length());
            if (offspring[r] == null) {
                offspring[r] = first.charAt(r);
                count++;
            }
        }

        for (char c : second.toCharArray()) {
            if (!Arrays.asList(offspring).contains(c)) {
                for (int i = 0; i < offspring.length; i++) {
                    if (offspring[i] == null) {
                        offspring[i] = c;
                        break;
                    }
                }
            }
        }

        return Stream.of(offspring).map(String::valueOf).collect(Collectors.joining());
    }

    private String mutateKey(String key) {
        int a = random.nextInt(key.length());
        int b = random.nextInt(key.length());

        char[] chars = key.toCharArray();
        char temp = chars[a];
        chars[a] = chars[b];
        chars[b] = temp;

        return new String(chars);
    }

    private List<String> initialization() {
        List<String> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            StringBuilder key = new StringBuilder();
            while (key.length() < 26) {
                int r = random.nextInt(letters.length());
                if (!key.toString().contains(String.valueOf(letters.charAt(r)))) {
                    key.append(letters.charAt(r));
                }
            }
            population.add(key.toString());
        }

        return population;
    }

    private List<Double> evaluation(List<String> population) {
        List<Double> fitness = new ArrayList<>();

        for (String key : population) {
            String decryptedText = decrypt(key);
            double keyFitness = calculateKeyFitness(decryptedText);
            fitness.add(keyFitness);
        }

        return fitness;
    }

    private List<String> elitism(List<String> population, List<Double> fitness) {
        return IntStream.range(0, populationSize)
                .boxed()
                .collect(Collectors.toMap(population::get, fitness::get)).entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(elitismCount)
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private int rouletteWheelSelection(List<Double> fitness) {
        int index = -1;
        double highestProbability = fitness.stream().max(Double::compare).orElseThrow(IllegalStateException::new);

        boolean selected = false;
        while (!selected) {
            index = random.nextInt(populationSize);
            double probability = fitness.get(index);
            double r = random.nextDouble() * highestProbability;
            selected = (r < probability);
        }

        return index;
    }

    private Pair<String, String> tournamentSelection(List<String> population, List<Double> fitness) {
        List<String> populationCopy = new ArrayList<>(population);
        List<String> selectedKeys = new ArrayList<>();

        for (int a = 0; a < 2; a++) {
            Map<String, Double> tournamentPopulation = new HashMap<>();

            for (int i = 0; i < tournamentSize; i++) {
                int r = random.nextInt(populationCopy.size());
                String key = populationCopy.get(r);
                Double keyFitness = fitness.get(r);

                tournamentPopulation.put(key, keyFitness);
                populationCopy.remove(r);
            }

            List<String> tournamentKeys  = tournamentPopulation.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            int index = -1;
            boolean selected = false;
            while (!selected) {
                index = random.nextInt(tournamentSize);
                double probability = tournamentProbabilities.get(index);

                double r = random.nextDouble() * tournamentWinnerProbability;
                selected = (r < probability);
            }
            selectedKeys.add(tournamentKeys.get(index));
        }
        return new Pair<>(selectedKeys.get(0), selectedKeys.get(1));
    }

    private String reproduction(List<String> population, List<Double> fitness) {

    }


    private static int log2(int N) {
        return (int) (Math.log(N) / Math.log(2));
    }
}
