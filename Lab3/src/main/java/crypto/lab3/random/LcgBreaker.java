package crypto.lab3.random;

import crypto.lab3.RemoteService;

public class LcgBreaker extends AbstractBreaker{
    public LcgBreaker(RemoteService remoteService) {
        super(remoteService);
    }

    @Override
    public void becomeRich() {

    }
}
