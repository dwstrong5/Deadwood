import java.util.ArrayList;
import java.util.Random;

public class Dice {
    // Rolls 1 die and returns a random int from 1-6 inclusively
    public static int roll() {
        Random r = new Random();

        int result = r.nextInt(6) + 1;

        return result;
    }

    // Rolls amount of die/dice and returns an array of rolls
    public static ArrayList<Integer> roll(int amount) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        int i = 0;

        while (i < amount) {
            results.add(roll());
            i++;
        }

        return results;
    }
}
