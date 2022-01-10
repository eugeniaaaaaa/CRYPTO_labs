package crypto.lab3.random;

import crypto.lab3.RemoteService;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;


public class LcgBreaker extends AbstractBreaker {
    private static class LongTuple3 {
        long X1, X2, X3;

        public LongTuple3(long x1, long x2, long x3) {
            X1 = x1;
            X2 = x2;
            X3 = x3;
        }
    }

    private long a = 0;
    private long c = 1;
    private final long m = BigInteger.valueOf(2).pow(32).longValue();

    public LcgBreaker(RemoteService remoteService) {
        super(remoteService);
    }

    @Override
    public void becomeRich() {
//        getRemoteService().createAccount();
        // X(i+1) = (X(i) * a + c) % m
        // Solution:
        // X2 = (a * X1 + c) % m = (m * z1 + X2) % m
        // Assume that:
        // a * X1 + c = m * z1 + X2
        // This assumption will lead to several possible values of 'a', only one of them is correct
        // X3 = (a * X2 + c) % m = (m * z2 + X3) % m
        // Same assumption:
        // a * X2 + c = m * z2 + X3
        // Let's subtract the equations:
        // (a * X1 + c) - (a * X2 + c) = (m * z1 + X2) - (m * z2 + X3)
        // a * X1 + c - a * X2 - c = m * z1 + X2 - m * z2 - X3
        // a = ((z1 -z2) * m + X2 - X3) / (X1 - X2)
        // Let (z1 -z2) = Z:
        // a = (Z * m + x2 - x3) / (x1 - x2)
        // So, our task is to find such 'Z' that (Z * m + x2 - x3) % (x1 - x2) == 0
        // We need 3 values to make assumptions and one more to find which values are correct:
        List<Long> Xs = IntStream.range(0, 4).mapToObj(i -> requestNextNumber()).collect(Collectors.toList());
        Set<Long> possibleAs = computePossibleAs(Xs);
        findAndSetCorrectConstants(possibleAs, Xs);
//        this.a = 4296631821L;
//        this.c = 1013904223L;

        long lastNumber = Xs.get(Xs.size() - 1);
        do {
            lastNumber = predictNextNumber(lastNumber);
        } while (betAndGetAccountMoney(100, lastNumber) < 1_000_000);
    }

    private Set<Long> computePossibleAs(List<Long> Xs) {
        if (Xs.size() < 3) {
            throw new IllegalStateException("Not enough arguments to predict");
        }

        // Only use first 3 entries for computation
        long X1 = Xs.get(0);
        long X2 = Xs.get(1);
        long X3 = Xs.get(2);

        Set<Long> possibleAs = new HashSet<>();
        for (long z = Integer.MIN_VALUE; z < Integer.MAX_VALUE; z++) {
            if (0 == (z * m + X2 - X3) % (X1 - X2)) {
                possibleAs.add((z * m + X2 - X3) / (X1 - X2));
            }
        }
        return possibleAs;
    }

    private void findAndSetCorrectConstants(Set<Long> possibleAs, List<Long> Xs) {
        long[] XsArray = Xs.stream().mapToLong(i -> i).toArray();
        for (long possibleA : possibleAs) {
            for (long possibleC = Integer.MIN_VALUE; possibleC <= Integer.MAX_VALUE; possibleC++) {
                boolean fits = true;
                for (int i = 1; i < Xs.size(); i++) {
                    if (predictNextNumber(XsArray[i - 1], possibleA, possibleC) != XsArray[i]) {
                        fits = false;
                        break;
                    }
                }
                if (fits) {
                    this.a = possibleA;
                    this.c = possibleC;
                    return;
                }
            }
        }
    }

    private long predictNextNumber(long prevNumber) {
        return predictNextNumber(prevNumber, a, c);
    }

    private long predictNextNumber(long prevNumber, long a, long c) {
        return (a * prevNumber + c) % m;
    }

    private long betAndGetAccountMoney(int amountOfMoney, long number) {
        return getRemoteService().play(amountOfMoney, number).getAccount().getMoney();
    }

    private long requestNextNumber() {
        return getRemoteService().play(1, 1).getRealNumber();
    }
}
