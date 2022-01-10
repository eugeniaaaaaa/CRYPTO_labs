package crypto.lab3;

import crypto.lab3.random.LcgBreaker;
import crypto.lab3.random.MtBreaker;
import crypto.lab3.random.RandomBreaker;
import crypto.lab3.remote.GameMode;

import java.util.Random;

public class Startup {
    public static void main(String[] args) {
        RemoteService service = new RemoteService("http://95.217.177.249/casino", 5434, GameMode.LCG);
//        RandomBreaker breaker = new LcgBreaker(service);
        RandomBreaker breaker = new MtBreaker(service);
        breaker.becomeRich();
    }
}
