package crypto.lab3.random;

import crypto.lab3.RemoteService;
import crypto.lab3.utils.Mt19937Random;

import java.util.stream.LongStream;

public class MtBreaker extends AbstractBreaker {
    public MtBreaker(RemoteService remoteService) {
        super(remoteService);
    }

    @Override
    public void becomeRich() {
        getRemoteService().createAccount();
        final long start = System.currentTimeMillis() / 1000;
        final long realNumber = getRemoteService().play(1, 1).getRealNumber();
        final long end = System.currentTimeMillis() / 1000;

        final int marginError = 10;
        Mt19937Random serverMt = LongStream.range(start - marginError, end + marginError)
                .mapToInt(i -> (int) i)
                .mapToObj(Mt19937Random::new)
                .filter(mt -> realNumber == mt.next())
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        long money;
        do {
            long next = serverMt.next();
            money = getRemoteService().play(100, next).getAccount().getMoney();
        } while (money < 1_000_000);
    }
}
