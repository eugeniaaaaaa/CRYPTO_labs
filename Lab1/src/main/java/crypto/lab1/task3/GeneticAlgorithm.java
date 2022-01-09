package crypto.lab1.task3;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneticAlgorithm {
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
    private final String letters = IntStream.rangeClosed('A', 'Z').mapToObj(i -> (char)i)
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
}
