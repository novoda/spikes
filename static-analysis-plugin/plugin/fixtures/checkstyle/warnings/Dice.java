import java.util.Random;

public final class Dice {
    private final Random random;

    public Dice() {
        random = new Random();
    }

    public int roll() {
        return random.nextInt(6) + 1; // magic number violation
    }
}
