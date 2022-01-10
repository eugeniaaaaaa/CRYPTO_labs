package crypto.lab3.random;

import crypto.lab3.RemoteService;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class LcgBreaker extends AbstractBreaker {
    private double a = 0;
    private double c = 1;
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
    }

    private long predictNextNumber(long prevNumber) {
        return (int)(prevNumber * a + c) % m;
    }

    private long betAndGetAccountMoney(int amountOfMoney, int number) {
        return getRemoteService().play(amountOfMoney, number).getAccount().getMoney();
    }

    private long requestNextNumber() {
        return getRemoteService().play(1, 1).getRealNumber();
    }
}
