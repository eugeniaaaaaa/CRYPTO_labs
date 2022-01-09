package crypto.lab1;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Task2 {
    public static void main(String[] args) {
        final String text = "Now the first one who will post the link to this document to our chat will receive +1 score. Simple.";
        final String key = "key";
        System.out.println(crack(Utils.encode(text, key)));
    }

    private static String crack(byte[] encoded) {
        final int messageLength = encoded.length;
        final int keyLength = findKeyLength(encoded);
        final byte[][] parts = split(encoded, keyLength);

        return null;
    }

    private static byte[][] split(byte[] bytes, int partSize) {
        final int messageLength = bytes.length;
        byte[][] splitResult = new byte[partSize][];
        for (int i = 0; i < partSize; i++) {
            int shardLength = messageLength / partSize;
            if (messageLength - shardLength * partSize > i) {
                shardLength++;
                System.out.println(i);
            }
            splitResult[i] = new byte[shardLength];
            for (int j = 0; j < shardLength; j++) {
                splitResult[i][j] = bytes[i + j * partSize];
            }
        }

        return splitResult;
    }

    private static final int someThreshold = 20;
    private static int findKeyLength(byte[] encoded) {
        final int messageLength = encoded.length;
        final int coincidenceIndicesCount = Math.min(someThreshold, messageLength);
        final double freqThreshold = 0.02;
        List<Integer> topIndices = IntStream.range(0, coincidenceIndicesCount)
                .filter(i -> randomEqualProb(Utils.encode(subarray(encoded, 0), subarray(encoded, i)), messageLength - i) > freqThreshold)
                .boxed()
                .collect(Collectors.toList());

        return mostProbableLength(topIndices);
    }

    private static int mostProbableLength(List<Integer> topIndices) {
        return topIndices.stream()
                .flatMap(i1 -> topIndices.stream().map(i2 -> Math.abs(i2 - i1)))
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("Cannot find most frequent length"));
    }

    private static byte[] subarray(byte[] bytes, int i) {
        int len = bytes.length;
        byte[] temp = new byte[len - i];
        System.arraycopy(bytes, i, temp, 0, len - i);
        return temp;
    }

    private static double randomEqualProb(byte[] bytes, int N) {
        final int[] symbolCounts = new int[256];
        for (int i = 0; i < N; i++) {
            symbolCounts[bytes[i] - Byte.MIN_VALUE]++;
        }
        // Index of coincidence
        double I = 0;
        for (int i = 0; i < 256; i++) {
            I += symbolCounts[i] * (symbolCounts[i] - 1) / (double) (N * (N - 1));
        }

        return I;
    }
}