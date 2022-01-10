package crypto.lab3;

import crypto.lab3.random.Mt19937Breaker;
import crypto.lab3.random.MtBreaker;
import crypto.lab3.random.RandomBreaker;
import crypto.lab3.remote.GameMode;

public class Startup {
    public static void main(String[] args) {
        RemoteService service = new RemoteService("http://95.217.177.249/casino", 5439, GameMode.BETTER_MT);
//        RandomBreaker breaker = new LcgBreaker(service);
        RandomBreaker breaker = new Mt19937Breaker(service);
        breaker.becomeRich();
    }
}
