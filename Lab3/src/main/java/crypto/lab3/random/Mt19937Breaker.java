package crypto.lab3.random;

import crypto.lab3.RemoteService;
import crypto.lab3.utils.Mt19937Random;

public class Mt19937Breaker extends AbstractBreaker{
    public Mt19937Breaker(RemoteService remoteService) {
        super(remoteService);
    }

    @Override
    public void becomeRich() {
        final int[] seedArray = new int[624];
        for (int i = 0; i < seedArray.length; i++) {
            long realNumber = getRemoteService().play(1, 1).getRealNumber();
            seedArray[i]=(int) realNumber;
        }

        Mt19937Random mtOnServer = new Mt19937Random(seedArray);


        long money;
        do {
            long next = mtOnServer.next();
            money = getRemoteService().play(100, next).getAccount().getMoney();
        } while (money < 1_000_000);
        System.out.println("money=" + money);
    }
}
