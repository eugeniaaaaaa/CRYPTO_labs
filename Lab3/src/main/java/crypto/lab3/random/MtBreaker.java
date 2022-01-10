package crypto.lab3.random;

import crypto.lab3.RemoteService;
import crypto.lab3.utils.Mt19937Random;

import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class MtBreaker extends AbstractBreaker {
    public MtBreaker(RemoteService remoteService) {
        super(remoteService);
    }

    @Override
    public void becomeRich() {
        final long start = System.currentTimeMillis() / 1000;
        final long realNumber = getRemoteService().play(1, 1L).getRealNumber();
        final long end = System.currentTimeMillis() / 1000;

        final int marginError = 10;
        Mt19937Random mtOnServer = LongStream.range(start - marginError, end + marginError)
                .mapToInt(i -> (int) i)
                .mapToObj(Mt19937Random::new)
                .filter(mt -> realNumber == mt.next())
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        long money;
        do {
            long next = mtOnServer.next();
            money = getRemoteService().play(100, next).getAccount().getMoney();
        } while (money < 1_000_000);
    }
}
