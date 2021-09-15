package engine;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NRandom {

    // The random number generator used throughout the course of evolution
    private static final Random random = ThreadLocalRandom.current();

    /**
     * Random weight generator. Generates random doubles in the range [weightRangeMin, weightRangeMax[
     * weightRangeMin must be less than weightRangeMax, otherwise their values will be swapped.
     *
     * @return A random double in the range specified by the parameters
     */
    public static double getRandomWeight(double min, double max) {
        if (min < max)
            return (Math.abs(min - max)) * random.nextDouble() + min;
        else
            return (Math.abs(max - min)) * random.nextDouble() + max;
    }

    /**
     * Generates a random double in the range [0, 1[
     * @return A random double
     */
    public static double getRandomDouble() {
        // return random.nextDouble();
        double gaussian = random.nextGaussian();
        if (gaussian >= 1 || gaussian < 0)
            return random.nextDouble();
        return gaussian;
    }

    /**
     * Return a random integer in the range [0, bound[
     * @param bound Value of the upper bound
     * @return A random integer
     */
    public static int getRandomInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Returns a random boolean
     * @return random boolean value
     */
    public static boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    /**
     * Returns the random number generator
     * @return Random generator
     */
    public static Random getRandom() {
        return random;
    }

}
