package crypto.lab3.random;

import crypto.lab3.RemoteService;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;


public class LcgBreaker extends AbstractBreaker {
    private static class AC {
        long a, c;
        public AC(long a, long c) {
            this.a = a;
            this.c = c;
        }
    }

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
        List<Integer> Xs = IntStream.range(0, 4)
                .mapToObj(i -> (int) requestNextNumber())
                .collect(Collectors.toList());

        Set<Long> aCandidates = computePossibleAs(Xs);
        AC ac = findCorrectConstants(aCandidates, Xs);

        int lastNumber = Xs.get(Xs.size() - 1);
        do {
            lastNumber = calculateNext(lastNumber, ac.a, ac.c);
        } while (betAndGetAccountMoney(lastNumber) < 1_000_000);
    }

    private Set<Long> computePossibleAs(List<Integer> results) {
        return IntStream.range(1, results.size() - 1)
                .parallel()
                .mapToObj(i -> {
                    int prevResult = results.get(i - 1);
                    int result = results.get(i);
                    int nextRes = results.get(i + 1);
                    Set<Long> As = new HashSet<>();
                    for (long z = Integer.MIN_VALUE; z < Integer.MAX_VALUE; z++) {
                        long aCandidateMod = (z * m + result - nextRes) % (prevResult - result);
                        if (aCandidateMod == 0) {
                            As.add((z * m + result - nextRes) / (prevResult - result));
                        }
                    }
                    return As;
                }).reduce((set1, set2) -> {
                    HashSet<Long> result = new HashSet<>(set1);
                    result.retainAll(set2);
                    return result;
                })
                .orElseThrow(IllegalStateException::new);
    }

    private AC findCorrectConstants(Set<Long> aCandidates, List<Integer> results) {
        return LongStream.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
                .parallel()
                .mapToObj(c -> {
                    for (long a : aCandidates) {
                        boolean allCorrect = true;
                        for (int i = 1; i < results.size(); i++) {
                            int prevResult = results.get(i - 1);
                            int result = results.get(i);
                            if (calculateNext(prevResult, a, c) % m != result) {
                                allCorrect = false;
                                break;
                            }
                        }
                        if (allCorrect) {
                            return new AC(a, c);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private int calculateNext(int last, long a, long c) {
        long ret = (a * last + c) % m;
        return (int) ret;
    }

    private long betAndGetAccountMoney(long number) {
        return getRemoteService().play(100, number).getAccount().getMoney();
    }

    private long requestNextNumber() {
        return getRemoteService().play(1, 1).getRealNumber();
    }
}
