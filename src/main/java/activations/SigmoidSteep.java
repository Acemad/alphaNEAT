package activations;

/**
 * Sigmoid activation function.
 * @author Acemad
 */
public class SigmoidSteep extends ActivationFunction {

    /**
     * Apply the sigmoid function on the given input
     * @param value Sum of incoming activations
     * @return
     */
    @Override
    public double apply(double value) {
        return (1 / (1 + Math.exp(-4.924273 * value)));
    }

    @Override
    public String shortCode() {
        return "ss";
    }

}
