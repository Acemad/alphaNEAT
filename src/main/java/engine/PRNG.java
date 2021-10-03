package engine;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.GaussianSampler;
import org.apache.commons.rng.sampling.distribution.MarsagliaNormalizedGaussianSampler;
import org.apache.commons.rng.sampling.distribution.ZigguratNormalizedGaussianSampler;
import org.apache.commons.rng.simple.RandomSource;

/**
 * A PRNG wrapper used as a central random number generator. The underlying generator can be changed by
 * modifying the random variable
 * @author Acemad
 */
public class PRNG {

    // The random number generator used throughout the course of evolution
    // private static final Random random = ThreadLocalRandom.current();
    private static final UniformRandomProvider random = RandomSource.XOR_SHIFT_1024_S_PHI.create();

    /**
     * Random weight generator. Generates random doubles in the range [weightRangeMin, weightRangeMax[
     * weightRangeMin must be less than weightRangeMax, otherwise their values will be swapped.
     *
     * @return A random double in the range specified by the parameters
     */
    public static double nextWeight(double min, double max) {
        if (min < max)
            return (Math.abs(min - max)) * nextDouble() + min;
        else
            return (Math.abs(max - min)) * nextDouble() + max;
    }

    /**
     * Generates a random double in the range [0, 1[
     * @return A random double
     */
    public static double nextDouble() {
        return random.nextDouble();
    }

    /**
     * Generates a random double from the gaussian distribution defined by mean=0 and the given sigma
     * @param sigma Standard deviation of the gaussian distribution
     * @return A random double sampled from the normal distribution
     */
    public static double nextGaussian(double sigma) {
        ContinuousSampler sampler = GaussianSampler.of(ZigguratNormalizedGaussianSampler.of(random), 0, sigma);
        return sampler.sample();
    }

    /**
     * Return a random integer in the range [0, bound[
     * @param bound Value of the upper bound
     * @return A random integer
     */
    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Returns a random boolean
     * @return random boolean value
     */
    public static boolean nextBoolean() {
        return random.nextBoolean();
    }

}
