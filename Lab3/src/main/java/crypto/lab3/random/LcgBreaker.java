package crypto.lab3.random;

import crypto.lab3.RemoteService;

import java.math.BigInteger;
import java.util.Arrays;
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
        List<Long> Xs = //IntStream.range(0, 4).mapToObj(i -> requestNextNumber()).collect(Collectors.toList());
                Arrays.asList(1895032804L, -864382477L, -1249235274L, 316003997L);
        Set<Long> possibleAs = computePossibleAs(Xs);
        findAndSetCorrectConstants(possibleAs, Xs);

        long lastNumber = Xs.get(Xs.size() - 1);
        do {
            lastNumber = predictNextNumber(lastNumber);
        } while (betAndGetAccountMoney(100, lastNumber) < 1_000_000);
    }

    private Set<Long> computePossibleAs(List<Long> Xs) {
        if (Xs.size() < 3) {
            throw new IllegalStateException("Not enough arguments to predict");
        }
        return IntStream.range(0, Xs.size() - 2)
                .mapToObj(i -> new LongTuple3(Xs.get(i), Xs.get(i + 1), Xs.get(i + 2)))
                .map(tuple -> LongStream.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
                        .filter(z -> 0 == (z * m + tuple.X2 - tuple.X3) % (tuple.X1 - tuple.X2))
                        .mapToObj(z -> (z * m + tuple.X2 - tuple.X3) / (tuple.X1 - tuple.X2))
                        .collect(Collectors.toSet()))
                .reduce((set1, set2) -> {
                    set1.retainAll(set2);
                    return set1;
                }).orElseThrow(IllegalStateException::new);
    }

    private void findAndSetCorrectConstants(Set<Long> possibleAs, List<Long> Xs) {
        class AC {
            final long a;
            final long c;

            public AC(long a, long c) {
                this.a = a;
                this.c = c;
            }
        }

        // actual C for bounds to decrease time of computation
        final long actualC = 1013904223L;
        final long fromC = Integer.MIN_VALUE;
        final long toC = Integer.MAX_VALUE;

        AC ac = possibleAs.stream()
                .flatMap(possibleA -> LongStream.rangeClosed(fromC, toC)
                        .parallel()
                        .filter(possibleC -> {
                            for (int i = 1; i < Xs.size(); i++) {
                                if (predictNextNumber(Xs.get(i - 1), possibleA, possibleC) != Xs.get(i)) {
                                    return false;
                                }
                            }
                            return true;
                        })
                        .mapToObj(possibleC -> new AC(possibleA, possibleC)))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        this.a = ac.a;
        this.c = ac.c;
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
