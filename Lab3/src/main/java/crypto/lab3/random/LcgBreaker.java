package crypto.lab3.random;

import crypto.lab3.RemoteService;

import java.math.BigInteger;


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
        // X3 = X2 * a + c
        // X2 = X1 * a + c
        // Where X1, X2, X3 are known and X1 <= X2 <= X3
        // After subtraction of equations:
        // X3 - X2 = a * (X2 - X1)
        // Hence:
        // a = (X3 - X2) / (X2 - X1)
        // c = X2 - X1 * a = X3 - X2 * a
        // For getting the value of m we continue betting until out prediction does not equal real number
        // Let Xa - prediction, Xb - real number, then
        // Xb < Xa,
        // m = Xa % Xb
        // m =
        long X1 = requestNextNumber();
        long X2 = requestNextNumber();
        long X3 = requestNextNumber();

        while (!((X1 <= X2) && (X2 <= X3))) {
            X1 = X2;
            X2 = X3;
            X3 = requestNextNumber();
        }

        a = (X3 - X2) / (double)(X2 - X1);
        c = X2 - X1 * a;

        long currentNumber = X3;
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
