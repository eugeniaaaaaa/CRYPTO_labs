package crypto.lab3.random;

import crypto.lab3.RemoteService;

public abstract class AbstractBreaker implements RandomBreaker{
    private final RemoteService remoteService;

    public AbstractBreaker(RemoteService remoteService) {
        this.remoteService = remoteService;
    }

    protected RemoteService getRemoteService() {
        return remoteService;
    }
}
