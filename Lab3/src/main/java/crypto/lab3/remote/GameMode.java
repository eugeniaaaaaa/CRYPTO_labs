package crypto.lab3.remote;

public enum GameMode {
    LCG("Lgc"), MT("Mt"), BETTER_MT("BetterMt");

    private final String value;

    GameMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
