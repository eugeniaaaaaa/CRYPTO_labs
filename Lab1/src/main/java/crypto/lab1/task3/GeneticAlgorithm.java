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
    private final int crossoverPointsCount = 5;
    private final double mutationProbability = 0.2;
    private final double elitismPercentage = 0.15;
    private final int terminate = 100;

    // Other parameters
    private Map<String, Integer> trigramFrequency;

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

    public void run(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalStateException("Invalid cipher text");
        }
        this.letterCase = new boolean[cipherText.length()];
        for (int i = 0; i < cipherText.length(); i++) {
            char c = cipherText.charAt(i);
            letterCase[i] = Character.isLowerCase(c) && Character.isAlphabetic(c);
        }
        this.cipherText = cipherText.toUpperCase();
        this.trigramFrequency = getNgramFrequency("data/tri-ngramFrequency.csv");

        List<String> population = initialization();

        double highestFitness = 0;
        int stuckCounter = 0;
        for (int i = 0; i <= generations; i++) {
            List<Double> fitness = evaluation(population);
            List<String> elitistPopulation = elitism(population, fitness);
            List<String> crossoverPopulation = reproduction(population, fitness);
            population.clear();
            population.addAll(elitistPopulation);
            population.addAll(crossoverPopulation);

            double maxFitness = fitness.stream().max(Comparator.comparingDouble(d -> d)).orElseThrow(IllegalStateException::new);
            if (highestFitness == maxFitness) {
                stuckCounter++;
            } else {
                stuckCounter = 1;
            }

            if (stuckCounter >= this.terminate) {
                throw new IllegalStateException("Got stuck too many times, terminating");
            }

            highestFitness = maxFitness;
            double averageFitness = fitness.stream().mapToDouble(d -> d).sum() / populationSize;

            int index = fitness.indexOf(highestFitness);
            String key = population.get(index);
            String decryptedText = decrypt(key);

            System.out.println(" ---> Generation No " + i);
            System.out.println("Average Fitness: " + averageFitness);
            System.out.println("Max fitness: " + highestFitness);
            System.out.println("Key: " + key);
            System.out.println("Decrypted text:\n" + convertToPlainText(decryptedText) + "\n");
        }
    }

    @SneakyThrows
    private Map<String, Integer> getNgramFrequency(String fileName) {
        return Files.lines(Paths.get(fileName))
                .collect(Collectors.toMap(line -> line.split(",")[0], line -> Integer.valueOf(line.split(",")[1])));
    }

    private Stream<String> generateNgrams(String word, int n) {
        return IntStream.range(0, word.length() - n + 1)
                .mapToObj(i -> word.substring(i, i + n))
                .filter(str -> str.chars().allMatch(Character::isAlphabetic));
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

    private double calculateKeyFitness(String text) {
        return generateNgrams(text, 3)
                .map(trigramFrequency::get)
                .filter(Objects::nonNull)
                .mapToDouble(GeneticAlgorithm::log2)
                .sum();
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
        return population.stream()
                .map(this::decrypt)
                .map(this::calculateKeyFitness)
                .collect(Collectors.toList());
    }

    private List<String> elitism(List<String> population, List<Double> fitness) {
        return IntStream.range(0, populationSize)
                .boxed()
                .collect(Collectors.toMap(population::get, fitness::get, (a, b) -> a)).entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(elitismCount)
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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

            List<String> tournamentKeys = tournamentPopulation.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
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

    private List<String> reproduction(List<String> population, List<Double> fitness) {
        return mutation(IntStream.range(0, (crossoverCount + 1) / 2)
                .mapToObj(i -> tournamentSelection(population, fitness))
                .flatMap(parents -> Stream.of(mergeKeys(parents.a, parents.b), mergeKeys(parents.b, parents.a)))
                .collect(Collectors.toList()));
    }

    private List<String> mutation(List<String> population) {
        return population.stream()
                .map(key -> (random.nextDouble() < mutationProbability)
                        ? mutateKey(key)
                        : key)
                .collect(Collectors.toList());
    }

    private boolean[] letterCase;

    private String convertToPlainText(String decryptedText) {
        char[] plainText = decryptedText.toCharArray();
        return IntStream.range(0, decryptedText.length())
                .mapToObj(i -> letterCase[i] ? Character.toLowerCase(plainText[i]) : plainText[i])
                .map(String::valueOf)
                .collect(Collectors.joining());
    }


    private static int log2(int N) {
        return (int) (Math.log(N) / Math.log(2));
    }
}
